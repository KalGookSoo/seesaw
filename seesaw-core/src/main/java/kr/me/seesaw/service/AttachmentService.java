package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateAttachmentCommand;
import kr.me.seesaw.model.AttachmentModel;

public interface AttachmentService {

    AttachmentModel createAttachment(CreateAttachmentCommand command);

    AttachmentModel getAttachmentById(String id);

    String getAbsolutePath(String pathname, String name);

}
