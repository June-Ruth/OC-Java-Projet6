package com.openclassrooms.paymybuddy.util;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.HistoricTransferAsSenderDTO;
import com.openclassrooms.paymybuddy.model.dto.TransferInformationFullDto;
import com.openclassrooms.paymybuddy.model.dto.UserInfoDTO;
import com.openclassrooms.paymybuddy.model.dto.UserRestrictedInfoDTO;

public final class DtoConverter {
    /**
     * Private empty constructor.
     */
    private DtoConverter() { }

    /**
     * Convert an UserAccount to UserRestrictedInfoDTO
     * used for public view access.
     * @param userAccount to convert
     * @return user information for public view access
     */
    public static UserRestrictedInfoDTO
    convertUserAccountToUserRestrictedInfoDTO(
            final UserAccount userAccount) {
        UserRestrictedInfoDTO userDTO = new UserRestrictedInfoDTO();
        userDTO.setFirstName(userAccount.getFirstName());
        userDTO.setLastName(userAccount.getLastName());
        userDTO.setEmail(userAccount.getEmail());
        return userDTO;
    }

    /**
     * Convert an UserAccount to UserInfoDTO used
     * for simplify view for admin and user concerned.
     * @param userAccount to convert
     * @return simplify user information
     */
    public static UserInfoDTO convertUserAccountToUserInfoDTO(
            final UserAccount userAccount) {
        UserInfoDTO userDTO = new UserInfoDTO();
        userDTO.setFirstName(userAccount.getFirstName());
        userDTO.setLastName(userAccount.getLastName());
        userDTO.setEmail(userAccount.getEmail());
        userDTO.setPassword(userAccount.getPassword());
        userDTO.setBankAccount(userAccount.getBankAccount());
        userDTO.setBalance(userAccount.getBalance());
        return userDTO;
    }

    /**
     * Convert a transfer into a TransferInformationFullDto.
     * @param transfer .
     * @return transfer with full information
     * @see TransferInformationFullDto
     */
    public static TransferInformationFullDto
    convertTransferToTransferInformationFullDto(final Transfer transfer) {
        String senderName = transfer.getSender().getFirstName()
                .concat(" ")
                .concat(transfer.getSender().getLastName());
        String receiverName =
                transfer.getReceiver().getFirstName()
                        .concat(" ")
                        .concat(transfer.getReceiver().getLastName());
        TransferInformationFullDto transferInformationFullDto =
                new TransferInformationFullDto(senderName,
                        receiverName,
                        transfer.getDescription(),
                        transfer.getDate(),
                        transfer.getAmount(),
                        transfer.getFee(),
                        transfer.getTransferType());
        return transferInformationFullDto;
    }

    /**
     * Convert a transfer into a historic transfer.
     * @param transfer .
     * @return historic transfer.
     * @see HistoricTransferAsSenderDTO
     */
    public static HistoricTransferAsSenderDTO
    convertTransferToHistoricTransferAsSenderDto(final Transfer transfer) {
        String name = transfer.getReceiver().getFirstName()
                .concat(" ").concat(
                        transfer.getReceiver().getLastName());
        HistoricTransferAsSenderDTO historicTransfer =
                new HistoricTransferAsSenderDTO(name,
                        transfer.getDescription(),
                        transfer.getAmount());
        return historicTransfer;
    }
}
