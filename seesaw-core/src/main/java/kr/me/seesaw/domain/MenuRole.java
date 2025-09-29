package kr.me.seesaw.domain;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(exclude = {"menu", "role"}, callSuper = true)
@ToString(exclude = {"menu", "role"})

@Entity
@Table(name = "tb_menu_role", indexes = {
        @Index(columnList = "menu_id"),
        @Index(columnList = "role_id")
})
@Comment("메뉴 역할 매핑")
@DynamicInsert
@DynamicUpdate
public class MenuRole extends BaseEntity {

    @Comment("메뉴")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", referencedColumnName = "id")
    private Menu menu;

    @Comment("역할")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    public static MenuRole create(String menuId, String roleId) {
        MenuRole menuRole = new MenuRole();
        Menu menu = new Menu();
        menu.setId(menuId);
        menuRole.menu = menu;
        Role role = new Role();
        role.setId(roleId);
        menuRole.role = role;
        return menuRole;
    }

}
