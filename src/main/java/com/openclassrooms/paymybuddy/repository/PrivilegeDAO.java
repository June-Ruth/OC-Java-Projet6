package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeDAO extends JpaRepository<Privilege, Integer> {

    Privilege findByName(String name);

    Privilege save(Privilege privilege);
}
