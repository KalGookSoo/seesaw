package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import kr.me.seesaw.domain.vo.NotificationType;
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
@Table(name = "tb_notification")
@DynamicInsert
@DynamicUpdate
@Comment("알림")
public class Notification extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Comment("유형")
    private NotificationType type;

    @Comment("카테고리 식별자")
    @Column(length = 36)
    private String categoryId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

}
