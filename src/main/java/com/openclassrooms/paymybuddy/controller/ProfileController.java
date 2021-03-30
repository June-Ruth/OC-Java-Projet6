package com.openclassrooms.paymybuddy.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("profile/{userId}")
public class ProfileController {
    //TODO : Logger
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(ProfileController.class);
    /**
     * @see UserAccountService
     */
    private UserAccountService userAccountService;
    /**
     * @see PasswordEncoder
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Autowired constructor.
     * @param pUserAccountService .
     * @param pPasswordEncoder .
     */
    public ProfileController(final UserAccountService pUserAccountService,
                             final PasswordEncoder pPasswordEncoder) {
        userAccountService = pUserAccountService;
        passwordEncoder = pPasswordEncoder;
    }

    /**
     * Allows user to see their own account information.
     * @param userId .
     * @return response entity with user information
     * @see UserInfoDTO
     */
    @GetMapping
    public ResponseEntity<String> getUserAccountInfo(
            @PathVariable final int userId) {
        LOGGER.info("Try to get user account information for user id : " + userId);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal =
                userAccountService.findUserAccountByEmail(
                        principal.getUsername());
        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);

        if (userAccount == userAccountPrincipal) {
            UserInfoDTO result =
                    DtoConverter.convertUserAccountToUserInfoDTO(userAccount);
            LOGGER.info("Success to get user account information for user id : " + userId + "\t" + result.toString());
            return ResponseEntity.ok().body(result.toString());
        } else {
            LOGGER.error("Forbidden access to get user information for user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Allows user to update their account information.
     * @param userId .
     * @param userInfoDTO with new information
     * @see UserInfoWithoutBalanceDTO
     * @return response entity with user information
     * @see UserAccount
     */
    @PutMapping
    public ResponseEntity<String> updateUserAccountInfo(
            @PathVariable final int userId,
            @Valid @RequestBody final UserInfoWithoutBalanceDTO userInfoDTO) {
        LOGGER.info("Try to update user account information for user id : " + userId
                + " and information to update are : \t" + userInfoDTO.toString());
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal =
                userAccountService.findUserAccountByEmail(
                        principal.getUsername());
        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);
       if (userAccount == userAccountPrincipal) {
           userAccount.setFirstName(userInfoDTO.getFirstName());
           userAccount.setLastName(userInfoDTO.getLastName());
           userAccount.setEmail(userInfoDTO.getEmail());
           userAccount.setPassword(passwordEncoder.encode(
                   userInfoDTO.getPassword()));
           userAccount.setBankAccount(userInfoDTO.getBankAccount());

           userAccountService.updateUserAccount(userAccount);
           LOGGER.info("Success to update user account information for user id : " + userId
                    + "and new information are " + userAccount.toString());
           return ResponseEntity.ok().body(userAccount.toString());
        } else {
            LOGGER.error("Forbidden access to update user information for user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Allows user to delete their own account.
     * @param userId .
     * @return response entity HTTP 200 if ok
     */
    @DeleteMapping
    public ResponseEntity<String> deleteUserAccount(
            @PathVariable final int userId) {
        LOGGER.info("Try to delete user account for user id : " + userId);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal =
                userAccountService.findUserAccountByEmail(
                        principal.getUsername());
        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);
        if (userAccount == userAccountPrincipal) {
            userAccountService.deleteUserAccountById(userId);
            LOGGER.info("Success to delete user account account for user id : " + userId);
            return ResponseEntity.ok().body("Account was deleted");
        } else {
            LOGGER.error("Forbidden access to delete user account for user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Allows user to see all their connections.
     * @param userId .
     * @return list with all connections
     * @see UserRestrictedInfoDTO
     */
    @GetMapping(value = "/connections")
    public ResponseEntity<String> getAllUserConnections(
            @PathVariable final int userId) {
        LOGGER.info("Try to get user network for user id : " + userId);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal =
                userAccountService.findUserAccountByEmail(
                        principal.getUsername());
        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);
        if (userAccount == userAccountPrincipal) {
            List<UserRestrictedInfoDTO> result = new ArrayList<>();
            List<UserAccount> userAccounts =
                    userAccountService.findUserNetwork(userId);
            for (UserAccount user : userAccounts) {
                UserRestrictedInfoDTO userDTO = DtoConverter
                        .convertUserAccountToUserRestrictedInfoDTO(user);
                result.add(userDTO);
            }
            LOGGER.info("Success to get user network for user id : " + userId + "\t" + result.toString());
            return ResponseEntity.ok().body(result.toString());
        } else {
            LOGGER.error("Forbidden access to get user network for user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Allows user to add new connection to their network.
     * @param userId .
     * @param connectionEmail to add
     * @return 201 created if ok and list of network updated.
     * @see UserRestrictedInfoDTO
     */
    @PutMapping(value = "/connections")
    public ResponseEntity<String> addNewConnection(
            @PathVariable final int userId,
            @RequestParam(name = "email") final String connectionEmail) {
        LOGGER.info("Try to add new connection to network of user id : " + userId
                + " and connection email : " + connectionEmail);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal =
                userAccountService.findUserAccountByEmail(
                        principal.getUsername());
        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);
        UserAccount connection =
                userAccountService.findUserAccountByEmail(connectionEmail);

        if (userAccount == userAccountPrincipal) {
            userAccountService.saveNewConnectionInUserNetwork(
                    userId, connectionEmail);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{connectionId}")
                    .buildAndExpand(connection.getId())
                    .toUri();

            List<UserRestrictedInfoDTO> network = new ArrayList<>();
            for (UserAccount user : userAccount.getConnection()) {
                UserRestrictedInfoDTO userDto = DtoConverter
                        .convertUserAccountToUserRestrictedInfoDTO(user);
                network.add(userDto);
            }

            LOGGER.info("Success to add connection to network of user id : " + userId + "\t"
                    + " and connection email :" + connectionEmail);

            return ResponseEntity.created(location).body(network.toString());
        } else {
            LOGGER.error("Forbidden access to add new connection to network of user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Allows user to delete one of their connection.
     * @param userId .
     * @param connectionId to delete
     * @return HTTP 200 if ok
     */
    @PutMapping(value = "/connections/{connectionId}")
    public ResponseEntity<String> deleteConnection(
            @PathVariable final int userId,
            @PathVariable final int connectionId) {
        LOGGER.info("Try to delete connection id : " + connectionId + " of user id : " + userId);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal =
                userAccountService.findUserAccountByEmail(
                        principal.getUsername());

        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);
        if (userAccount == userAccountPrincipal) {
            userAccountService.findUserAccountById(connectionId);
            userAccountService.saveDeleteConnectionInUserNetwork(
                    userId, connectionId);

            List<UserRestrictedInfoDTO> network = new ArrayList<>();
            for (UserAccount user : userAccount.getConnection()) {
                UserRestrictedInfoDTO userDto = DtoConverter
                        .convertUserAccountToUserRestrictedInfoDTO(user);
                network.add(userDto);
            }
            LOGGER.info("Success to delete connection id : " + connectionId + " of user id : " + userId);
            return ResponseEntity.ok().body(network.toString());
        } else {
            LOGGER.error("Forbidden access to delete connection of user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Allows user to see all their transfers as sender or receiver.
     * @param userId .
     * @return list of all transfers
     */
    @GetMapping(value = "/transfers")
    public ResponseEntity<String> getAllUserTransfers(
            @PathVariable final int userId) {
        LOGGER.info("Try to get transfer log for user id : " + userId);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount userAccountPrincipal = userAccountService
                .findUserAccountByEmail(principal.getUsername());

        UserAccount userAccount =
                userAccountService.findUserAccountById(userId);

        if (userAccount == userAccountPrincipal) {
            List<Transfer> transfers =
                    userAccountService.findUserTransfers(userId);
            LOGGER.info("Success to get transfer log for user id : " + userId);
            return ResponseEntity.ok().body(transfers.toString());
        } else {
            LOGGER.error("Forbidden access to get transfer log for user id : " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
