package kr.me.seesaw.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import kr.me.seesaw.domain.Attachment;
import kr.me.seesaw.domain.Reply;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "ReplyModel", description = "댓글 모델")
@ToString(exclude = {"article", "attachments", "votes"})
@EqualsAndHashCode(exclude = {"article", "attachments", "votes"}, callSuper = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ReplyModel extends AbstractHierarchicalModel<ReplyModel> implements Hierarchical<ReplyModel, String> {

    @Comment("노출여부")
    private boolean exposed;

    @Comment("본문")
    @Schema(description = "댓글 본문")
    private String content;

    @Comment("게시글 식별자")
    @Schema(description = "게시글 식별자(UUID)")
    private String articleId;

    @JsonBackReference
    private ArticleModel article;

    @JsonManagedReference
    private List<AttachmentModel> attachments = new ArrayList<>();

    @JsonManagedReference
    private List<VoteModel> votes = new ArrayList<>();

    @Override
    public void addChild(ReplyModel child) {
        getChildren().add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public ReplyModel(Reply reply) {
        setBaseModel(reply);
        setParentId(reply.getParentId());
        this.exposed = reply.isExposed();
        this.content = reply.getContent();
        this.articleId = reply.getArticleId();
    }

    public String getMaskedAuthor() {
        String createdBy = getCreatedBy();
        int visibleChars = Math.min(createdBy.length(), 4);
        return createdBy.substring(0, visibleChars) + "****";
    }

    public void addAttachment(AttachmentModel attachment) {
        attachments.add(attachment);
    }

    public void joinAttachments(List<AttachmentModel> attachments) {
        attachments.stream().filter(this::isAttachmentForReply).forEach(this::addAttachment);
    }

    private boolean isAttachmentForReply(AttachmentModel attachment) {
        return getId().equals(attachment.getReferenceId());
    }

    public List<AttachmentModel> getInlineImages() {
        return attachments.stream().filter(AttachmentModel::isInlineImage).toList();
    }

    public List<AttachmentModel> getAttachments() {
        return attachments.stream().filter(AttachmentModel::isAttachment).toList();
    }
}
