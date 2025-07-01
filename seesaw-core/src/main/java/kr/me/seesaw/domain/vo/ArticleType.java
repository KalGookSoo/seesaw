package kr.me.seesaw.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 타입
 */
@Getter
@AllArgsConstructor
public enum ArticleType {
    MAP("지도"),
    HTML("HTML"),
    CAROUSEL("캐러셀"),
    BUTTON_GROUP("버튼"),
    IMAGE("이미지"),
    TABLE("표"),
    VIDEO("동영상");
    private final String description;
}
