package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.UserInfoDTO;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import com.openclassrooms.paymybuddy.model.dto.UserRestrictedInfoDTO;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import com.openclassrooms.paymybuddy.util.DtoConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("profile/{user_id}")
public class ProfileController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(ProfileController.class);

    private UserAccountService userAccountService;
    private PasswordEncoder passwordEncoder;

    public ProfileController(final UserAccountService pUserAccountService,
                             final PasswordEncoder pPasswordEncoder) {
        userAccountService = pUserAccountService;
        passwordEncoder = pPasswordEncoder;
    }

    @GetMapping
    public ResponseEntity<String> getUserAccountInfo(@PathVariable final int user_id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());
        UserAccount userAccount = userAccountService.findUserAccountById(user_id);

        if(userAccount == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccount == userAccountPrincipal) {
            UserInfoDTO result = DtoConverter.convertUserAccountToUserInfoDTO(userAccount);
            return ResponseEntity.ok().body(result.toString());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping
    public ResponseEntity<String> updateUserAccountInfo(@PathVariable final int user_id,
                                                        @Valid @RequestBody final UserInfoWithoutBalanceDTO userInfoDTO) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());
        UserAccount userAccountOld = userAccountService.findUserAccountById(user_id);
        if (userAccountOld == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccountOld == userAccountPrincipal) {
            UserAccount userAccount = new UserAccount(userInfoDTO.getFirstName(),
                                                    userInfoDTO.getLastName(),
                                                    userInfoDTO.getEmail(),
                                                    passwordEncoder.encode(userInfoDTO.getPassword()),
                                                    userAccountOld.getRoles(),
                                                    userInfoDTO.getBankAccount(),
                                                    userAccountOld.getBalance(),
                                                    userAccountOld.getConnection(),
                                                    userAccountOld.getTransferLog());
            userAccountService.updateUserAccount(userAccount);
            return ResponseEntity.ok().body(userAccount.toString());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUserAccount(@PathVariable final int user_id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());
        UserAccount userAccount = userAccountService.findUserAccountById(user_id);
        if (userAccount == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccount == userAccountPrincipal) {
            userAccountService.deleteUserAccountById(user_id);
            return ResponseEntity.ok().body("Account was deleted");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(value = "/connections")
    public ResponseEntity<String> getAllUserConnections(@PathVariable final int user_id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());
        UserAccount userAccount = userAccountService.findUserAccountById(user_id);
        if(userAccount == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccount == userAccountPrincipal){
            List<UserRestrictedInfoDTO> result = new ArrayList<>();
            List<UserAccount> userAccounts = userAccountService.findUserNetwork(user_id);
            for (UserAccount user : userAccounts) {
                UserRestrictedInfoDTO userDTO = DtoConverter.convertUserAccountToUserRestrictedInfoDTO(user);
                result.add(userDTO);
            }
            return ResponseEntity.ok().body(result.toString());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(value = "/connections")
    public ResponseEntity<String> addNewConnection(@PathVariable final int user_id,
                                                   @RequestParam(name = "email") final String connection_email) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());
        UserAccount userAccount = userAccountService.findUserAccountById(user_id);
        UserAccount connection = userAccountService.findUserAccountByEmail(connection_email);

        if (userAccount == null || connection == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccount == userAccountPrincipal) {
            userAccountService.saveNewConnectionInUserNetwork(user_id, connection_email);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{connection_id}")
                    .buildAndExpand(connection.getId())
                    .toUri();

            List<UserRestrictedInfoDTO> network = new ArrayList<>(); //TODO//à compléter + voir si connection est déjà dans le network

            return ResponseEntity.created(location).body(network.toString());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(value = "/connections/{connection_id}")
    public ResponseEntity<String> deleteConnection(@PathVariable final int user_id,
                                                   @PathVariable final int connection_id) {

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());

        UserAccount userAccount = userAccountService.findUserAccountById(user_id);
        UserAccount connection = userAccountService.findUserAccountById(connection_id);
        if (userAccount == null || connection == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccount == userAccountPrincipal) {
            userAccountService.saveDeleteConnectionInUserNetwork(user_id, connection_id);

            List<UserRestrictedInfoDTO> network = new ArrayList<>(); //TODO : à compléter
            return ResponseEntity.ok().body(network.toString());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(value = "/transfers")
    public ResponseEntity<String> getAllUserTransfers(@PathVariable final int user_id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService.findUserAccountByEmail(principal.getUsername());

        UserAccount userAccount = userAccountService.findUserAccountById(user_id);

        if(userAccount == null) {
            return ResponseEntity.notFound().build();
        } else if (userAccount == userAccountPrincipal){
            List<Transfer> transfers = userAccountService.findUserTransfers(user_id);
            return ResponseEntity.ok().body(transfers.toString());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
