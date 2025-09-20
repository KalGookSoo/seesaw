package kr.me.seesaw.command;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.Attachment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Schema(description = "사이트 생성 커맨드")
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public final class CreateAttachmentCommand implements Serializable {

    private final String referenceId;

    private final Attachment.Type type;

    private final MultipartFile multipartFile;

}
