package com.openclassrooms.paymybuddy.model.dto;

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

    /**
     * Public constructor.
     * @param pReceiver .
     * @param pDescription .
     * @param pAmount .
     */
    public HistoricTransferAsSenderDTO(
            final String pReceiver,
            final String pDescription,
            final double pAmount) {
        receiver = pReceiver;
        description = pDescription;
        amount = pAmount;
    }

    /**
     * Getter receiver.
     * @return receiver.
     */
    public String getReceiver() {
        return receiver;
    }
    /**
     * Setter receiver.
     * @param pReceiver .
     */
    public void setReceiver(final String pReceiver) {
        receiver = pReceiver;
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

    /**
     * To String.
     * @return string information
     */
    @Override
    public String toString() {
        return "HistoricTransferAsSenderDTO{"
                + "receiver='" + receiver + '\''
                + ", description='" + description + '\''
                + ", amount=" + amount
                + '}';
    }
}
