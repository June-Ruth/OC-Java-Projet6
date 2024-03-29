package com.openclassrooms.paymybuddy.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(AdminController.class);

    /**
     * @see UserAccountService
     */
    private UserAccountService userAccountService;
    /**
     * @see TransferService
     */
    private TransferService transferService;

    /**
     * Autowired constructor.
     * @param pUserAccountService .
     * @param pTransferService .
     */
    public AdminController(final UserAccountService pUserAccountService,
                           final TransferService pTransferService) {
        userAccountService = pUserAccountService;
        transferService = pTransferService;
    }

    /**
     * Allows ROLE_ADMIN to see all user accounts with full user information.
     * @return list of all user accounts information
     * @see UserInfoDTO
     */
    @GetMapping(value = "/users")
    public List<UserInfoDTO> getAllUserAccounts() {
        LOGGER.info("Try to get all user accounts");
        List<UserAccount> userAccounts =
                userAccountService.findAllUserAccounts();
        List<UserInfoDTO> result = new ArrayList<>();
        for (UserAccount userAccount : userAccounts) {
            UserInfoDTO userDTO =
                    DtoConverter.convertUserAccountToUserInfoDTO(userAccount);
            result.add(userDTO);
        }

        StringBuilder sb = new StringBuilder();
        for (UserInfoDTO userInfoDTO : result) {
            sb.append(userInfoDTO.toString());
            sb.append("\t");
        }

        LOGGER.info("Return list with all user accounts :\t" + sb.toString());
        return result;
    }

    /**
     * Allows ROLE_ADMIN to see all transfers with full transfer information.
     * @return list of all transfers information
     * @see TransferInformationFullDto
     */
    @GetMapping(value = "/transfers")
    public List<TransferInformationFullDto> getAllTransfers() {
        LOGGER.info("Try to get all transfers");
        List<Transfer> transfers = transferService.findAllTransfers();
        List<TransferInformationFullDto> transferInformationFullDtoList
                = new ArrayList<>();
        for (Transfer transfer : transfers) {
            TransferInformationFullDto transferInformationFullDto =
                    DtoConverter.convertTransferToTransferInformationFullDto(
                            transfer);
            transferInformationFullDtoList.add(transferInformationFullDto);
        }

        StringBuilder sb = new StringBuilder();
        for (TransferInformationFullDto transferDto
                : transferInformationFullDtoList) {
            sb.append(transferDto.toString());
            sb.append("\t");
        }

        LOGGER.info("Return list with all transfers :\t" + sb.toString());

        return transferInformationFullDtoList;
    }

}
