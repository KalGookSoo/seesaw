package kr.me.seesaw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_vote")
@Comment("투표")
@DynamicInsert
@DynamicUpdate
public class Vote extends BaseEntity {

    @Comment("참조 식별자")
    @Column(length = 36)
    private String referenceId;

    @Comment("찬성여부")
    private boolean approved;

}
