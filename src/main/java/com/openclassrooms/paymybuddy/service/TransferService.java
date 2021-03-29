package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;

import java.util.List;

public interface TransferService {
    /**
     * Save a Transfer transaction.
     * @param transfer .
     * @return transfer which was saved
     */
    Transfer saveTransfer(Transfer transfer);
    /**
     * Find a transfer by its id.
     * Return null if not found.
     * @param transferId .
     * @return transfer found.
     */
    Transfer findTransferById(int transferId);
    /**
     * Find the list of transfers by their sender.
     * Return null if no transfer was found.
     * @param sender .
     * @return a list of transfer.
     */
    List<Transfer> findTransferBySender(UserAccount sender);
    /**
     * Find the list of all the transfer present in the database.
     * Return null if no one was found.
     * @return list of transfer.
     */
    List<Transfer> findAllTransfers();

}
