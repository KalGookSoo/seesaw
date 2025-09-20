package kr.me.seesaw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.Attachment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Schema(name = "AttachmentModel", description = "첨부파일 모델")
@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@AllArgsConstructor
public final class AttachmentModel extends BaseModel {

    @Schema(description = "참조 식별자", example = "8f14e45f-ea9d-4b1c-a3a4-12c4b2a9c001")
    private String referenceId;

    @Schema(description = "", example = "")
    private String originalName;

    @Schema(description = "", example = "")
    private String name;

    @Schema(description = "", example = "")
    private String pathName;

    @Schema(description = "", example = "")
    private String mimeType;

    @Schema(description = "", example = "")
    private long size;

    public AttachmentModel(Attachment attachment) {
        setBaseModel(attachment);
        this.referenceId = attachment.getReferenceId();
        this.originalName = attachment.getOriginalName();
        this.name = attachment.getName();
        this.pathName = attachment.getPathName();
        this.mimeType = attachment.getMimeType();
        this.size = attachment.getSize();
    }

}
