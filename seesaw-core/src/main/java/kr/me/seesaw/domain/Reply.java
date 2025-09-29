package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import kr.me.seesaw.command.CreateReplyCommand;
import kr.me.seesaw.command.UpdateReplyCommand;
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
@Table(name = "tb_reply", indexes = {
        @Index(columnList = "article_id"),
        @Index(columnList = "parent_id")
})
@Comment("댓글")
@DynamicInsert
@DynamicUpdate
public class Reply extends AbstractHierarchical<Reply> {

    @Comment("노출여부")
    private boolean exposed;

    @Comment("본문")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Comment("게시글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @JsonBackReference
    private Article article;

    public static Reply create(CreateReplyCommand command) {
        Reply reply = new Reply();
        Article article = new Article();
        article.setId(command.getArticleId());
        reply.article = article;
        reply.content = command.getContent();
        reply.exposed = command.isExposed();
        return reply;
    }

    public void update(UpdateReplyCommand command) {
        this.content = command.getContent();
        this.exposed = command.isExposed();
    }

}
