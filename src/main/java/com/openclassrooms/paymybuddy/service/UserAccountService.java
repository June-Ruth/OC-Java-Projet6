package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;

import java.util.List;

public interface UserAccountService {
    /**
     * Find a user account by its id.
     * @param id .
     * @return user account
     */
    UserAccount findUserAccountById(int id);
    /**
     * Find if a user account exists by its email.
     * @param email .
     * @return true if a user account with the email is in database.
     */
    boolean findIfUserAccountExistsByEmail(String email);
    /**
     * Find all the user accounts in database.
     * @return list of user account
     */
    List<UserAccount> findAllUserAccounts();
    /**
     * Save a new user account or save update modification of user account.
     * @param userAccount .
     * @return user account saved.
     */
    UserAccount saveUserAccount(UserAccount userAccount);
    /**
     * Delete a user account by its id.
     * @param id .
     * @return true if delete.
     */
    boolean deleteUserAccountById(int id);
    /**
     * Find user's network.
     * @param id .
     * @return List of all connection.
     */
    List<UserAccount> findUserNetwork(int id);
    /**
     * Add a new connection to user's network by connection email.
     * @param userId .
     * @param connectionEmail .
     * @return user account updated.
     */
    UserAccount saveNewConnectionInUserNetwork(int userId,
                                               String connectionEmail);
    /**
     * Delete a connection to user's network.
     * @param userId .
     * @param connectionId .
     * @return user account updated
     */
    UserAccount saveDeleteConnectionInUserNetwork(int userId, int connectionId);
    /**
     * Find user transfer log by user id.
     * @param id .
     * @return transfer log
     */
    List<Transfer> findUserTransfers(int id);
    /**
     * Find a user account by its email.
     * Return null if not exists.
     * @param email .
     * @return user account.
     */
    UserAccount findUserAccountByEmail(String email);
}
