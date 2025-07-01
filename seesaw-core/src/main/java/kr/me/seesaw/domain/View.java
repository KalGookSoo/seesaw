package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
@Table(name = "tb_view")
@Comment("뷰")
@DynamicInsert
@DynamicUpdate
public class View extends BaseEntity {

    @Comment("게시글 식별자")
    @Column(length = 36)
    private String articleId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Article article;

    public static View create(String articleId) {
        View view = new View();
        view.articleId = articleId;
        return view;
    }
}
