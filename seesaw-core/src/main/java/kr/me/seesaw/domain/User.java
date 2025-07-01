package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import kr.me.seesaw.domain.vo.Email;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_user")
@Comment("계정")
@DynamicInsert
@DynamicUpdate
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    @Comment("계정명")
    private String username;

    @JsonIgnore
    @Comment("패스워드")
    private String password;

    @Comment("이름")
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "email_id")),
            @AttributeOverride(name = "domain", column = @Column(name = "email_domain"))
    })
    @Comment("이메일")
    private Email email;

    @Comment("연락처")
    private String contactNumber;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Role> roles = new ArrayList<>();

    @Comment("만료 일시")
    private LocalDateTime expiredDate;

    @Comment("잠금 일시")
    private LocalDateTime lockedDate;

    @Comment("패스워드 만료 일시")
    private LocalDateTime credentialsExpiredDate;

    public static User create(String username, String password, String name, Email email, String contactNumber) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.name = name;
        user.email = email;
        user.contactNumber = contactNumber;
        user.initializeAccountPolicy();
        return user;
    }

    public static User create(String username, Email email) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.initializeAccountPolicy();
        return user;
    }

    /**
     * 패스워드를 변경합니다.
     * @param password 패스워드
     */
    public void changePassword(String password) {
        Assert.notNull(password, "패스워드는 NULL이 될 수 없습니다.");
        this.password = password;
        this.credentialsExpiredDate = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(180L);
    }

    /**
     * 만료 일시는 금일(00:00)로부터 2년 후 까지로 설정합니다.
     * 패스워드 만료 일시는 생성일(00:00)로부터 180일 후 까지로 설정합니다.
     */
    private void initializeAccountPolicy() {
        expiredDate = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusYears(2L);
        credentialsExpiredDate = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(180L);
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환합니다.
     * @return 계정이 만료되지 않았는지 여부
     */
    public boolean isAccountNonExpired() {
        return expiredDate == null || expiredDate.isAfter(LocalDateTime.now());
    }

    /**
     * 계정이 잠겨있지 않은지 여부를 반환합니다.
     * @return 계정이 잠겨있지 않은지 여부
     */
    public boolean isAccountNonLocked() {
        return lockedDate == null || lockedDate.isBefore(LocalDateTime.now());
    }

    /**
     * 계정의 패스워드가 만료되지 않았는지 여부를 반환합니다.
     * @return 계정의 패스워드가 만료되지 않았는지 여부
     */
    public boolean isCredentialsNonExpired() {
        return credentialsExpiredDate == null || credentialsExpiredDate.isAfter(LocalDateTime.now());
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public Email getEmail() {
        return email == null ? Email.empty() : email;
    }

}
