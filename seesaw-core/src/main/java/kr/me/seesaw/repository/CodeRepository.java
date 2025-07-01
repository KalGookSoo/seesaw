package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, String> {
    List<Code> findByName(String name);
}
