package kr.me.seesaw.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 알림 타입
 */
@Getter
@AllArgsConstructor
public enum NotificationType {
    TOKTOK("톡톡"),
    SMS("문자"),
    EMAIL("이메일");
    private final String description;
}
