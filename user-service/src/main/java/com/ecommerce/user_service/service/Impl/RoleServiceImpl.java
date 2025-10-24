package com.ecommerce.user_service.service.Impl;

import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.exception.RoleAlreadyExistsException;
import com.ecommerce.user_service.exception.RoleNotFoundException;
import com.ecommerce.user_service.repository.RoleRepository;
import com.ecommerce.user_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistsException("Role '" + roleName + "' already exists");
        }
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RoleNotFoundException("Role '" + roleName + "' not found");
        }
        return role;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void deleteRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RoleNotFoundException("Role '" + roleName + "' not found");
        }
        roleRepository.delete(role);
    }
}
