package com.openclassrooms.paymybuddy.model.dto;

import com.openclassrooms.paymybuddy.model.BankAccount;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static com.openclassrooms.paymybuddy.constant.Number.CENT_MILLE;
import static com.openclassrooms.paymybuddy.constant.Number.QUINZE;
import static com.openclassrooms.paymybuddy.constant.Number.SOIXANTE;

public class UserInfoDTO {
    /**
     * First name.
     */
    @NotNull(message = "First name cannot be null")
    @Size(max = QUINZE, message = "First name must be less than 15 characters")
    private String firstName;

    /**
     * Last name.
     */
    @NotNull(message = "Last name cannot be null")
    @Size(max = QUINZE, message = "Last name must be less than 15 characters")
    private String lastName;

    /**
     * Email.
     * Must be unique.
     */
    @Email(message = "Email should be valid")
    @Size(max = SOIXANTE, message = "Email must be less than 60 characters")
    private String email;

    /**
     * Password.
     */
    @NotNull(message = "Password cannot be null")
    private String password;

    /**
     * Associated bank account.
     * @see BankAccount
     */
    @Valid
    @NotNull(message = "Bank account cannot be null")
    private BankAccount bankAccount;

    /**
     * Balance available on user account.
     */
    @PositiveOrZero(message = "Balance cannot be negative")
    @Max(value = CENT_MILLE,
            message = "Balance should not be greater than 1000 000€")
    private double balance;

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

    /**
     * Getter balance.
     * @return balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Setter balance.
     * @param pBalance to set
     */
    public void setBalance(final double pBalance) {
        balance = pBalance;
    }

}
