package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {
    //TODO : Logger
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(RoleServiceImpl.class);
    /**
     * @see RoleDAO
     */
    private final RoleDAO roleDAO;

    /**
     * Autowired constructor for RoleService.
     * @param pRoleDAO .
     */
    public RoleServiceImpl(final RoleDAO pRoleDAO) {
        roleDAO = pRoleDAO;
    }

    /**
     * Find a role by its name.
     * @param name .
     * @return role if found
     */
    @Override
    public Role findRoleByName(final String name) {
        return roleDAO.findByName(name);
    }

    /**
     * Save a new role.
     * @param role .
     * @return Role saved.
     */
    @Transactional
    @Override
    public Role saveRole(final Role role) {
        return roleDAO.save(role);
    }
}
