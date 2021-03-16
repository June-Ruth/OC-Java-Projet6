package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;

import java.util.List;

public interface UserAccountService {

    UserAccount findUserAccountById(int id);

    boolean findIfUserAccountExistsByEmail(String email);

    List<UserAccount> findAllUserAccounts();

    UserAccount saveUserAccount(UserAccount userAccount);

    UserAccount updateUserAccount(UserAccount userAccount);

    boolean deleteUserAccountById(int id);

    List<UserAccount> findUserNetwork(int id);

    UserAccount saveNewConnectionInUserNetwork(int user_id, String connection_email);

    UserAccount saveDeleteConnectionInUserNetwork(int user_id, int connection_id);

    List<Transfer> findUserTransfers(int id);

    boolean existsConnectionById(int id);

    UserAccount findUserAccountByEmail(String email);
}
