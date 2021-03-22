package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.TransferType;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.repository.TransferDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {
    //TODO : Logger + Gestion des exceptions
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(TransferServiceImpl.class);
    /**
     * @see TransferDAO
     */
    private final TransferDAO transferDAO;
    /**
     * @see UserAccountService
     */
    private final UserAccountService userAccountService;

    /**
     * Autowired constructor for TransferService.
     * @param pTransferDAO .
     * @param pUserAccountService .
     */
    public TransferServiceImpl(final TransferDAO pTransferDAO,
                               final UserAccountService pUserAccountService) {
        transferDAO = pTransferDAO;
        userAccountService = pUserAccountService;
    }

    /**
     * Save a Transfer transaction.
     * In case of transfer with bank,
     * sender's balance is updated depending on the amount of transaction.
     * In case of transfer between users, sender's and receiver's balance
     * are updated depending on the amount of transaction.
     * Fee are not paid during this transaction.
     * @param transfer .
     * @return transfer which was saved
     */
    @Transactional
    @Override
    public Transfer saveTransfer(final Transfer transfer) {
        transferDAO.save(transfer);
        UserAccount sender = transfer.getSender();
        UserAccount receiver = transfer.getReceiver();
        double amount = transfer.getAmount();
        TransferType type = transfer.getTransferType();
        switch (type) {
            case TRANSFER_WITH_BANK:
                sender.setBalance(sender.getBalance() - amount);
                userAccountService.saveUserAccount(sender);
                break;
            case TRANSFER_BETWEEN_USER:
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + amount);
                userAccountService.saveUserAccount(sender);
                userAccountService.saveUserAccount(receiver);
                break;
                default: break;
        }
        return transfer;
    }

    /**
     * Find a transfer by its id.
     * Return null if not found.
     * @param transferId .
     * @return transfer found.
     */
    @Override
    public Transfer findTransferById(final int transferId) {
        return transferDAO.findById(transferId);
    }

    /**
     * Find the list of transfers by their sender.
     * Return null if no transfer was found.
     * @param sender .
     * @return a list of transfer.
     */
    @Override
    public List<Transfer> findTransferBySender(final UserAccount sender) {
        return transferDAO.findAllBySender(sender);
    }

    /**
     * Find the list of all the transfer present in the database.
     * Return null if no one was found.
     * @return list of transfer.
     */
    @Override
    public List<Transfer> findAllTransfers() {
        return transferDAO.findAll();
    }
}
