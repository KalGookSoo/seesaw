package kr.me.seesaw.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.search.SiteSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SiteSearchRepository implements SearchRepository<Site, SiteSearch> {

    private final EntityManager em;

    @Override
    public Page<Site> search(Pageable pageable, SiteSearch siteSearch) {
        String jpql = "select site from Site site where 1 = 1";

        TypedQuery<Site> query = em.createQuery(jpql, Site.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과 리스트 조회
        List<Site> sites = query.getResultList();

        // 카운트 쿼리
        String countJpql = "select count(distinct site) from Site site where 1 = 1";

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(sites, pageable, count);
    }

}
