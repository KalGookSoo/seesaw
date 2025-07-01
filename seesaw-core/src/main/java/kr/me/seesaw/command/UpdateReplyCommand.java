package kr.me.seesaw.command;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "댓글 수정 커맨드")
@Data
public class UpdateReplyCommand implements Serializable {

    @Parameter(description = "노출 여부")
    @Schema(description = "노출 여부", example = "true")
    private boolean exposed;

    @Parameter(description = "내용", required = true)
    @Schema(description = "내용", example = "내용")
    @NotNull
    @NotBlank
    private String content;

    @Parameter(description = "첨부파일")
    @Schema(description = "첨부파일", example = "첨부파일")
    private List<MultipartFile> multipartFiles = new ArrayList<>();

}
