package kr.me.seesaw.service;

import kr.me.seesaw.model.PermissionModel;
import kr.me.seesaw.model.RoleModel;

import java.util.List;

public interface RoleService {

    RoleModel getRole(String name);

    List<PermissionModel> getPermissions(String roleId);

    PermissionModel getPermission(String roleId, String targetId);

}
