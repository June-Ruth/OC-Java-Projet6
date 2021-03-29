package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountDAO extends JpaRepository<BankAccount, Integer> {

}
