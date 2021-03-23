package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<Role, Integer> {
    /**
     * Find a role by its name.
     * @param name .
     * @return Role
     */
    Role findByName(String name);

    /**
     * Save a role.
     * @param role .
     * @return role saved.
     */
    Role save(Role role);
}
