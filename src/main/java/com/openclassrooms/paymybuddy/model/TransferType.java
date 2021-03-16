package com.openclassrooms.paymybuddy.model;

/**
 * Transfer type available.
 */
public enum TransferType {

    /**
     * Transfer between two users of the application.
     */
    TRANSFER_BETWEEN_USER,

    /**
     * Transfer from one user to its bank account.
     */
    TRANSFER_WITH_BANK
}
