package kr.me.seesaw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.domain.vo.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(name = "CategoryModel", description = "카테고리 모델")
@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
public class CategoryModel extends AbstractHierarchicalModel<CategoryModel> implements Hierarchical<CategoryModel, String> {

    @Schema(description = "카테고리 이름", example = "공지사항")
    private String name;

    @Schema(description = "카테고리 설명", example = "중요 공지 모음")
    private String description;

    @Schema(description = "카테고리 타입", example = "BOARD", implementation = CategoryType.class)
    private CategoryType type;

    @Schema(description = "사이트 노출 여부", example = "true")
    private boolean siteExposed;

    @Schema(description = "사이트 노출 순서", example = "1")
    private int siteExposedOrder;

    @Schema(description = "노출 여부", example = "true")
    private boolean exposed;

    @Schema(description = "정렬 순서", example = "10")
    private Integer sequence;

    @Schema(description = "사이트 식별자(UUID)", example = "8f14e45f-ea9d-4b1c-a3a4-12c4b2a9c001")
    private String siteId;

    @Override
    public void addChild(CategoryModel child) {
        getChildren().add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public CategoryModel(Category category) {
        setId(category.getId());
        setCreatedBy(category.getCreatedBy());
        setCreatedIp(category.getCreatedIp());
        setCreatedDate(category.getCreatedDate());
        setLastModifiedBy(category.getLastModifiedBy());
        setLastModifiedIp(category.getLastModifiedIp());
        setLastModifiedDate(category.getLastModifiedDate());
        setParentId(category.getParentId());
        name = category.getName();
        description = category.getDescription();
        type = category.getType();
        siteExposed = category.isSiteExposed();
        siteExposedOrder = category.getSiteExposedOrder();
        exposed = category.isExposed();
        sequence = category.getSequence();
        siteId = category.getSiteId();
    }

}
