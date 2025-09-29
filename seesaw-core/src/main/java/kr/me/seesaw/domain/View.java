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
@EqualsAndHashCode(exclude = {"article"}, callSuper = true)
@ToString(exclude = {"article"})

@Entity
@Table(name = "tb_view", indexes = {
        @Index(columnList = "article_id")
})
@Comment("뷰")
@DynamicInsert
@DynamicUpdate
public class View extends BaseEntity {

    @Comment("게시글 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private Article article;

    public static View create(String articleId) {
        View view = new View();
        Article article = new Article();
        article.setId(articleId);
        view.article = article;
        return view;
    }

}
