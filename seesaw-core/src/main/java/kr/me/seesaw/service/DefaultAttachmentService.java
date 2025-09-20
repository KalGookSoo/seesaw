package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateAttachmentCommand;
import kr.me.seesaw.core.file.FileIOService;
import kr.me.seesaw.domain.Attachment;
import kr.me.seesaw.model.AttachmentModel;
import kr.me.seesaw.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

@Transactional
@Service
public class DefaultAttachmentService implements AttachmentService {

    private final String filepath;

    private final AttachmentRepository attachmentRepository;

    public DefaultAttachmentService(
            @Value("${kr.me.seesaw.filepath}") String filepath,
            AttachmentRepository attachmentRepository
    ) {
        this.filepath = filepath;
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * 요청된 바이너리 데이터를 파일로 쓰고 데이터베이스 모델로 영속화 후 첨부파일 모델로 변환하여 반환합니다.
     * 입출력 예외 시 IllegalArgumentException으로 wrapping하여 던집니다.
     *
     * @param command 첨부파일 생성 커맨드
     * @return 첨부파일 모델
     * @throws IllegalArgumentException 예외
     */
    @Override
    public AttachmentModel createAttachment(CreateAttachmentCommand command) throws IllegalArgumentException {
        Attachment attachment = Attachment.create(command.getReferenceId(), command.getType(), command.getMultipartFile());
        byte[] bytes;
        try {
            bytes = command.getMultipartFile().getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), bytes);
        attachmentRepository.save(attachment);
        return new AttachmentModel(attachment);
    }

    @Transactional(readOnly = true)
    @Override
    public AttachmentModel getAttachmentById(String id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 첨부파일: " + id));
        return new AttachmentModel(attachment);
    }

    @Override
    public String getAbsolutePath(String pathname, String name) {
        return filepath + pathname + File.separator + name;
    }

    private void writeFile(String pathname, byte[] bytes) {
        try {
            FileIOService.write(pathname, bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
