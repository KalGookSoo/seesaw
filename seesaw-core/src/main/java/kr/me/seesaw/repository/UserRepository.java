package kr.me.seesaw.repository;

import kr.me.seesaw.domain.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * 계정 저장소
 */
public interface UserRepository extends Repository<User, String> {

    User save(User user);

    Optional<User> findByUsername(String username);

}
