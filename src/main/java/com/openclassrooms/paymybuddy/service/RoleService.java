package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Role;

public interface RoleService {
    /**
     * Find a role by its name.
     * @param name .
     * @return role if found
     */
    Role findRoleByName(String name);
    /**
     * Save a new role.
     * @param role .
     * @return Role saved.
     */
    Role saveRole(Role role);
}
