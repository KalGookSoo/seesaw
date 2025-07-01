package kr.me.seesaw.service;

import kr.me.seesaw.domain.Permission;
import kr.me.seesaw.domain.Role;

import java.util.List;

public interface RoleService {

    Role getRole(String name);

    List<Permission> getPermissions(String roleId);

    Permission getPermission(String roleId, String targetId);

}
