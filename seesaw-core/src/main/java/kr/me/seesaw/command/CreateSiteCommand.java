package kr.me.seesaw.command;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.vo.Address;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Schema(description = "사이트 생성 커맨드")
@Data
public class CreateSiteCommand implements Serializable {

    @Parameter(description = "이름")
    @Schema(description = "이름", example = "이름")
    private String name;

    @Parameter(description = "URL")
    @Schema(description = "URL", example = "https://example.com")
    private String url;

    @Parameter(description = "설명")
    @Schema(description = "설명", example = "설명")
    private String description;

    @Parameter(description = "배포 코드")
    @Schema(description = "배포 코드", example = "배포 코드")
    private String distributionCode;

    @Parameter(description = "검색 엔진 노출 여부")
    @Schema(description = "검색 엔진 노출 여부", example = "true")
    private boolean searchEngineExposed;

    @Parameter(description = "이미지 노출 여부")
    @Schema(description = "이미지 노출 여부", example = "true")
    private boolean imageExposed;

    @Parameter(description = "태그")
    @Schema(description = "태그", example = "태그1,태그2")
    private String tags;

    @Parameter(description = "주소")
    @Schema(description = "주소")
    private Address address;

    @Parameter(description = "연락처")
    @Schema(description = "연락처", example = "010-1234-5678")
    private String contactNumber;

    @Parameter(description = "프로필 이미지")
    @Schema(description = "프로필 이미지")
    private MultipartFile profileImage;

}
