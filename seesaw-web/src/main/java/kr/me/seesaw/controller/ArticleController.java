package kr.me.seesaw.controller;

import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.search.ArticleSearch;
import kr.me.seesaw.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping(params = "categoryType=STATIC_CONTENT")
    public String getArticles(
            @RequestParam String categoryId,
            Model model
    ) {
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        Pageable pageable = Pageable.unpaged(sort);
        Page<ArticleModel> page = articleService.findAllByCategoryId(categoryId, pageable);

        model.addAttribute("page", page);

        return "articles/static_content";
    }

    @GetMapping(params = "categoryType=BOARD")
    public String getArticlesInListView(
            @PageableDefault(size = 8, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        // 공지는 오름차순 후 createdDate로 내림차순
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        List<ArticleModel> fixedArticles = articleService.getFixedArticles(search.getCategoryId(), true, sort);

        Page<ArticleModel> page = articleService.findAll(pageable, search);

        model.addAttribute("fixedArticles", fixedArticles);
        model.addAttribute("page", page);

        return "articles/table";
    }

    @GetMapping(params = {"categoryType=BOARD", "viewType=CARD"})
    public String getArticlesInCardView(
            @PageableDefault(size = 9, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        Page<ArticleModel> page = articleService.findAll(pageable, search);

        model.addAttribute("page", page);

        return "articles/card";
    }

    @GetMapping("/{id}")
    public String getArticle(
            @PathVariable("id") String id,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        ArticleModel article = articleService.getArticleAggregation(id);
        model.addAttribute("article", article);

        ArticleModel previousArticle = articleService.getPreviousArticle(search, article.getCreatedDate());
        model.addAttribute("previousArticle", previousArticle);

        ArticleModel nextArticle = articleService.getNextArticle(search, article.getCreatedDate());
        model.addAttribute("nextArticle", nextArticle);

        return "articles/view";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String getArticleNew(
            @ModelAttribute("search") ArticleSearch search
    ) {
        return "articles/new";
    }

    @PreAuthorize("@defaultArticleService.isOwner(#id, authentication.name)")
    @GetMapping("/{id}/edit")
    public String getArticleEdit(
            @PathVariable("id") String id,
            @ModelAttribute("search") ArticleSearch search,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        ArticleModel article = articleService.find(id);

        model.addAttribute("article", article);
        model.addAttribute("page", page);

        return "articles/edit";
    }

}
