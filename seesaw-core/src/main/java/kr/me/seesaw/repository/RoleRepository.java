package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Role;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends Repository<Role, String> {

    Role save(Role role);

    List<Role> findAllByIdIn(Collection<String> ids);

    Optional<Role> findByName(String name);

}
