package kr.me.seesaw.command;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.vo.CategoryType;
import lombok.*;

import java.io.Serializable;

@Schema(description = "카테고리 생성 커맨드")
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryCommand implements Serializable {
    private String name;

    private String description;

    private CategoryType type;

    private boolean siteExposed;

    private int siteExposedOrder;

    private boolean exposed;

    private Integer sequence;

    private String siteId;
}
