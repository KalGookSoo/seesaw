package kr.me.seesaw;

import jakarta.servlet.http.HttpServletRequest;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.interceptor.ContextEnvironment;
import kr.me.seesaw.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 메인 컨트롤러
 */
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final ArticleService articleService;

    /**
     * 메인 화면을 반환합니다.
     *
     * @return 메인 화면
     */
    @GetMapping("/")
    public String index(
            HttpServletRequest request,
            @PageableDefault(size = 3, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {

        // 하위 카테고리 중 사이트 노출 카테고리 목록을 추출
        @SuppressWarnings("unchecked")
        Map<String, CategoryModel> allCategories = ((Map<String, CategoryModel>) request.getAttribute(ContextEnvironment.ALL_CATEGORIES));
        List<String> siteExposedCategoryIds = allCategories.values()
                .stream()
                .filter(Predicate.not(CategoryModel::isRoot))
                .filter(CategoryModel::isSiteExposed)
                .sorted(Comparator.comparing(CategoryModel::getSiteExposedOrder))
                .map(CategoryModel::getId)
                .toList();

        // 사이트 노출 게시글은 최근 3 개의 게시글로 규정
        Map<String, Page<ArticleModel>> siteExposedPages = siteExposedCategoryIds.stream()
                .collect(Collectors.toMap(Function.identity(),
                        id -> articleService.findAllByCategoryId(id, pageable),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("siteExposedPages", siteExposedPages);

        return "index";
    }

}