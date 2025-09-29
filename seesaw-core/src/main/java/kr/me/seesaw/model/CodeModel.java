package kr.me.seesaw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import kr.me.seesaw.domain.Code;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * 코드 표현 모델
 * - 엔터티의 계층 책임을 모델로 이관합니다.
 */
@Schema(name = "CodeModel", description = "코드 모델")
@ToString(exclude = {"children", "parent"})
@EqualsAndHashCode(exclude = {"children", "parent"}, callSuper = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CodeModel extends AbstractHierarchicalModel<CodeModel> implements Hierarchical<CodeModel, String> {

    @Comment("이름")
    @Schema(description = "코드 이름")
    private String name;

    @Comment("설명")
    @Schema(description = "코드 설명")
    private String description;

    @Comment("순서")
    @Schema(description = "정렬 순서")
    private Integer sequence;

    public CodeModel(Code code) {
        setBaseModel(code);
        setParentId(code.getParent() != null ? code.getParent().getId() : null);
        this.name = code.getName();
        this.description = code.getDescription();
        this.sequence = code.getSequence();
    }

    @Override
    public void addChild(CodeModel child) {
        getChildren().add(child);
        child.setParentId(getId());
        child.setParent(this);
    }
}
