package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.UserInfoDTO;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import com.openclassrooms.paymybuddy.model.dto.UserRestrictedInfoDTO;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import com.openclassrooms.paymybuddy.util.DtoConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("profile/{user_id}")
public class ProfileController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(ProfileController.class);

    private UserAccountService userAccountService;
    private RoleDAO roleDAO; //TODO : faire passer en service
    private PasswordEncoder passwordEncoder;

    public ProfileController(final UserAccountService pUserAccountService,
                             final RoleDAO pRoleDAO,
                             final PasswordEncoder pPasswordEncoder) {
        Objects.requireNonNull(pUserAccountService);
        userAccountService = pUserAccountService;
        roleDAO = pRoleDAO;
        passwordEncoder = pPasswordEncoder;
    }

    //TODO : read my own user information
    @GetMapping
    public ResponseEntity<String> getUserAccountInfo(@PathVariable final int user_id) {
        UserAccount userAccount = userAccountService.findUserAccountById(user_id);
        if(userAccount == null) {
            return ResponseEntity.notFound().build();
        } else {
            UserInfoDTO result = DtoConverter.convertUserAccountToUserInfoDTO(userAccount);
            return ResponseEntity.ok().body(result.toString());
        }
    }

    //TODO : update my own user information (except transfer log and network)
    @PutMapping
    public ResponseEntity<String> updateUserAccountInfo(@PathVariable final int user_id,
                                                        @Valid @RequestBody final UserInfoWithoutBalanceDTO userInfoDTO) {
        boolean exists = userAccountService.findUserAccountById(user_id) != null ;
        if (exists) {
            UserAccount userAccount = DtoConverter.convertUserInfoWithoutBalanceDTOtoUserAccount(userInfoDTO, roleDAO.findByName("ROLE_USER")); //voir si pas nécessaire de différencier un existant d'un new
            userAccount.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));
            userAccountService.updateUserAccount(userAccount);
            return ResponseEntity.ok().body(userAccount.toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO : delete my own user account (and bank account = OK avec Cascade)
    @DeleteMapping
    public ResponseEntity<String> deleteUserAccount(@PathVariable final int user_id) {
        boolean exists = userAccountService.findUserAccountById(user_id) != null;
        if (exists) {
            userAccountService.deleteUserAccountById(user_id);
            return ResponseEntity.ok().body("Account was deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO : read only my connections
    @GetMapping(value = "/connections")
    public ResponseEntity<String> getAllUserConnections(@PathVariable final int user_id) {
        boolean exists = userAccountService.findUserAccountById(user_id) != null;
        if(exists) {
            List<UserRestrictedInfoDTO> result = new ArrayList<>();
            List<UserAccount> userAccounts = userAccountService.findUserNetwork(user_id);
            for (UserAccount userAccount : userAccounts) { // TODO ; NPE si userAccounts null
                UserRestrictedInfoDTO userDTO = DtoConverter.convertUserAccountToUserRestrictedInfoDTO(userAccount);
                result.add(userDTO);
            }
            return ResponseEntity.ok().body(result.toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO : ajouter des connections à son network à partir de l'adresse email
    @PutMapping(value = "/connections")
    public ResponseEntity<String> updateToAddNewConnection(@PathVariable final int user_id,
                                                           @RequestParam(name = "email") final String connection_email) {
        boolean user_exists = userAccountService.findUserAccountById(user_id) != null;
        boolean connection_exists = userAccountService.findIfUserAccountExistsByEmail(connection_email);

        if (user_exists && connection_exists) {
            UserAccount connection = userAccountService.saveNewConnectionInUserNetwork(user_id, connection_email);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{connection_id}")
                    .buildAndExpand(connection.getId())
                    .toUri();

            List<UserRestrictedInfoDTO> network = new ArrayList<>(); //à compléter

            return ResponseEntity.created(location).body(network.toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO : supprimer une connections à son network
    @PutMapping(value = "/connections/{connection_id}")
    public ResponseEntity<String> updateToDeleteOldConnection(@PathVariable final int user_id,
                                                              @PathVariable final int connection_id) {
        boolean user_exists = userAccountService.findUserAccountById(user_id) != null;
        boolean connection_exists = userAccountService.existsConnectionById(connection_id);
        if (user_exists && connection_exists) {
            userAccountService.saveDeleteConnectionInUserNetwork(user_id, connection_id);

            List<UserRestrictedInfoDTO> network = new ArrayList<>(); //à compléter
            return ResponseEntity.ok().body(network.toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO : read only my transferlog
    @GetMapping(value = "/transfers")
    public ResponseEntity<String> getAllUserTransfers(@PathVariable final int user_id) {
        boolean exists = userAccountService.findUserAccountById(user_id) != null;
        if (exists) {
            List<Transfer> transfers = userAccountService.findUserTransfers(user_id);
            return ResponseEntity.ok().body(transfers.toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
