package kr.me.seesaw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.vo.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Schema(description = "카테고리 모델")
@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED, force = true)
@SuperBuilder
public class CategoryModel extends BaseModel implements Serializable {
    private final String name;

    private final String description;

    private final CategoryType type;

    private final boolean siteExposed;

    private final int siteExposedOrder;

    private final boolean exposed;

    private final Integer sequence;

    private final String siteId;
}
