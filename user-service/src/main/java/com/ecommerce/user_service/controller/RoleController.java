package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // CREATE ROLE
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestParam String roleName) {
        Role role = roleService.createRole(roleName);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    // GET ROLE BY NAME
    @GetMapping("/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String roleName) {
        Role role = roleService.getRoleByName(roleName);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    // GET ALL ROLES
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // DELETE ROLE
    @DeleteMapping("/{roleName}")
    public ResponseEntity<String> deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }
}
