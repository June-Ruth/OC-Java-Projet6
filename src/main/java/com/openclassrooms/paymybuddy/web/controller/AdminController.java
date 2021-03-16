package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.TransferInformationFullDto;
import com.openclassrooms.paymybuddy.model.dto.UserInfoDTO;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import com.openclassrooms.paymybuddy.util.DtoConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AdminController {
    //TODO : Logger + JavaDoc
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(AdminController.class);

    private UserAccountService userAccountService;
    private TransferService transferService;

    public AdminController(final UserAccountService pUserAccountService,
                           final TransferService pTransferService) {
        userAccountService = pUserAccountService;
        transferService = pTransferService;
    }

    @GetMapping(value = "/admin/users")
    public List<UserInfoDTO> getAllUserAccounts() {
        List<UserAccount> userAccounts = userAccountService.findAllUserAccounts();
        List<UserInfoDTO> result = new ArrayList<>();
        for (UserAccount userAccount : userAccounts) {
            UserInfoDTO userDTO = DtoConverter.convertUserAccountToUserInfoDTO(userAccount);
            result.add(userDTO);
        }
        return result;
    }

    @GetMapping(value = "/admin/transfers")
    public List<TransferInformationFullDto> getAllTransfers() {
        List<Transfer> transfers = transferService.findAllTransfers();
        List<TransferInformationFullDto> transferInformationFullDtoList = new ArrayList<>();
        for (Transfer transfer : transfers) {
            String senderName = transfer.getSender().getFirstName().concat(" ").concat(transfer.getSender().getLastName());
            String receiverName = transfer.getReceiver().getFirstName().concat(" ").concat(transfer.getReceiver().getLastName());
            TransferInformationFullDto transferInformationFullDto = new TransferInformationFullDto(senderName, receiverName, transfer.getDescription(), transfer.getDate(), transfer.getAmount(), transfer.getFee(), transfer.getTransferType());
            transferInformationFullDtoList.add(transferInformationFullDto);
        }
        return transferInformationFullDtoList;
    }

}
