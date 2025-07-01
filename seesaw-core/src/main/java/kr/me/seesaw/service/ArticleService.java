package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.domain.Article;
import kr.me.seesaw.search.ArticleSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    Page<Article> findAll(Pageable pageable, ArticleSearch search);
    Page<Article> findAllByCategoryId(String categoryId, Pageable pageable);
    Article find(String id);
    Article create(CreateArticleCommand command) throws IOException;
    Article update(String id, UpdateArticleCommand command) throws IOException;
    void delete(String id);
    void deleteAll(List<String> ids);
    boolean isOwner(String id, String username);
    List<Article> getFixedArticles(String categoryId, boolean fixed, Sort sort);
}
