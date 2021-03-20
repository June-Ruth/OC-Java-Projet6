package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import com.openclassrooms.paymybuddy.util.DtoConverter;
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
import java.util.Objects;

@RestController
@RequestMapping("signup")
public class SignUpController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(SignUpController.class);

    private UserAccountService userAccountService;
    private RoleDAO roleDAO; //TODO : faire passer en service
    private PasswordEncoder passwordEncoder;

    public SignUpController(final UserAccountService pUserAccountService,
                             final RoleDAO pRoleDAO,
                             final PasswordEncoder pPasswordEncoder) {
        Objects.requireNonNull(pUserAccountService);
        userAccountService = pUserAccountService;
        roleDAO = pRoleDAO;
        passwordEncoder = pPasswordEncoder;
    }


    //TODO : create user account (and bank account => ok avec Cascade)
    @PostMapping(value = "/signup")
    public ResponseEntity<UserAccount> createUserAccount(@Valid @RequestBody final UserInfoWithoutBalanceDTO userInfoWithoutBalanceDTO) {
        UserAccount userAccount = DtoConverter.convertUserInfoWithoutBalanceDTOtoUserAccount(userInfoWithoutBalanceDTO, roleDAO.findByName("ROLE_USER"));
        userAccount.setPassword(passwordEncoder.encode(userInfoWithoutBalanceDTO.getPassword()));
        userAccountService.saveUserAccount(userAccount);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/profile")
                .path("/{user_id}")
                .buildAndExpand(userAccount.getId())
                .toUri();

        return ResponseEntity.created(location).body(userAccount);
    }
}
