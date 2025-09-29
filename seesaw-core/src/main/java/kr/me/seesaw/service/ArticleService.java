package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.search.ArticleSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {

    Page<ArticleModel> findAll(Pageable pageable, ArticleSearch search);

    Page<ArticleModel> findAllByCategoryId(String categoryId, Pageable pageable);

    ArticleModel find(String id);

    ArticleModel getArticleAggregation(String id);

    ArticleModel create(CreateArticleCommand command) throws IOException;

    ArticleModel update(String id, UpdateArticleCommand command) throws IOException;

    void delete(String id);

    void deleteAll(List<String> ids);

    boolean isOwner(String id, String username);

    List<ArticleModel> getFixedArticles(String categoryId, boolean fixed, Sort sort);

    @Nullable
    ArticleModel getPreviousArticle(ArticleSearch search, LocalDateTime createdDate);

    @Nullable
    ArticleModel getNextArticle(ArticleSearch search, LocalDateTime createdDate);

}
