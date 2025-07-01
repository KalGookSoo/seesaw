package kr.me.seesaw.command;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "카테고리 수정 커맨드")
@Data
public class UpdateCategoryCommand implements Serializable {

    @Parameter(description = "카테고리 식별자", required = true)
    @Schema(description = "카테고리 식별자", example = "카테고리 식별자")
    private String id;

    @Parameter(description = "내용")
    @Schema(description = "내용", example = "내용")
    private String content;

}
