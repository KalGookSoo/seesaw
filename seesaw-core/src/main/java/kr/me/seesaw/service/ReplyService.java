package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateReplyCommand;
import kr.me.seesaw.command.UpdateReplyCommand;
import kr.me.seesaw.domain.Reply;

public interface ReplyService {
    Reply find(String id);
    void create(CreateReplyCommand command);
    void update(String id, UpdateReplyCommand command);
    boolean isOwner(String id, String username);
    void delete(String id);
}
