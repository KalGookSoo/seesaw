package kr.me.seesaw.core.authentication;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.UUID;

public class AnonymousPrincipalProvider implements PrincipalProvider {

    @Override
    public Authentication getAuthentication() {
        return new AnonymousAuthenticationToken(
                UUID.randomUUID().toString(),
                "anonymous",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );
    }

}
