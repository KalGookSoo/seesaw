package kr.me.seesaw.core.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HeaderPrincipalProvider implements PrincipalProvider {

    @Override
    public Authentication getAuthentication() {
        // bearer token으로 반환
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return null;
    }

}
