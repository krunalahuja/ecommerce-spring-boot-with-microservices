package com.ecommerce.user_service.service;

import com.ecommerce.user_service.entity.Role;

import java.util.List;

public interface RoleService {
    Role createRole(String roleName);
    Role getRoleByName(String roleName);
    List<Role> getAllRoles();
    void deleteRole(String roleName);
}
