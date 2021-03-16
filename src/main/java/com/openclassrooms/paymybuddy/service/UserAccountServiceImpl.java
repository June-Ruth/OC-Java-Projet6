package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.repository.UserAccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(UserAccountServiceImpl.class);

    /**
     * @see UserAccountDAO
     */
    private final UserAccountDAO userAccountDAO;

    /**
     * Public constructor for UserAccountService.
     * Require non null UserAccountDAO.
     * @param pUserAccountDAO not null
     */
    public UserAccountServiceImpl(final UserAccountDAO pUserAccountDAO) {
        Objects.requireNonNull(pUserAccountDAO);
        userAccountDAO = pUserAccountDAO;
    }

    @Override
    public UserAccount findUserAccountById(int id) {
        return userAccountDAO.findById(id);
    }

    @Override
    public boolean findIfUserAccountExistsByEmail(String email) {
        return userAccountDAO.existsByEmail(email);
    }

    @Override
    public List<UserAccount> findAllUserAccounts() {
        return userAccountDAO.findAll();
    }

    @Override
    public UserAccount saveUserAccount(UserAccount userAccount) {
        return userAccountDAO.save(userAccount);
    }

    @Override
    public UserAccount updateUserAccount(UserAccount userAccount) {
        return userAccountDAO.save(userAccount);
    }

    @Override
    public boolean deleteUserAccountById(int id) {
        return userAccountDAO.deleteById(id);
    }

    @Override
    public List<UserAccount> findUserNetwork(int id) {
        UserAccount userAccount = userAccountDAO.findById(id);
        return userAccount.getConnection();
    }

    @Override
    public UserAccount saveNewConnectionInUserNetwork(int user_id, String connection_email) {
        UserAccount userAccount = userAccountDAO.findById(user_id);
        UserAccount connection = userAccountDAO.findByEmail(connection_email);
        List<UserAccount> connections = userAccount.getConnection();
        connections.add(connection);
        userAccountDAO.save(userAccount);
        return connection;
    }

    @Override
    public UserAccount saveDeleteConnectionInUserNetwork(int user_id, int connection_id) {
        UserAccount userAccount = userAccountDAO.findById(user_id);
        UserAccount connection = userAccountDAO.findById(connection_id);
        List<UserAccount> connections = userAccount.getConnection();
        connections.remove(connection);
        UserAccount newUserAccount = userAccountDAO.save(userAccount);
        return newUserAccount;
    }

    @Override
    public List<Transfer> findUserTransfers(int id) {
        UserAccount userAccount = userAccountDAO.findById(id);
        List<Transfer> transfers = userAccount.getTransferLog();
        return transfers;
    }

    @Override
    public boolean existsConnectionById(int id) {
        return userAccountDAO.existsById(id);
    }

    @Override
    public UserAccount findUserAccountByEmail(String email) {
        return userAccountDAO.findByEmail(email);
    }
}
