package kr.me.seesaw.repository;

import kr.me.seesaw.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewRepository extends JpaRepository<View, String> {
    List<View> findAllByArticleIdIn(List<String> articleIds);
}
