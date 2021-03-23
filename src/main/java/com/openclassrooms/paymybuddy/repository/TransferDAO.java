package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferDAO extends JpaRepository<Transfer, Integer> {
    /**
     * Save or update a Transfer.
     * @param transfer .
     * @return transfer saved.
     */
    Transfer save(Transfer transfer);

    /**
     * Find a transfer by its id.
     * @param id .
     * @return Transfer.
     */
    Optional<Transfer> findById(int id);

    /**
     * Find all transfer by their sender.
     * @param userAccount .
     * @return List of transfer.
     */
    List<Transfer> findAllBySender(UserAccount userAccount);

    /**
     * Find all transfer in DB.
     * @return List of transfer.
     */
    List<Transfer> findAll();
}
