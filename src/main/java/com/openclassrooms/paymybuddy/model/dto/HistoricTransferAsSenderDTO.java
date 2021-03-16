package com.openclassrooms.paymybuddy.model.dto;

import com.openclassrooms.paymybuddy.model.UserAccount;

public class HistoricTransferAsSenderDTO {
    /**
     * User account's first name and last name which receives the transfer.
     */
    private String receiver;
    /**
     * Description by sender of the transfer.
     */
    private String description;
    /**
     * Amount of transfer without fee.
     */
    private double amount;

    public HistoricTransferAsSenderDTO(String receiver, String description, double amount) {
        this.receiver = receiver;
        this.description = description;
        this.amount = amount;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
