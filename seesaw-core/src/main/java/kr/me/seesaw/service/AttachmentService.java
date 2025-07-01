package kr.me.seesaw.service;

import kr.me.seesaw.domain.Attachment;

public interface AttachmentService {
    Attachment find(String id);
    String getAbsolutePath(String id);
    String getAbsolutePath(Attachment attachment);
}
