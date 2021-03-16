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
     * Transfer between one user and its bank account.
     */
    TRANSFER_WITH_BANK
}
