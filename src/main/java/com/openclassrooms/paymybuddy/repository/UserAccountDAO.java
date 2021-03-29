package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountDAO extends JpaRepository<UserAccount, Integer> {
    /**
     * Find a user account by its id.
     * @param id .
     * @return Optional UserAccount
     */
    Optional<UserAccount> findById(int id);

    /**
     * Check if a userAccount exists with this email.
     * @param email .
     * @return true if exists.
     */
    boolean existsByEmail(String email);

    /**
     * Find all user account in database.
     * @return list of all user account.
     */
    List<UserAccount> findAll();

    /**
     * Save and update a userAccount.
     * @param userAccount .
     * @return user account saved.
     */
    UserAccount save(UserAccount userAccount);

    /**
     * Delete a user account by its id.
     * @param id .
     */
    void deleteById(int id);

    /**
     * Find a user account by its email.
     * @param email .
     * @return Optional UserAccount
     */
    Optional<UserAccount> findByEmail(String email);
}
