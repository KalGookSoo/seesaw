package kr.me.seesaw.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(exclude = {"role", "user", "site"}, callSuper = true)
@ToString(exclude = {"role", "user", "site"})

@Entity
@Table(name = "tb_role_mapping", indexes = {
        @Index(columnList = "user_id, site_id"),
        @Index(columnList = "role_id")
})
@Comment("역할 매핑")
@DynamicInsert
@DynamicUpdate
public class RoleMapping extends BaseEntity {

    @Comment("계정 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Comment("사이트 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @Comment("역할 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    public static RoleMapping create(Role role, User user, Site site) {
        RoleMapping roleMapping = new RoleMapping();
        roleMapping.role = role;
        roleMapping.user = user;
        roleMapping.site = site;
        return roleMapping;
    }

}
