package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateAttachmentCommand;
import kr.me.seesaw.domain.Attachment;
import kr.me.seesaw.model.AttachmentModel;
import kr.me.seesaw.repository.AttachmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;

@ActiveProfiles({"test"})
@DataJpaTest
class DefaultAttachmentServiceTest {

    private AttachmentService attachmentService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        String filepath = tempDir.toString();
        attachmentService = new DefaultAttachmentService(filepath, attachmentRepository);
    }

    @Test
    @DisplayName("첨부파일 생성 시 첨부파일 모델을 반환합니다.")
    void createAttachmentShouldReturnAttachmentModel() {
        // given
        String referenceId = UUID.randomUUID().toString();
        Attachment.Type type = Attachment.Type.ATTACHMENT;
        MultipartFile multipartFile = new MockMultipartFile("profileImage", "profile-image.jpeg", MediaType.IMAGE_JPEG_VALUE, new byte[4]);
        CreateAttachmentCommand createAttachmentCommand = new CreateAttachmentCommand(referenceId, type, multipartFile);

        // when
        AttachmentModel attachmentModel = attachmentService.createAttachment(createAttachmentCommand);
        entityManager.flush();

        // then
        Assertions.assertEquals(referenceId, attachmentModel.getReferenceId());
    }

    @Test
    @DisplayName("첨부파일 상세 조회 시 파일이 존재할 경우 첨부파일 모델을 반환합니다.")
    void getAttachmentByIdShouldReturnAttachmentModel() {
        // given
        String referenceId = UUID.randomUUID().toString();
        Attachment.Type type = Attachment.Type.ATTACHMENT;
        MultipartFile multipartFile = new MockMultipartFile("profileImage", "profile-image.jpeg", MediaType.IMAGE_JPEG_VALUE, new byte[4]);
        CreateAttachmentCommand createAttachmentCommand = new CreateAttachmentCommand(referenceId, type, multipartFile);
        String id = attachmentService.createAttachment(createAttachmentCommand).getId();
        entityManager.flush();
        entityManager.clear();

        // when
        AttachmentModel attachmentModel = attachmentService.getAttachmentById(id);

        // then
        Assertions.assertEquals(id, attachmentModel.getId());
    }

    @Test
    @DisplayName("첨부파일 상세 조회 시 파일을 찾을 수 없는 경우 NoSuchElementException을 반환합니다.")
    void getAttachmentByIdShouldThrowNoSuchElementException() {
        // given
        String id = "test-attachment-id";

        // when
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> attachmentService.getAttachmentById(id));

        // then
        Assertions.assertEquals(NoSuchElementException.class, exception.getClass());
    }

}