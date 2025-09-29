package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.core.file.FileIOService;
import kr.me.seesaw.domain.*;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.model.AttachmentModel;
import kr.me.seesaw.model.ReplyModel;
import kr.me.seesaw.model.ViewModel;
import kr.me.seesaw.repository.*;
import kr.me.seesaw.search.ArticleSearch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class DefaultArticleService implements ArticleService {

    @Value("${kr.me.seesaw.filepath}")
    private final String filepath;

    private final ArticleRepository articleRepository;

    private final ArticleSearchRepository articleSearchRepository;

    private final ReplyRepository replyRepository;

    private final ViewRepository viewRepository;

    private final AttachmentRepository attachmentRepository;

    private final EntityManager entityManager;

    private final PrincipalProvider principalProvider;

    public DefaultArticleService(
            @Value("${kr.me.seesaw.filepath}") String filepath,
            ArticleRepository articleRepository,
            ArticleSearchRepository articleSearchRepository,
            ReplyRepository replyRepository,
            ViewRepository viewRepository,
            AttachmentRepository attachmentRepository,
            EntityManager entityManager,
            PrincipalProvider principalProvider
    ) {
        this.filepath = filepath;
        this.articleRepository = articleRepository;
        this.articleSearchRepository = articleSearchRepository;
        this.replyRepository = replyRepository;
        this.viewRepository = viewRepository;
        this.attachmentRepository = attachmentRepository;
        this.entityManager = entityManager;
        this.principalProvider = principalProvider;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ArticleModel> findAll(Pageable pageable, ArticleSearch search) {
        Page<Article> entityPage = articleSearchRepository.search(pageable, search);
        Page<ArticleModel> page = entityPage.map(ArticleModel::new);

        // 페이지 요청이 아닐 경우 조인하지 않는다.
        if (pageable.isUnpaged()) {
            return page;
        }

        List<String> articleIds = page.getContent()
                .stream()
                .map(ArticleModel::getId)
                .toList();

        List<ReplyModel> replies = replyRepository.findAllByArticleIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(ReplyModel::new)
                .toList();
        page.getContent()
                .forEach(article -> article.joinReplies(replies));

        List<ViewModel> views = viewRepository.findAllByArticleIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(ViewModel::new)
                .toList();
        page.getContent()
                .forEach(article -> article.joinViews(views));

        List<AttachmentModel> attachments = attachmentRepository.findAllByReferenceIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(AttachmentModel::new)
                .toList();
        page.getContent()
                .forEach(article -> article.joinAttachments(attachments));
        return page;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ArticleModel> findAllByCategoryId(String categoryId, Pageable pageable) {
        Page<Article> entityPage = articleRepository.findAllByCategoryId(categoryId, pageable);
        Page<ArticleModel> page = entityPage.map(ArticleModel::new);

        // 페이지 요청이 아닐 경우 조인하지 않는다.
        if (pageable.isUnpaged()) {
            return page;
        }
        List<String> articleIds = page.getContent().stream().map(ArticleModel::getId).toList();

        List<ReplyModel> replies = replyRepository.findAllByArticleIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(ReplyModel::new)
                .toList();
        page.getContent().forEach(article -> article.joinReplies(replies));

        List<ViewModel> views = viewRepository.findAllByArticleIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(ViewModel::new)
                .toList();
        page.getContent().forEach(article -> article.joinViews(views));

        List<AttachmentModel> attachments = attachmentRepository.findAllByReferenceIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(AttachmentModel::new)
                .toList();
        page.getContent().forEach(article -> article.joinAttachments(attachments));
        return page;
    }

    @Transactional(readOnly = true)
    @Override
    public ArticleModel find(String id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));
        return new ArticleModel(article);
    }

    @Transactional(readOnly = true)
    public ArticleModel getArticleAggregation(String id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));
        ArticleModel model = new ArticleModel(article);

        List<ReplyModel> replies = replyRepository.findAllByArticleIdIn(Collections.singletonList(id))
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(ReplyModel::new)
                .toList();
        model.joinReplies(replies);

        List<ViewModel> views = viewRepository.findAllByArticleIdIn(Collections.singletonList(id))
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(ViewModel::new)
                .toList();
        model.joinViews(views);

        List<AttachmentModel> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(id))
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(AttachmentModel::new)
                .toList();
        model.joinAttachments(attachments);
        return model;
    }

    @Override
    public ArticleModel create(CreateArticleCommand command) throws IOException {

        // 생성될 게시글의 식별자를 참조하기위해 먼저 게시글을 저장한다.
        Article article = Article.create(command);
        articleRepository.save(article);

        Document document = Jsoup.parse(command.getContent());
        Iterator<Element> iterator = document.select("img[src*=\"blob:\"]").iterator();

        for (MultipartFile multipartFile : command.getInlineImages()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.INLINE_IMAGE, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);

            String url = "/api/attachments/" + attachment.getId();

            // images의 src를 첨부파일을 생성 후 "/api/attachments/{id}"로 치환한다.
            if (iterator.hasNext()) {
                Element element = iterator.next();
                element.attr("src", url);
            }
        }

        // 첨부파일
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.ATTACHMENT, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);
        }

        // 인라인이미지 링크를 첨부파일 API로 치환한 본문으로 재할당한다
        Safelist safelist = Safelist.relaxed().preserveRelativeLinks(true);
        article.setContent(Jsoup.clean(document.body().html(), "http://localhost", safelist));
        articleRepository.save(article);
        return new ArticleModel(article);
    }

    @Override
    public ArticleModel update(String id, UpdateArticleCommand command) throws IOException {
        Article article = Optional.ofNullable(entityManager.getReference(Article.class, id))
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));

        Document existingContent = Jsoup.parse(article.getContent());
        Elements existingImages = existingContent.select("img[src*=\"/api/attachments/\"]");

        List<String> deletedAttachmentIds = new ArrayList<>();

        Document newContent = Jsoup.parse(command.getContent());
        Elements remainingImages = newContent.select("img[src*=\"/api/attachments/\"]");

        // src는 "/"로 스플릿하여 마지막 요소를 uuid4 패턴의 문자열이다.
        for (Element existingImage : existingImages) {
            String existingSrc = existingImage.attr("src");
            boolean isPresentInNewImages = remainingImages.stream()
                    .anyMatch(newImage -> newImage.attr("src").equals(existingSrc));
            if (!isPresentInNewImages) {
                deletedAttachmentIds.add(existingSrc.substring(existingSrc.lastIndexOf("/") + 1));
            }
        }

        // 수정하면서 삭제한 이미지를 삭제
        List<Attachment> attachments = attachmentRepository.findAllByIdIn(deletedAttachmentIds);
        attachmentRepository.deleteAllInBatch(attachments);
        attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

        Iterator<Element> iterator = newContent.select("img[src*=\"blob:\"]").iterator();

        for (MultipartFile multipartFile : command.getInlineImages()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.INLINE_IMAGE, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);

            String url = "/api/attachments/" + attachment.getId();

            // images의 src를 첨부파일을 생성 후 "/api/attachments/{id}"로 치환한다.
            if (iterator.hasNext()) {
                Element element = iterator.next();
                element.attr("src", url);
            }
        }

        Safelist safelist = Safelist.relaxed().preserveRelativeLinks(true);
        command.setContent(Jsoup.clean(newContent.body().html(), "http://localhost", safelist));
        article.update(command);
        articleRepository.save(article);

        // 첨부파일
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.ATTACHMENT, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);
        }
        return new ArticleModel(article);
    }

    @Override
    public void delete(String id) {
        Article article = Optional.ofNullable(entityManager.getReference(Article.class, id))
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));
        List<Reply> replies = replyRepository.findAllByArticleIdIn(Collections.singletonList(article.getId()));
        replyRepository.deleteAllInBatch(replies);
        List<View> views = viewRepository.findAllByArticleIdIn(Collections.singletonList(article.getId()));
        viewRepository.deleteAllInBatch(views);
        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(article.getId()));
        attachmentRepository.deleteAllInBatch(attachments);
        attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

        articleRepository.delete(article);
    }

    @Override
    public void deleteAll(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    public boolean isOwner(String id, String username) {
        ArticleModel article = find(id);
        return article.getCreatedBy().equals(username);
    }

    @Override
    public List<ArticleModel> getFixedArticles(String categoryId, boolean fixed, Sort sort) {
        return articleRepository.findAllByCategoryIdAndFixed(categoryId, fixed, sort)
                .stream()
                .map(ArticleModel::new)
                .toList();
    }

    @Nullable
    @Override
    public ArticleModel getPreviousArticle(ArticleSearch search, LocalDateTime createdDate) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        return articleSearchRepository.findFirstNext(search, createdDate, sort).map(ArticleModel::new).orElse(null);
    }

    @Nullable
    @Override
    public ArticleModel getNextArticle(ArticleSearch search, LocalDateTime createdDate) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        return articleSearchRepository.findFirstNext(search, createdDate, sort).map(ArticleModel::new).orElse(null);
    }

    private void writeFile(String pathname, byte[] bytes) {
        try {
            FileIOService.write(pathname, bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void increaseView(String articleId) {
        View view = View.create(articleId);
        Object principal = principalProvider.getAuthentication().getPrincipal();
        // 동일인물 중복 조회수 불허
        List<View> views = viewRepository.findAllByArticleIdIn(Collections.singletonList(articleId));
    }

}
