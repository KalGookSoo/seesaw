package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateReplyCommand;
import kr.me.seesaw.command.UpdateReplyCommand;
import kr.me.seesaw.domain.Reply;
import kr.me.seesaw.model.ReplyModel;
import kr.me.seesaw.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultReplyService implements ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional(readOnly = true)
    @Override
    public ReplyModel find(String id) {
        Reply reply = replyRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return new ReplyModel(reply);
    }

    @Override
    public ReplyModel create(CreateReplyCommand command) {
        Reply reply = Reply.create(command);
        Reply savedReply = replyRepository.save(reply);
        return new ReplyModel(savedReply);
    }

    @Override
    public ReplyModel update(String id, UpdateReplyCommand command) {
        Reply reply = replyRepository.getReferenceById(id);
        reply.update(command);
        Reply updatedReply = replyRepository.save(reply);
        return new ReplyModel(updatedReply);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isOwner(String id, String username) {
        ReplyModel reply = find(id);
        return reply.getCreatedBy().equals(username);
    }

    @Override
    public void delete(String id) {
        Reply reply = replyRepository.getReferenceById(id);
        replyRepository.delete(reply);
    }

}
