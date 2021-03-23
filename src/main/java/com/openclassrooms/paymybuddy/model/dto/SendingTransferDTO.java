package com.openclassrooms.paymybuddy.model.dto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import static com.openclassrooms.paymybuddy.constant.Number.CENT_MILLE;
import static com.openclassrooms.paymybuddy.constant.Number.SOIXANTE;

public class SendingTransferDTO {
    /**
     * User account's email which receives the transfer.
     */
    @Valid
    private String receiverEmail;

    /**
     * Description by sender of the transfer.
     */
    @Size(max = SOIXANTE,
            message = "Description must be less than 60 characters")
    private String description;

    /**
     * Amount of transfer without fee.
     */
    @Positive(message = "Amount to transfer cannot be zero or negative")
    @Max(value = CENT_MILLE,
            message = "Amount to transfer should not be greater than 100 000€")
    private double amount;

    /**
     * Public constructor.
     * @param pReceiverEmail .
     * @param pDescription .
     * @param pAmount .
     */
    public SendingTransferDTO(
            final String pReceiverEmail,
            final String pDescription,
            final double pAmount) {
        receiverEmail = pReceiverEmail;
        description = pDescription;
        amount = pAmount;
    }

    /**
     * Getter receiver email.
     * @return receiver email.
     */
    public String getReceiverEmail() {
        return receiverEmail;
    }

    /**
     * Setter receiver email.
     * @param pReceiverEmail .
     */
    public void setReceiverEmail(final String pReceiverEmail) {
        receiverEmail = pReceiverEmail;
    }

    /**
     * Getter description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter description.
     * @param pDescription .
     */
    public void setDescription(final String pDescription) {
        description = pDescription;
    }

    /**
     * Getter Amount.
     * @return amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Setter amount.
     * @param pAmount .
     */
    public void setAmount(final double pAmount) {
        amount = pAmount;
    }
}
