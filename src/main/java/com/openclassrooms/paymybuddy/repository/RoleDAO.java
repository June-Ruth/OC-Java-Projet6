package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<Role, Integer> {
    Role findByName(String name);

    Role save(Role role);
}
