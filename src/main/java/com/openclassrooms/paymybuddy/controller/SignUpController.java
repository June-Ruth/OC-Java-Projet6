package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.Role;
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

@RestController
@RequestMapping("signup")
public class SignUpController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(SignUpController.class);
    /**
     * @see UserAccountService
     */
    private UserAccountService userAccountService;
    /**
     * @see RoleService
     */
    private RoleService roleService;
    /**
     * @see PasswordEncoder
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Autowired constructor.
     * @param pUserAccountService .
     * @param pRoleService .
     * @param pPasswordEncoder .
     */
    public SignUpController(final UserAccountService pUserAccountService,
                            final RoleService pRoleService,
                            final PasswordEncoder pPasswordEncoder) {
        userAccountService = pUserAccountService;
        roleService = pRoleService;
        passwordEncoder = pPasswordEncoder;
    }

    /**
     * Allows new users to create an account.
     * @param userInfo with all valid information
     * @return a new account
     */
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<UserAccount> createUserAccount(
            @Valid @RequestBody final UserInfoWithoutBalanceDTO userInfo) {
        LOGGER.info("Try to create user account with user information :\t" + userInfo.toString());
        Role role = roleService.findRoleByName("ROLE_USER");
        List<Role> userRole = new ArrayList<>();
        userRole.add(role);

        UserAccount userAccount =
                new UserAccount(userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getEmail(),
                passwordEncoder.encode(userInfo.getPassword()),
                userRole,
                userInfo.getBankAccount(),
                0, new ArrayList<>(), new ArrayList<>());

        userAccountService.saveNewUserAccount(userAccount);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/profile")
                .path("/{user_id}")
                .buildAndExpand(userAccount.getId())
                .toUri();

        LOGGER.info("Success to create user account with user information : \t" + userInfo.toString());
        return ResponseEntity.created(location).body(userAccount);
    }
}
