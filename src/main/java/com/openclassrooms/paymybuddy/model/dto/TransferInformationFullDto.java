package com.openclassrooms.paymybuddy.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.openclassrooms.paymybuddy.model.TransferType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

public class TransferInformationFullDto {
    /**
     * User account's first name and last name which sends the transfer.
     */
    private String sender;

    /**
     * User account's first name and last name which receives the transfer.
     */
    private String receiver;

    /**
     * Description by sender of the transfer.
     */
    private String description;

    /**
     * Date of transfer.
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Amount of transfer without fee.
     */
    private double amount;

    /**
     * Associated fee of the transfer.
     */
    private double fee;

    /**
     * Type of transfer.
     * @see TransferType
     */
    @Enumerated(EnumType.STRING)
    private TransferType transferType;

    /**
     * Public constructor.
     * Parameters are all needed and non nullable.
     * @param pSender .
     * @param pReceiver .
     * @param pDescription .
     * @param pDate .
     * @param pAmount .
     * @param pFee .
     * @param pTransferType .
     */
    public TransferInformationFullDto(final String pSender,
                    final String pReceiver,
                    final String pDescription,
                    final LocalDate pDate,
                    final double pAmount,
                    final double pFee,
                    final TransferType pTransferType) {
        sender = pSender;
        receiver = pReceiver;
        description = pDescription;
        date = pDate;
        amount = pAmount;
        fee = pFee;
        transferType = pTransferType;
    }

    /**
     * Getter sender.
     * @return sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Setter sender.
     * @param pSender to set
     */
    public void setSender(final String pSender) {
        this.sender = pSender;
    }

    /**
     * Getter receiver.
     * @return receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Setter receiver.
     * @param pReceiver to set
     */
    public void setReceiver(final String pReceiver) {
        receiver = pReceiver;
    }

    /**
     * Getter description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter description.
     * @param pDescription to set
     */
    public void setDescription(final String pDescription) {
        description = pDescription;
    }

    /**
     * Getter date.
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Setter date.
     * @param pDate to set
     */
    public void setDate(final LocalDate pDate) {
        date = pDate;
    }

    /**
     * Getter amount.
     * @return amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Setter amount.
     * @param pAmount to set
     */
    public void setAmount(final double pAmount) {
        amount = pAmount;
    }

    /**
     * Getter fee.
     * @return fee
     */
    public double getFee() {
        return fee;
    }

    /**
     * Setter fee.
     * @param pFee .
     */
    public void setFee(final double pFee) {
        fee = pFee;
    }

    /**
     * Getter transfer type.
     * @return transfer type
     */
    public TransferType getTransferType() {
        return transferType;
    }

    /**
     * Setter transfer type.
     * @param pTransferType to set
     */
    public void setTransferType(final TransferType pTransferType) {
        transferType = pTransferType;
    }
}
