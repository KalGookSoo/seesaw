package kr.me.seesaw.repository;

import kr.me.seesaw.domain.MenuRole;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;

public interface MenuRoleRepository extends Repository<MenuRole, String> {

    MenuRole save(MenuRole menuRole);

    List<MenuRole> findAllByMenuIdIn(Collection<String> menuIds);

    List<MenuRole> findAllByRoleIdIn(Collection<String> roleIds);

}
