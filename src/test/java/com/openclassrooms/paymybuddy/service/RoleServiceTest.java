package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class RoleServiceTest {

    @Mock
    private static RoleDAO roleDAO;

    private static RoleService roleService;

    private Role role;

    @BeforeEach
    void beforeEach() {
        roleService = new RoleServiceImpl(roleDAO);
        role = new Role("TEST");
    }

    @Test
    void findRoleByName() {
        when(roleDAO.findByName(anyString())).thenReturn(role);
        roleService.findRoleByName(role.getName());
        verify(roleDAO, times(1)).findByName(anyString());
    }

    @Test
    void saveRole() {
        when(roleDAO.save(any(Role.class))).thenReturn(role);
        roleService.saveRole(role);
        verify(roleDAO, times(1)).save(any(Role.class));
    }
}
