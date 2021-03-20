package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import com.openclassrooms.paymybuddy.service.RoleService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("signup")
public class SignUpController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(SignUpController.class);

    private UserAccountService userAccountService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public SignUpController(final UserAccountService pUserAccountService,
                            final RoleService pRoleService,
                            final PasswordEncoder pPasswordEncoder) {
        Objects.requireNonNull(pUserAccountService);
        userAccountService = pUserAccountService;
        roleService = pRoleService;
        passwordEncoder = pPasswordEncoder;
    }


    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<UserAccount> createUserAccount(@Valid @RequestBody final UserInfoWithoutBalanceDTO userInfoWithoutBalanceDTO) {
        List<UserAccount> connections = new ArrayList<>();
        List<Transfer> transfers = new ArrayList<>();
        Role role = roleService.findRoleByName("ROLE_USER");
        List<Role> userRole = new ArrayList<>();
        userRole.add(role);

        UserAccount userAccount = new UserAccount(userInfoWithoutBalanceDTO.getFirstName(),
                userInfoWithoutBalanceDTO.getLastName(),
                userInfoWithoutBalanceDTO.getEmail(),
                passwordEncoder.encode(userInfoWithoutBalanceDTO.getPassword()),
                userRole,
                userInfoWithoutBalanceDTO.getBankAccount(),
                0, connections, transfers);

        userAccountService.saveUserAccount(userAccount);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/profile")
                .path("/{user_id}")
                .buildAndExpand(userAccount.getId())
                .toUri();

        return ResponseEntity.created(location).body(userAccount);
    }
}
