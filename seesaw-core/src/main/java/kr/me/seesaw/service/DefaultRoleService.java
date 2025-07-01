package kr.me.seesaw.service;

import kr.me.seesaw.domain.Permission;
import kr.me.seesaw.domain.Role;
import kr.me.seesaw.repository.PermissionRepository;
import kr.me.seesaw.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultRoleService implements RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public Role getRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissions(String roleId) {
        return permissionRepository.findAllByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermission(String roleId, String targetId) {
        return permissionRepository.findByRoleIdAndTargetId(roleId, targetId)
                .orElseThrow(NoSuchElementException::new);
    }

}
