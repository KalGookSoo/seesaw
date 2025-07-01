package kr.me.seesaw.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleName {
    ROLE_USER("일반사용자"),
    ROLE_MANAGER("관리자"),
    ROLE_ADMIN("최고관리자"),
    ROLE_ANONYMOUS("익명사용자");
    private final String description;
}
