package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_menu")
@DynamicInsert
@DynamicUpdate
@Comment("메뉴")
public class Menu extends AbstractHierarchical<Menu> implements Hierarchical<Menu, String> {

    @Comment("이름")
    private String name;

    @Comment("URI")
    private String uri;

    @Comment("순번")
    private Integer sequence;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Role> roles = new ArrayList<>();

    @Override
    public void addChild(Menu child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

}
