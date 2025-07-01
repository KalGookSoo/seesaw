package kr.me.seesaw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_attachment")
@Comment("첨부파일")
@DynamicInsert
@DynamicUpdate
public class Attachment extends BaseEntity {

    @Comment("참조 식별자")
    @Column(length = 36)
    private String referenceId;

    @Comment("원본이름")
    private String originalName;

    @Comment("이름")
    private String name;

    @Comment("경로명")
    private String pathName;

    @Comment("MIME 타입")
    private String mimeType;

    @Comment("크기")
    private long size;

    /**
     * 게시글에 첨부된 파일
     */
    public static Attachment create(String referenceId, Type type, MultipartFile multipartFile) {
        Attachment attachment = new Attachment();
        attachment.referenceId = referenceId;
        attachment.originalName = multipartFile.getOriginalFilename();
        attachment.name = generateName(attachment.originalName);
        attachment.pathName = type.getPath();
        attachment.mimeType = multipartFile.getContentType();
        attachment.size = multipartFile.getSize();
        return attachment;
    }

    private static String generateName(String originName) {
        return String.format("%s_%s", UUID.randomUUID(), originName);
    }

    public boolean isInlineImage() {
        return pathName.equals(Type.INLINE_IMAGE.getPath());
    }

    public boolean isAttachment() {
        return pathName.equals(Type.ATTACHMENT.getPath());
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        PROFILE("/profiles"),
        BACKGROUND_IMAGE("/backgroundImages"),
        INLINE_IMAGE("/images"),
        ATTACHMENT("/attachments");
        private final String path;
    }

}
