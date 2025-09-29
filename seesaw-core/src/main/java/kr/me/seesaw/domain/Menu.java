package kr.me.seesaw.domain;

import jakarta.persistence.*;
import jakarta.persistence.Index;
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
@EqualsAndHashCode(exclude = {"menuRoles"}, callSuper = true)
@ToString(exclude = {"menuRoles"})

@Entity
@Table(name = "tb_menu", indexes = {
        @Index(columnList = "parent_id")
})
@DynamicInsert
@DynamicUpdate
@Comment("메뉴")
public class Menu extends AbstractHierarchical<Menu> {

    @Comment("이름")
    private String name;

    @Comment("URI")
    private String uri;

    @Comment("순번")
    private Integer sequence;

    @OneToMany(mappedBy = "menu", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<MenuRole> menuRoles = new ArrayList<>();

    public Menu(String name, String uri, Integer sequence) {
        this.name = name;
        this.uri = uri;
        this.sequence = sequence;
    }

    public void update(String name, String uri, Integer sequence) {
        this.name = name;
        this.uri = uri;
        this.sequence = sequence;
    }

}
