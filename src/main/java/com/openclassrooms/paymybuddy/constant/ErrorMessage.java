package com.openclassrooms.paymybuddy.constant;

public final class ErrorMessage {
    /**
     * Error message when email was not found.
     */
    public static final String EMAIL_NOT_FOUND = "Email was not found.";
    /**
     * Error message when balance money is not enough to make a transfer.
     */
    public static final String NOT_ENOUGH_MONEY =
            "You don't have enough money on your account to do this action.";
    /**
     * Error message when user was not found.
     */
    public static final String USER_NOT_FOUND = "User was not found.";
    /**
     * Error message when transfer was not found.
     */
    public static final String TRANSFER_NOT_FOUND = "Transfer was not found.";
    /**
     * Error message when try to save an already existing transfer.
     */
    public static final String CONNECTION_ALREADY_EXISTS =
            "Connection you try to add is already existing.";
    /**
     * Error message when try to add an alredy existing email.
     */
    public static final String EMAIL_ALREADY_EXISTS =
            "An account with this email is already existing.";

    /**
     * Private constructor.
     */
    private ErrorMessage() { }
}
