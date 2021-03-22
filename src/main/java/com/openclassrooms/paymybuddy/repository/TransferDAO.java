package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferDAO extends JpaRepository<Transfer, Integer> {

    Transfer save(Transfer transfer);

    Optional<Transfer> findById (int id);

    List<Transfer> findAllBySender (UserAccount userAccount);

    List<Transfer> findAll();
}
