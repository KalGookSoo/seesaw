package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String> {
    List<Reply> findAllByArticleIdIn(List<String> articleIds);
}
