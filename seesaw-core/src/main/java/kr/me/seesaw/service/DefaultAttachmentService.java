package kr.me.seesaw.service;

import kr.me.seesaw.domain.Attachment;
import kr.me.seesaw.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

    @Override
    public Attachment find(String id) {
        return attachmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public String getAbsolutePath(String id) {
        Attachment attachment = find(id);
        return getAbsolutePath(attachment);
    }

    @Override
    public String getAbsolutePath(Attachment attachment) {
        return filepath + attachment.getPathName() + File.separator + attachment.getName();
    }

}
