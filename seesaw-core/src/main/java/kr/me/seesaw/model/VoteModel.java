package kr.me.seesaw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.Vote;
import lombok.*;
import org.hibernate.annotations.Comment;

@Schema(name = "VoteModel", description = "투표 모델")
@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VoteModel extends BaseModel {

    @Comment("참조 식별자")
    @Schema(description = "참조 식별자(UUID)")
    private String referenceId;

    @Comment("찬성여부")
    @Schema(description = "찬성 여부")
    private boolean approved;

    public VoteModel(Vote vote) {
        setBaseModel(vote);
        this.referenceId = vote.getReferenceId();
        this.approved = vote.isApproved();
    }
}
