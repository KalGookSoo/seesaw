package kr.me.seesaw.config;

import kr.me.seesaw.security.SeesawPermissionEvaluator;
import kr.me.seesaw.service.PrincipalOauth2UserService;
import kr.me.seesaw.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public PermissionEvaluator seesawPermissionEvaluator(RoleService roleService) {
        return new SeesawPermissionEvaluator(roleService);
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(@Qualifier("seesawPermissionEvaluator") PermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(this::handleCsrfPolicies);
        http.cors(this::handleCorsPolicies);
        http.authorizeHttpRequests(this::handleAuthorizeHttpRequests);
        http.oauth2Login(this::handleOauth2Login);
        http.logout(this::configureLogout);

        return http.build();
    }

    private void handleCsrfPolicies(CsrfConfigurer<HttpSecurity> config) {
        config.ignoringRequestMatchers("/sign-out", "/**");
        config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    private void handleCorsPolicies(CorsConfigurer<HttpSecurity> config) {
        config.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(Collections.singletonList("/**"));
            configuration.setAllowedOrigins(Collections.singletonList(request.getHeader(HttpHeaders.ORIGIN)));
            configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
            configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CACHE_CONTROL, HttpHeaders.CONTENT_TYPE, "X-XSRF-TOKEN"));
            configuration.setAllowCredentials(true);
            return configuration;
        });
    }

    private void handleAuthorizeHttpRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry config) {
        config.requestMatchers(new AntPathRequestMatcher("/styles/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/scripts/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/fonts/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/health")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).hasRole("ADMIN")
                .anyRequest()
                .permitAll();
    }

    private void handleOauth2Login(OAuth2LoginConfigurer<HttpSecurity> httpSecurityOAuth2LoginConfigurer) {
        httpSecurityOAuth2LoginConfigurer.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(principalOauth2UserService))
                .successHandler(authenticationSuccessHandler());
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> config) {
        config.logoutUrl("/sign-out")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.sendRedirect("/");
                });
    }

}
