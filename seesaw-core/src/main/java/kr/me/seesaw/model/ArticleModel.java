package kr.me.seesaw.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import kr.me.seesaw.domain.Article;
import kr.me.seesaw.domain.Attachment;
import kr.me.seesaw.domain.vo.ArticleType;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema(name = "ArticleModel", description = "게시글 모델")
@ToString(exclude = {"attachments", "replies", "views", "votes"})
@EqualsAndHashCode(exclude = {"attachments", "replies", "views", "votes"}, callSuper = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ArticleModel extends AbstractHierarchicalModel<SiteModel> implements Hierarchical<SiteModel, String> {

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

    @JsonManagedReference
    private List<AttachmentModel> attachments = new ArrayList<>();

    @JsonManagedReference
    private List<ReplyModel> replies = new ArrayList<>();

    @JsonManagedReference
    private List<ViewModel> views = new ArrayList<>();

    @JsonManagedReference
    private List<VoteModel> votes = new ArrayList<>();

    @Override
    public void addChild(SiteModel child) {
        getChildren().add(child);
        child.setParentId(getId());
        // ArticleModel은 SiteModel이 아니므로 parent 참조는 설정하지 않습니다.
    }

    public ArticleModel(Article article) {
        setBaseModel(article);
        setParentId(article.getParentId());
        this.exposed = article.isExposed();
        this.fixed = article.isFixed();
        this.fixedOrder = article.getFixedOrder();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.type = article.getType();
        this.categoryId = article.getCategory() != null ? article.getCategory().getId() : null;
        if (article.getAttachments() != null) {
            this.attachments = article.getAttachments().stream().map(AttachmentModel::new).collect(Collectors.toList());
        }
        if (article.getReplies() != null) {
            this.replies = article.getReplies().stream().map(ReplyModel::new).collect(Collectors.toList());
            // set back-reference
            this.replies.forEach(r -> r.setArticle(this));
        }
        if (article.getViews() != null) {
            this.views = article.getViews().stream().map(ViewModel::new).collect(Collectors.toList());
            this.views.forEach(v -> v.setArticle(this));
        }
        if (article.getVotes() != null) {
            this.votes = article.getVotes().stream().map(VoteModel::new).collect(Collectors.toList());
        }
    }

    public String getMaskedAuthor() {
        String createdBy = getCreatedBy();
        int visibleChars = Math.min(createdBy.length(), 4);
        return createdBy.substring(0, visibleChars) + "****";
    }

    public void joinReplies(List<ReplyModel> replies) {
        replies.stream().filter(this::isReplyForArticle).forEach(this::addReply);
    }

    public boolean isReplyForArticle(ReplyModel reply) {
        return getId().equals(reply.getArticleId());
    }

    public void addReply(ReplyModel reply) {
        this.replies.add(reply);
        reply.setArticle(this);
    }

    public void joinViews(List<ViewModel> views) {
        views.stream().filter(this::isViewForArticle).forEach(this::addView);
    }

    public boolean isViewForArticle(ViewModel view) {
        return getId().equals(view.getArticleId());
    }

    public void addView(ViewModel view) {
        this.views.add(view);
        view.setArticle(this);
    }

    public String getPlainContent() {
        return Jsoup.parse(content).text();
    }

    public void joinAttachments(List<AttachmentModel> attachments) {
        attachments.stream().filter(this::isAttachmentForArticle).forEach(this::addAttachment);
    }

    private boolean isAttachmentForArticle(AttachmentModel attachment) {
        return getId().equals(attachment.getReferenceId());
    }

    private void addAttachment(AttachmentModel attachment) {
        this.attachments.add(attachment);
    }

    public List<AttachmentModel> getInlineImages() {
        return attachments.stream().filter(AttachmentModel::isInlineImage).toList();
    }

    public List<AttachmentModel> getAttachments() {
        return attachments.stream().filter(AttachmentModel::isAttachment).toList();
    }

}
