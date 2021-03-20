package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Role;

public interface RoleService {
    Role findRoleByName(String name);

    Role saveRole(Role role);
}
