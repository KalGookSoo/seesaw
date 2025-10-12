package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.core.file.FileIOService;
import kr.me.seesaw.domain.*;
import kr.me.seesaw.model.*;
import kr.me.seesaw.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class DefaultSiteService implements SiteService {

    private final String filepath;

    private final SiteRepository siteRepository;

    private final AttachmentRepository attachmentRepository;

    private final ArticleSearchRepository articleSearchRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    public DefaultSiteService(
            @Value("${kr.me.seesaw.filepath}") String filepath,
            SiteRepository siteRepository,
            AttachmentRepository attachmentRepository,
            ArticleSearchRepository articleSearchRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.filepath = filepath;
        this.siteRepository = siteRepository;
        this.attachmentRepository = attachmentRepository;
        this.articleSearchRepository = articleSearchRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public SiteModel getSiteById(String id) {
        return siteRepository.findById(id)
                .map(SiteModel::new)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public SiteModel getSiteByDomainName(String domainName) {
        return siteRepository.findByDomainName(domainName)
                .map(SiteModel::new)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. domainName: " + domainName));
    }

    @Transactional(readOnly = true)
    @Override
    public SiteModel getSiteContext(String domainName) {
        Site site = siteRepository.findByDomainName(domainName)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. domainName: " + domainName));
        SiteModel siteModel = new SiteModel(site);

        // 프로필 이미지, 배경 이미지 조인
        attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(site.getId()))
                .stream()
                .map(AttachmentModel::new)
                .forEach(siteModel::addAttachment);

        // 카테고리 조인
        site.getCategories()
                .stream()
                .filter(Category::isExposed)
                .sorted(Comparator.comparing(Category::getSequence))
                .map(CategoryModel::new)
                .forEach(siteModel::addCategory);

        // 최근 7일 게시글 조인
        List<String> categoryIds = siteModel.getCategories()
                .stream()
                .map(CategoryModel::getId)
                .toList();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);

        List<ArticleModel> articles = articleSearchRepository.findAllByCategoryId(categoryIds, cutoffDate)
                .stream()
                .map(ArticleModel::new)
                .sorted(Comparator.comparing(BaseModel::getCreatedDate))
                .toList();
        siteModel.getCategories()
                .forEach(categoryModel -> categoryModel.joinArticles(articles));

        Map<String, CategoryModel> allCategories = siteModel.getCategories()
                .stream()
                .collect(Collectors.toMap(CategoryModel::getId, Function.identity()));

        // 최근 게시글을 병합하여 상위 카테고리 게시글에 바인딩
        siteModel.getCategories()
                .stream()
                .filter(CategoryModel::isRoot)
                .forEach(categoryModel -> articles.stream()
                        .filter(article -> categoryModel.getId().equals(allCategories.get(article.getCategoryId()).getParentId()))
                        .forEach(categoryModel::addRecentArticle));

        // 최근 게시글을 해당 카테고리에도 바인딩
        articles.forEach(article -> {
            CategoryModel category = allCategories.get(article.getCategoryId());
            if (category != null) {
                category.addRecentArticle(article);
            }
        });

        return siteModel;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SiteModel> getOwnSites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다. username: " + username));

        // 계정 식별자로 roleMappingRepository.findAllByUserId로 계정에 매핑된 siteId 목록 추출
        List<String> siteIds = user.getRoleMappings()
                .stream()
                .map(RoleMapping::getSite)
                .map(Site::getId)
                .toList();

        return siteRepository.findAllByIdIn(siteIds)
                .stream()
                .map(SiteModel::new)
                .toList();
    }

    @Override
    public SiteModel createSite(CreateSiteCommand command) throws IOException {
        Site site = Site.create(
                command.getName(),
                command.getDomainName(),
                command.getDescription(),
                command.getDistributionCode(),
                command.isSearchEngineExposed(),
                command.isImageExposed(),
                command.getTags(),
                command.getAddress(),
                command.getContactNumber(),
                command.getIntro(),
                command.getContent()
        );
        siteRepository.save(site);

        // todo 사이트와 첨부파일은 논리적 연관관계를 맺고있기 때문에 Site Entity의 Association을 제거해야 함.
        SiteModel siteModel = new SiteModel(site);

        // 프로필 이미지
        if (command.hasProfileImage()) {
            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.INLINE_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getProfileImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            siteModel.addAttachment(new AttachmentModel(attachment));
        }

        // 배경 이미지
        if (command.hasBackgroundImage()) {
            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.BACKGROUND_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getBackgroundImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            siteModel.addAttachment(new AttachmentModel(attachment));
        }
        return siteModel;
    }

    @Override
    public SiteModel updateSite(String id, CreateSiteCommand command) throws IOException {
        Site site = siteRepository.getReferenceById(id);
        site.update(
                command.getName(),
                command.getDomainName(),
                command.getDescription(),
                command.getDistributionCode(),
                command.isSearchEngineExposed(),
                command.isImageExposed(),
                command.getTags(),
                command.getAddress(),
                command.getContactNumber(),
                command.getIntro(),
                command.getContent()
        );
        siteRepository.save(site);
        SiteModel siteModel = new SiteModel(site);

        // 기존 파일 조회
        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(site.getId()));

        // 프로필 이미지
        if (command.hasProfileImage()) {
            // 기존 첨부파일 제거
            List<Attachment> profileImages = attachments.stream().filter(attachment -> Attachment.Type.PROFILE.getPath().equals(attachment.getPathName())).toList();
            attachmentRepository.deleteAllInBatch(profileImages);
            attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.INLINE_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getProfileImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            siteModel.addAttachment(new AttachmentModel(attachment));
        }

        // 배경 이미지
        if (command.hasBackgroundImage()) {
            // 기존 첨부파일 제거
            List<Attachment> backgroundImages = attachments.stream().filter(attachment -> Attachment.Type.BACKGROUND_IMAGE.getPath().equals(attachment.getPathName())).toList();
            attachmentRepository.deleteAllInBatch(backgroundImages);
            attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.BACKGROUND_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getBackgroundImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            siteModel.addAttachment(new AttachmentModel(attachment));
        }
        return siteModel;
    }

    @Override
    public void deleteSite(String id) {
        // 사이트에 종속된 모든 영속성 데이터를 먼저 제거하고 삭제 가능하도록 할 것
        Site site = siteRepository.getReferenceById(id);

        // 첨부파일 삭제
        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(id));
        attachmentRepository.deleteAllInBatch(attachments);
        attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

        // 카테고리 삭제
//        categoryRepository

        // 게시글 삭제

        // 댓글 삭제

        // 투표 삭제

        // 뷰 삭제

        // 사이트 삭제
        siteRepository.deleteById(id);
    }

    private void writeFile(String pathname, byte[] bytes) {
        try {
            FileIOService.write(pathname, bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
