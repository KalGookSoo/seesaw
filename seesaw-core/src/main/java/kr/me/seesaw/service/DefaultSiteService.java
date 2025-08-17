package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.domain.*;
import kr.me.seesaw.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultSiteService implements SiteService {

    private final EntityManager entitymanager;

    private final SiteRepository siteRepository;

    private final AttachmentRepository attachmentRepository;

    private final CategoryRepository categoryRepository;

    private final ArticleSearchRepository articleSearchRepository;

    private final RoleMappingRepository roleMappingRepository;

    private final UserRepository userRepository;

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
        categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"))
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
    public Site createSite(CreateSiteCommand command) {
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
        return siteRepository.save(site);
    }

    @Override
    public Site updateSite(String id, CreateSiteCommand command) {
        Site site = entitymanager.getReference(Site.class, id);
        if (site == null) {
            throw new NoSuchElementException("사이트를 찾을 수 없습니다. id: " + id);
        }
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
        entitymanager.persist(site);

        // 첨부파일
        if (command.hasProfileImage()) {
            // 기존 파일 조회 및 삭제

            // 파일 생성
            List<String> referenceIds = Collections.singletonList(site.getId());
            List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(referenceIds);
            if (!attachments.isEmpty()) {
                attachmentRepository.deleteAll(attachments);
            }

            // 파일 영속화

        }

        return site;
    }

    @Override
    public void deleteSite(String id) {

    }

}
