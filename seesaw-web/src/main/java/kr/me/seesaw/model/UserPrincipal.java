package kr.me.seesaw.model;

import kr.me.seesaw.domain.Role;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.domain.vo.RoleName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails, OAuth2User {

    private final User user;

    private final Map<String, Object> attributes;

    public UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public String getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        boolean accountNonLocked = isAccountNonLocked();
        boolean accountNonExpired = isAccountNonExpired();
        boolean credentialsNonExpired = isCredentialsNonExpired();
        return accountNonLocked && accountNonExpired && credentialsNonExpired;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User that) {
            return getUsername().equals(that.getUsername());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [" +
                "Username=" + getUsername() + ", " +
                "Password=[PROTECTED], " +
                "Enabled=" + isEnabled() + ", " +
                "AccountNonExpired=" + isAccountNonExpired() + ", " +
                "CredentialsNonExpired=" + isCredentialsNonExpired() + ", " +
                "AccountNonLocked=" + isAccountNonLocked() + ", " +
                "Granted Authorities=" + getAuthorities() + "]";
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public boolean hasRole(String role) {
        return getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }

    public String getRoleDescription() {
        if (hasRole(RoleName.ROLE_ADMIN.name())) {
            return RoleName.ROLE_ADMIN.getDescription();
        }
        if (hasRole(RoleName.ROLE_MANAGER.name())) {
            return RoleName.ROLE_MANAGER.getDescription();
        }
        if (hasRole(RoleName.ROLE_USER.name())) {
            return RoleName.ROLE_USER.getDescription();
        }
        return RoleName.ROLE_ANONYMOUS.getDescription();
    }

}
