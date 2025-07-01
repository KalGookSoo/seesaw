package kr.me.seesaw.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_code")
@DynamicInsert
@DynamicUpdate
@Comment("코드")
public class Code extends AbstractHierarchical<Code> implements Hierarchical<Code, String> {

    @Comment("이름")
    private String name;

    @Comment("설명")
    private String description;

    @Comment("순서")
    private Integer sequence;

    @Override
    public void addChild(Code child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public static Code create(String name, String description, Integer sequence, String parentId) {
        Code code = new Code();
        code.name = name;
        code.description = description;
        code.sequence = sequence;
        code.parentId = parentId;
        return code;
    }
}
