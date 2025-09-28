package kr.me.seesaw.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_role")
@Comment("역할")
@DynamicInsert
@DynamicUpdate
public class Role extends BaseEntity {

    @Comment("이름")
    private String name;

    @Comment("별칭")
    private String alias;

    @OneToMany(mappedBy = "role", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<RoleMapping> roleMappings = new ArrayList<>();

    @Deprecated
    public static Role create(String name, String alias) {
        Role role = new Role();
        role.name = name;
        role.alias = alias;
        return role;
    }

}
