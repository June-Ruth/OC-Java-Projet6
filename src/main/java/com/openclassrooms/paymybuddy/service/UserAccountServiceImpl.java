package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
import com.openclassrooms.paymybuddy.exception.ElementAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ElementNotFoundException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.repository.UserAccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(UserAccountServiceImpl.class);
    /**
     * @see UserAccountDAO
     */
    private final UserAccountDAO userAccountDAO;

    /**
     * Autowired constructor for UserAccountService.
     * @param pUserAccountDAO .
     */
    public UserAccountServiceImpl(final UserAccountDAO pUserAccountDAO) {
        userAccountDAO = pUserAccountDAO;
    }

    /**
     * Find a user account by its id.
     * @param id .
     * @return user account
     */
    @Override
    public UserAccount findUserAccountById(final int id) {
        LOGGER.info("Try to find user with id : " + id);
        UserAccount userAccount = userAccountDAO.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        return userAccount;
    }

    /**
     * Find if a user account existis by its email.
     * @param email .
     * @return true if a user account with the email is in database.
     */
    @Override
    public boolean findIfUserAccountExistsByEmail(final String email) {
        LOGGER.info("Check if a user exists with email : " + email);
        return userAccountDAO.existsByEmail(email);
    }

    /**
     * Find all the user accounts in database.
     * @return list of user account
     */
    @Override
    public List<UserAccount> findAllUserAccounts() {
        LOGGER.info("Try to find all user accounts in db");
        return userAccountDAO.findAll();
    }

    /**
     * Update modification of user account.
     * @param userAccount .
     * @return user account saved.
     */
    @Transactional
    @Override
    public UserAccount updateUserAccount(final UserAccount userAccount) {
        LOGGER.info("Try to update a user account with information :\t" + userAccount.toString());
        return userAccountDAO.save(userAccount);
    }

    /**
     * Save a new user account.
     * @param userAccount .
     * @return user account saved.
     */
    @Transactional
    @Override
    public UserAccount saveNewUserAccount(final UserAccount userAccount) {
        LOGGER.info("Try to save user account with information :\t"
                + userAccount.toString());
        String email = userAccount.getEmail();
        if (findIfUserAccountExistsByEmail(email)) {
            LOGGER.error("Cannot save user account, an account with email "
                    + userAccount.getEmail() + " is already existing");
            throw new ElementAlreadyExistsException(
                    ErrorMessage.EMAIL_ALREADY_EXISTS);
        } else {
            LOGGER.info("Success to save user account with information :\t" + userAccount.toString());
            return userAccountDAO.save(userAccount);
        }
    }

    /**
     * Delete a user account by its id.
     * @param id .
     */
    @Transactional
    @Override
    public void deleteUserAccountById(final int id) {
        LOGGER.info("Try to delete user account with id :" + id);
        userAccountDAO.deleteById(id);
    }

    /**
     * Find user's network.
     * @param id .
     * @return List of all connection.
     */
    @Override
    public List<UserAccount> findUserNetwork(final int id) {
        LOGGER.info("Try to find user network for id : " + id);
        UserAccount userAccount = userAccountDAO.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        LOGGER.info("Success to find user network for id : " + id);
        return userAccount.getConnection();
    }

    /**
     * Add a new connection to user's network by connection email.
     * @param userId .
     * @param connectionEmail .
     * @return user account updated.
     */
    @Transactional
    @Override
    public UserAccount saveNewConnectionInUserNetwork(
            final int userId, final String connectionEmail) {
        LOGGER.info("Try to save a new connection with email : "
                + connectionEmail + "for user id : " + userId);
        UserAccount userAccount = userAccountDAO.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        UserAccount connection = userAccountDAO.findByEmail(connectionEmail)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        List<UserAccount> connections = userAccount.getConnection();
        if (connections.contains(connection)) {
            LOGGER.error("Connection with email : " + connectionEmail
                    + " is already in network");
            throw new ElementAlreadyExistsException(
                    ErrorMessage.CONNECTION_ALREADY_EXISTS);
        }
        connections.add(connection);
        userAccountDAO.save(userAccount);
        LOGGER.info("Success to add connection with email "
                + connectionEmail + "for user id : " + userId);
        return connection;
    }

    /**
     * Delete a connection to user's network.
     * @param userId .
     * @param connectionId .
     * @return user account updated
     */
    @Transactional
    @Override
    public UserAccount saveDeleteConnectionInUserNetwork(
            final int userId, final int connectionId) {
        LOGGER.info("Try to delete connection with id : "
                + connectionId + "for user id : " + userId);
        UserAccount userAccount = userAccountDAO.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        UserAccount connection = userAccountDAO.findById(connectionId)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        List<UserAccount> connections = userAccount.getConnection();
        if (!connections.contains(connection)) {
            LOGGER.error("Connection with id : " + connectionId
                    + " is not in network");
            throw new ElementNotFoundException(
                    ErrorMessage.USER_NOT_FOUND);
        }
        connections.remove(connection);
        UserAccount newUserAccount = userAccountDAO.save(userAccount);
        LOGGER.info("Success to delete connection with id "
                + connectionId + "for user id : " + userId);
        return newUserAccount;
    }

    /**
     * Find user transfer log by user id.
     * @param id .
     * @return transfer log
     */
    @Override
    public List<Transfer> findUserTransfers(final int id) {
        LOGGER.info("Try to find transfer log for user id : " + id);
        UserAccount userAccount = userAccountDAO.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
        List<Transfer> transfers = userAccount.getTransferLog();
        LOGGER.info("Success to find transfer log for user id : " + id);
        return transfers;
    }

    /**
     * Find a user account by its email.
     * Return null if not exists.
     * @param email .
     * @return user account.
     */
    @Override
    public UserAccount findUserAccountByEmail(final String email) {
        LOGGER.info("Try to find user account with email " + email);
        return userAccountDAO.findByEmail(email).orElseThrow(
                () -> new ElementNotFoundException(
                        ErrorMessage.USER_NOT_FOUND));
    }
}
