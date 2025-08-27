package kr.me.seesaw.repository;

import kr.me.seesaw.domain.RoleMapping;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RoleMappingRepository extends Repository<RoleMapping, String> {

    RoleMapping save(RoleMapping userRole);

    List<RoleMapping> findAllByUserId(String userId);

    List<RoleMapping> findAllByUserIdAndSiteId(String userId, String siteId);

    List<RoleMapping> findAllByUserIdAndRoleId(String userId, String roleId);

}
