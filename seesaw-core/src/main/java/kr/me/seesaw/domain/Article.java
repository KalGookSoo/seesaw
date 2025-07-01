package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import kr.me.seesaw.domain.vo.ArticleType;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_article")
@Comment("게시글")
@DynamicInsert
@DynamicUpdate
public class Article extends AbstractHierarchical<Article> implements Hierarchical<Article, String> {

    @Comment("노출여부")
    private boolean exposed;

    @Comment("고정여부")
    private boolean fixed;

    @Comment("고정순서")
    private Integer fixedOrder;

    @Comment("제목")
    private String title;

    @Comment("본문")
    @Column(columnDefinition = "TEXT")
    @Setter(AccessLevel.PUBLIC)
    private String content;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private ArticleType type;

    @Comment("카테고리 식별자")
    @Column(length = 36)
    private String categoryId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Category category;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Attachment> attachments = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Reply> replies = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<View> views = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Vote> votes = new ArrayList<>();

    public boolean isRecentlyGenerated() {
        LocalDateTime now = LocalDateTime.now();
        return !getCreatedDate().isBefore(now.minusDays(7));
    }

    @Override
    public void addChild(Article child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public static Article create(CreateArticleCommand command) {
        Article article = new Article();
        article.categoryId = command.getCategoryId();
        article.type = command.getType();
        article.fixed = command.isFixed();
        article.fixedOrder = command.getFixedOrder();
        article.title = command.getTitle();
        article.content = command.getContent();
        return article;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public void update(UpdateArticleCommand command) {
        this.categoryId = command.getCategoryId();
        this.type = command.getType();
        this.fixed = command.isFixed();
        this.fixedOrder = command.getFixedOrder();
        this.title = command.getTitle();
        this.content = command.getContent();
    }

    public String getMaskedAuthor() {
        String createdBy = getCreatedBy();
        int visibleChars = Math.min(createdBy.length(), 4);
        return createdBy.substring(0, visibleChars) + "****";
    }

    public void joinReplies(List<Reply> replies) {
        replies.stream().filter(this::isReplyForArticle).forEach(this::addReply);
    }

    public boolean isReplyForArticle(Reply reply) {
        return getId().equals(reply.getArticleId());
    }

    public void addReply(Reply reply) {
        replies.add(reply);
        reply.setArticle(this);
    }

    public void joinViews(List<View> views) {
        views.stream().filter(this::isViewForArticle).forEach(this::addView);
    }

    public boolean isViewForArticle(View view) {
        return getId().equals(view.getArticleId());
    }

    public void addView(View view) {
        views.add(view);
        view.setArticle(this);
    }

    public String getPlainContent() {
        return Jsoup.parse(content).text();
    }

    public void joinAttachments(List<Attachment> attachments) {
        attachments.stream().filter(this::isAttachmentForArticle).forEach(this::addAttachment);
    }

    private boolean isAttachmentForArticle(Attachment attachment) {
        return getId().equals(attachment.getReferenceId());
    }

    public List<Attachment> getInlineImages() {
        return attachments.stream().filter(Attachment::isInlineImage).toList();
    }

    public List<Attachment> getAttachments() {
        return attachments.stream().filter(Attachment::isAttachment).toList();
    }

}
