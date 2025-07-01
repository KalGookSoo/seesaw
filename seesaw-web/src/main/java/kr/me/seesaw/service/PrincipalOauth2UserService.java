package kr.me.seesaw.service;

import kr.me.seesaw.domain.*;
import kr.me.seesaw.domain.vo.Email;
import kr.me.seesaw.domain.vo.RoleName;
import kr.me.seesaw.model.UserPrincipal;
import kr.me.seesaw.oauth2.provider.NaverUserDetail;
import kr.me.seesaw.oauth2.provider.OAuth2UserDetail;
import kr.me.seesaw.repository.RoleMappingRepository;
import kr.me.seesaw.repository.RoleRepository;
import kr.me.seesaw.repository.SiteRepository;
import kr.me.seesaw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final SiteRepository siteRepository;

    private final RoleMappingRepository roleMappingRepository;

    private final String domainName;

    public PrincipalOauth2UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            SiteRepository siteRepository,
            RoleMappingRepository roleMappingRepository,
            @Value("${site.domain.name}") String domainName
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.siteRepository = siteRepository;
        this.roleMappingRepository = roleMappingRepository;
        this.domainName = domainName;
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (!"naver".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException("계정 인증 실패");
        }
        OAuth2UserDetail oAuth2UserDetail = new NaverUserDetail(oAuth2User.getAttributes());
        String email = oAuth2UserDetail.getEmail();
        String username = email.split("@")[0];
        Site site = siteRepository.findByDomainName(domainName).orElseThrow(NoSuchElementException::new);// TODO 사이트를 찾을 수 없는 경우 커스텀 예외 만들어서 커스텀 페이지 만들 것

        return userRepository.findByUsername(username)
                .map(user -> {
                    // 인증한 계정이 현재 사이트에 종속된 역할 목록 조회
                    List<RoleMapping> roleMappings = roleMappingRepository.findAllByUserIdAndSiteId(user.getId(), site.getId());
                    List<Role> roles = roleRepository.findAllByIdIn(roleMappings.stream().map(RoleMapping::getRoleId).toList());

                    // 일반사용자 역할이 없다면 생성하여 부여
                    boolean hasRoleUser = roles.stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_USER.name()));
                    if (!hasRoleUser) {
                        Role roleUser = roleRepository.findByName(RoleName.ROLE_USER.name()).orElseThrow(NoSuchElementException::new);
                        RoleMapping roleMapping = RoleMapping.create(roleUser.getId(), user.getId(), site.getId());
                        roleMappingRepository.save(roleMapping);
                        user.addRole(roleUser);
                    }

                    // 역할 목록을 계정에 할당
                    roles.forEach(user::addRole);
                    return new UserPrincipal(user, oAuth2User.getAttributes());
                })
                .orElseGet(() -> {
                    // 계정 생성
                    User user = User.create(username, new Email(email.split("@")[0], email.split("@")[1]));
                    userRepository.save(user);

                    // 현재 인증한 계정, 현재 접속한 사이트에 일반사용자 역할을 부여
                    Role roleUser = roleRepository.findByName(RoleName.ROLE_USER.name()).orElseThrow(NoSuchElementException::new);
                    RoleMapping roleMapping = RoleMapping.create(roleUser.getId(), user.getId(), site.getId());
                    roleMappingRepository.save(roleMapping);

                    // 일반사용자
                    user.addRole(roleUser);
                    return new UserPrincipal(user, oAuth2User.getAttributes());
                });

    }
}
