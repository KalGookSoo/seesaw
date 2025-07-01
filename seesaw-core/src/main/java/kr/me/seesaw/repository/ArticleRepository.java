package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {
    Page<Article> findAllByCategoryId(String categoryId, Pageable pageable);
    List<Article> findAllByCategoryIdAndFixed(String categoryId, boolean fixed, Sort sort);
}
