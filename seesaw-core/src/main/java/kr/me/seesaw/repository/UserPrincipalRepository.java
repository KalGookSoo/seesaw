package kr.me.seesaw.repository;

import kr.me.seesaw.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * 계정 인증 주체 저장소
 */
public interface UserPrincipalRepository extends Repository<User, String> {

    /**
     * 계정명으로 계정 인증 주체를 반환합니다.
     * @param username 계정명
     * @return 계정 인증 주체
     */
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(@NonNull String username);

}
