package kr.me.seesaw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "tb_permission")
@DynamicInsert
@DynamicUpdate
@Comment("권한")
public class Permission extends BaseEntity {

    @Comment("대상 식별자")
    @Column(length = 36)
    private String targetId;

    @Comment("역할 식별자")
    @Column(length = 36)
    private String roleId;

    /**
     * ACL 권한 마스크(mask) 값의 의미와 관련된 정보는
     * Spring Security의 BasePermission 클래스의 JavaDoc을 참조하세요.
     *
     * @see <a href="https://docs.spring.io/spring-security/site/docs/4.2.20.RELEASE/apidocs/org/springframework/security/acls/domain/BasePermission.html">
     * BasePermission (Spring Security 4.2.20.RELEASE API)
     * </a>
     */
    @Comment("비트마스크")
    private int mask;

    public Permission(String targetId, String roleId, int mask) {
        this.targetId = targetId;
        this.roleId = roleId;
        this.mask = mask;
    }

    public static Permission create(String targetId, String roleId, int mask) {
        return new Permission(targetId, roleId, mask);
    }

}
