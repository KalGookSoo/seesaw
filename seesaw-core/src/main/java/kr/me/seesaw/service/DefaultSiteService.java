package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.core.file.FileIOService;
import kr.me.seesaw.domain.*;
import kr.me.seesaw.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
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

    private final CategoryRepository categoryRepository;

    private final ArticleSearchRepository articleSearchRepository;

    private final RoleMappingRepository roleMappingRepository;

    private final UserRepository userRepository;

    public DefaultSiteService(
            @Value("${kr.me.seesaw.filepath}") String filepath,
            SiteRepository siteRepository,
            AttachmentRepository attachmentRepository,
            CategoryRepository categoryRepository,
            ArticleSearchRepository articleSearchRepository,
            RoleMappingRepository roleMappingRepository, UserRepository userRepository
    ) {
        this.filepath = filepath;
        this.siteRepository = siteRepository;
        this.attachmentRepository = attachmentRepository;
        this.categoryRepository = categoryRepository;
        this.articleSearchRepository = articleSearchRepository;
        this.roleMappingRepository = roleMappingRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Site getSiteById(String id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. id: " + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "siteCache", key = "#domainName")
    @Override
    public Site getSiteByDomainName(String domainName) {
        return siteRepository.findByDomainName(domainName)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. domainName: " + domainName));
    }

    @Transactional(readOnly = true)
    @Override
    public Site getSiteContext(String domainName) {
        Site site = siteRepository.findByDomainName(domainName)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. domainName: " + domainName));
        // 프로필 이미지, 배경 이미지 조인
        // TODO 서비스로 만들어 캐싱할 것
        attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(site.getId()))
                .forEach(site::addAttachment);

        // 카테고리 조인
        // TODO 서비스로 만들어 캐싱할 것
        categoryRepository.findAllBySiteId(site.getId(), Sort.by(Sort.Direction.ASC, "sequence"))
                .stream()
                .filter(Category::isExposed)
                .forEach(site::addCategory);

        // 최근 7일 게시글 조인
        List<String> categoryIds = site.getCategories().stream().map(Category::getId).toList();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);

        // TODO 서비스로 만들어 캐싱할 것
        List<Article> articles = articleSearchRepository.findAllByCategoryId(categoryIds, cutoffDate)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .toList();
        site.getCategories().forEach(category -> category.joinArticles(articles));

        Map<String, Category> allCategories = site.getCategories().stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        // 최근 게시글을 병합하여 상위 카테고리 게시글에 바인딩
        site.getCategories()
                .stream()
                .filter(Category::isRoot)
                .forEach(category -> articles.stream()
                        .filter(article -> category.getId().equals(allCategories.get(article.getCategoryId()).getParentId()))
                        .forEach(category::addRecentArticle));

        // 최근 게시글을 해당 카테고리에도 바인딩
        articles.forEach(article -> {
            Category category = allCategories.get(article.getCategoryId());
            if (category != null) {
                category.addRecentArticle(article);
            }
        });

        return site;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Site> getOwnSites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다. username: " + username));

        // 계정 식별자로 roleMappingRepository.findAllByUserId로 계정에 매핑된 siteId 목록 추출
        List<RoleMapping> roleMappings = roleMappingRepository.findAllByUserId(user.getId());
        List<String> siteIds = roleMappings.stream()
                .map(RoleMapping::getSiteId)
                .distinct()
                .toList();

        return siteRepository.findAllByIdIn(siteIds);
    }

    @Override
    public Site createSite(CreateSiteCommand command) throws IOException {
        //
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

        // 프로필 이미지
        if (command.hasProfileImage()) {
            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.INLINE_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getProfileImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            site.addAttachment(attachment);
        }

        // 배경 이미지
        if (command.hasBackgroundImage()) {
            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.BACKGROUND_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getBackgroundImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            site.addAttachment(attachment);
        }
        return site;
    }

    @Override
    public Site updateSite(String id, CreateSiteCommand command) throws IOException {
        Site site = Optional.ofNullable(siteRepository.getReferenceById(id))
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. id: " + id));
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

        // 프로필 이미지
        if (command.hasProfileImage()) {
            // 기존 파일 조회 및 삭제
            List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(site.getId()));
            attachmentRepository.deleteAllInBatch(attachments);
            attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.INLINE_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getProfileImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            site.addAttachment(attachment);
        }

        // 배경 이미지
        if (command.hasBackgroundImage()) {
            // 쓰기
            Attachment attachment = Attachment.create(site.getId(), Attachment.Type.BACKGROUND_IMAGE, command.getProfileImage());
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), command.getBackgroundImage().getBytes());

            // 영속화
            attachmentRepository.save(attachment);
            site.addAttachment(attachment);
        }
        return site;
    }

    @Override
    public void deleteSite(String id) {
        // 사이트에 종속된 모든 영속성 데이터를 먼저 제거하고 삭제 가능하도록 할 것
        Site site = siteRepository.getReferenceById(id);
        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(site.getId()));
        attachmentRepository.deleteAllInBatch(attachments);
        attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);
    }

    private void writeFile(String pathname, byte[] bytes) {
        try {
            FileIOService.write(pathname, bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
