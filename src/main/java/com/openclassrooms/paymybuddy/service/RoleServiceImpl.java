package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(RoleServiceImpl.class);

    /**
     * @see RoleDAO
     */
    private final RoleDAO roleDAO;

    /**
     * Public constructor for RoleService.
     * Require non null RoleDAO.
     * @param pRoleDAO not null
     */
    public RoleServiceImpl(final RoleDAO pRoleDAO) {
        Objects.requireNonNull(pRoleDAO);
        roleDAO = pRoleDAO;
    }

    @Override
    public Role findRoleByName(String name) {
        return roleDAO.findByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        return roleDAO.save(role);
    }
}
