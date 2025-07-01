package kr.me.seesaw.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 카테고리 타입
 */
@Getter
@AllArgsConstructor
public enum CategoryType {
    NONE("타입없음"),
    STATIC_CONTENT("정적컨텐츠"),
    BOARD("게시판"),
    QNA("문의/신청"),
    SCHEDULE("스케줄(캘린더)"),
    STORE("메뉴/가격"),
    BUSINESS("매장/영업");
    private final String description;
}
