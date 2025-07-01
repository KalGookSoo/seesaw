package kr.me.seesaw.repository;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import kr.me.seesaw.domain.Article;
import kr.me.seesaw.search.ArticleSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ArticleSearchRepository implements SearchRepository<Article, ArticleSearch> {

    private final EntityManager em;

    @Override
    public Page<Article> search(Pageable pageable, ArticleSearch search) {
        String jpql = "select article from Article article where 1 = 1";
        jpql += generateJpql(search);

        if (pageable.getSort().isSorted()) {
            jpql += " order by " + pageable.getSort().toString().replace(":", "");
        }

        TypedQuery<Article> query = em.createQuery(jpql, Article.class);
        setParameters(query, search);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과 리스트 조회
        List<Article> articles = query.getResultList();

        // 카운트 쿼리
        String countJpql = "select count(distinct article) from Article article where 1 = 1";
        countJpql += generateJpql(search);

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        setParameters(countQuery, search);

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(articles, pageable, count);
    }

    public List<Article> findAllByCategoryId(List<String> categoryIds, LocalDateTime createdDate) {
        String jpql = "select article from Article article where categoryId in :categoryIds and createdDate >= :createdDate";
        TypedQuery<Article> query = em.createQuery(jpql, Article.class);
        query.setParameter("categoryIds", categoryIds);
        query.setParameter("createdDate", createdDate);
        return query.getResultList();
    }

    private String generateJpql(@Nonnull ArticleSearch search) {
        StringBuilder jpql = new StringBuilder();
        if (StringUtils.hasText(search.getCategoryId())) {
            jpql.append(" and article.categoryId = :categoryId");
        }
        if ("title".equals(search.getKeyField()) && StringUtils.hasText(search.getKeyWord())) {
            jpql.append(" and article.title like :title");
        }
        if ("content".equals(search.getKeyField()) && StringUtils.hasText(search.getKeyWord())) {
            jpql.append(" and article.content like :content");
        }
        if ("createdBy".equals(search.getKeyField()) && StringUtils.hasText(search.getKeyWord())) {
            jpql.append(" and article.createdBy like :createdBy");
        }
        return jpql.toString();
    }

    private void setParameters(@Nonnull TypedQuery<?> query, @Nonnull ArticleSearch search) {
        if (StringUtils.hasText(search.getCategoryId())) {
            query.setParameter("categoryId", search.getCategoryId());
        }
        if ("title".equals(search.getKeyField()) && StringUtils.hasText(search.getKeyWord())) {
            query.setParameter("title", "%" + search.getKeyWord() + "%");
        }
        if ("content".equals(search.getKeyField()) && StringUtils.hasText(search.getKeyWord())) {
            query.setParameter("content", "%" + search.getKeyWord() + "%");
        }
        if ("createdBy".equals(search.getKeyField()) && StringUtils.hasText(search.getKeyWord())) {
            query.setParameter("createdBy", "%" + search.getKeyWord() + "%");
        }
    }

}
