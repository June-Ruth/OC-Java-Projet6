package com.openclassrooms.paymybuddy.model.dto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class SendingTransferDTO {

    /**
     * User account's email which receives the transfer.
     */
    @Valid
    private String receiverEmail;

    /**
     * Description by sender of the transfer.
     */
    @Size(max = 60, message = "Description must be less than 60 characters")
    private String description;

    /**
     * Amount of transfer without fee.
     */
    @Positive(message = "Amount to transfer cannot be zero or negative")
    @Max(value = 100000, message = "Amount to transfer should not be greater than 100 000€")
    private double amount;

    public SendingTransferDTO(final String receiverEmail, final String description, final double amount) {
        this.receiverEmail = receiverEmail;
        this.description = description;
        this.amount = amount;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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
