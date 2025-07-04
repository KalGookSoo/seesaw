package kr.me.seesaw.service;

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

    private final SiteRepository siteRepository;

    private final AttachmentRepository attachmentRepository;

    private final CategoryRepository categoryRepository;

    private final ArticleSearchRepository articleSearchRepository;

    private final RoleMappingRepository roleMappingRepository;

    private final UserRepository userRepository;

    @Cacheable(value = "siteCache", key = "#domainName")
    @Override
    public Site getSite(String domainName) {
        return siteRepository.findByDomainName(domainName).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Site getSiteContext(String domainName) {
        Site site = siteRepository.findByDomainName(domainName).orElseThrow(NoSuchElementException::new);
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

    @Override
    public List<Site> getOwnSites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다"));

        // 계정 식별자로 roleMappingRepository.findAllByUserId로 계정에 매핑된 siteId 목록 추출
        List<RoleMapping> roleMappings = roleMappingRepository.findAllByUserId(user.getId());
        List<String> siteIds = roleMappings.stream()
                .map(RoleMapping::getSiteId)
                .distinct()
                .toList();

        return siteRepository.findAllByIdIn(siteIds);
    }
}
