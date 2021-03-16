package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.UserInfoDTO;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import com.openclassrooms.paymybuddy.util.DtoConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AdminController {
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

    //TODO ; read all user (ADMIN ONLY)
    @GetMapping(value = "/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserInfoDTO> getAllUserAccounts() {
        List<UserAccount> userAccounts = userAccountService.findAllUserAccounts();

        List<UserInfoDTO> result = new ArrayList<>();
        for (UserAccount userAccount : userAccounts) {
            UserInfoDTO userDTO = DtoConverter.convertUserAccountToUserInfoDTO(userAccount);
            result.add(userDTO);
        }
        return result;
    }

    //TODO ; problème de récursion infini
    //TODO : see all transfer (ADMIN ONLY)
    @GetMapping(value = "/admin/transfers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Transfer> getAllTransfers() {
        List<Transfer> transfers = transferService.findAllTransfers();
        return transfers;
    }

    //TODO : designed an user as admin

}
