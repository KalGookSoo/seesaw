package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateReplyCommand;
import kr.me.seesaw.command.UpdateReplyCommand;
import kr.me.seesaw.domain.Reply;
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

    @Override
    public Reply find(String id) {
        return replyRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void create(CreateReplyCommand command) {
        Reply reply = Reply.create(command);
        replyRepository.save(reply);
    }

    @Override
    public void update(String id, UpdateReplyCommand command) {
        Reply reply = replyRepository.getReferenceById(id);
        reply.update(command);
        replyRepository.save(reply);
    }

    @Override
    public boolean isOwner(String id, String username) {
        Reply reply = find(id);
        return reply.getCreatedBy().equals(username);
    }

    @Override
    public void delete(String id) {
        Reply reply = replyRepository.getReferenceById(id);
        replyRepository.delete(reply);
    }

}
