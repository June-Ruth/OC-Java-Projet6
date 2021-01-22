package com.openclassrooms.paymybuddy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Person model for each user.
 */
@Entity
@Table(name = "person")
public class Person {

    /**
     * ID, generated by DataBase.
     * Use as primary key in DataBase.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "person_id")
    private int id;

    /**
     * First name.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Last name.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Email.
     * Must be unique.
     */
    @Column(name = "email")
    private String email;

    /**
     * Password.
     */
    @Column(name = "password")
    private String password;

    /**
     * Associated bank account.
     * @see BankAccount
     */
    @OneToOne
    @JoinColumn(name = "bank_account_rib", nullable = false)
    private BankAccount bankAccount;

    /**
     * Public constructor.
     * Parameters are all needed and non nullable.
     * @param pFirstName .
     * @param pLastName .
     * @param pEmail .
     * @param pPassword .
     * @param pBankAccount .
     */
    public Person(final String pFirstName,
                  final String pLastName,
                  final String pEmail,
                  final String pPassword,
                  final BankAccount pBankAccount) {
        firstName = pFirstName;
        lastName = pLastName;
        email = pEmail;
        password = pPassword;
        bankAccount = pBankAccount;
    }

    /**
     * Getter ID.
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Setter ID.
     * ID is auto-generated, should not be accessible.
     * @param pId to set
     */
    private void setId(final int pId) {
        id = pId;
    }

    /**
     * Getter first name.
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter first name.
     * @param pFirstName to set.
     */
    public void setFirstName(final String pFirstName) {
        firstName = pFirstName;
    }

    /**
     * Getter last name.
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter last name.
     * @param pLastName to set
     */
    public void setLastName(final String pLastName) {
        lastName = pLastName;
    }

    /**
     * Getter email.
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter email.
     * @param pEmail to set
     */
    public void setEmail(final String pEmail) {
        email = pEmail;
    }

    /**
     * Getter password.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter password.
     * @param pPassword to set
     */
    public void setPassword(final String pPassword) {
        password = pPassword;
    }

    /**
     * Getter Bank Account.
     * @return bank account
     */
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * Setter Bank Account.
     * @param pBankAccount to set
     */
    public void setBankAccount(final BankAccount pBankAccount) {
        bankAccount = pBankAccount;
    }
}
