package kr.me.seesaw.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.model.SiteModel;
import kr.me.seesaw.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NavigationInterceptor implements HandlerInterceptor {

    private final Environment environment;

    private final SiteService siteService;

    private String getHost(HttpServletRequest request) {
        String scheme = environment.matchesProfiles("prod") ? request.getHeader("X-Forwarded-Proto") : request.getScheme();
        String serverName = request.getServerName();
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);

        if (!environment.matchesProfiles("prod")) {
            baseUrl.append(":").append(request.getServerPort());
        }

        return baseUrl.toString();
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        try {
            String host = getHost(request);
            request.setAttribute(ContextEnvironment.REQUEST_HOST, host);

            String path = request.getRequestURI();
            request.setAttribute(ContextEnvironment.REQUEST_PATH, path);

            String queryStr = Optional.ofNullable(request.getQueryString()).orElse("");
            request.setAttribute(ContextEnvironment.REQUEST_QUERY_STRING, queryStr);

            StringBuilder requestUrl = new StringBuilder(host).append(path);
            if (!queryStr.isEmpty()) {
                requestUrl.append("?").append(queryStr);
            }
            request.setAttribute(ContextEnvironment.REQUEST_URL, requestUrl.toString());

            request.setAttribute(ContextEnvironment.ACTIVE_PROFILES, environment.getActiveProfiles());

            String applicationName = environment.getProperty("spring.application.name");
            String domainName = applicationName + ".seesaw.me.kr";
            SiteModel site = siteService.getSiteContext(domainName);
            request.setAttribute(ContextEnvironment.SITE_CONTEXT, site);

            // 요소 탐색 편의를 위한 속성 할당
            Map<String, CategoryModel> allCategories = site.getCategories().stream()
                    .collect(Collectors.toMap(CategoryModel::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            request.setAttribute(ContextEnvironment.ALL_CATEGORIES, allCategories);

            // 계층형 목록 출력을 위한 속성 할당
            List<CategoryModel> nestedCategories = HierarchicalFactory.build(site.getCategories());
            request.setAttribute(ContextEnvironment.NESTED_CATEGORIES, nestedCategories);

            // 요청 파라미터에 카테고리 식별자가 있을 경우 현재 카테고리 정보를 속성에 할당
            Optional.ofNullable(request.getParameter("categoryId"))
                    .map(Objects::toString)
                    .ifPresent(categoryId -> request.setAttribute(ContextEnvironment.CURRENT_CATEGORY, allCategories.get(categoryId)));
        } catch (NoSuchElementException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        return true;
    }

}
