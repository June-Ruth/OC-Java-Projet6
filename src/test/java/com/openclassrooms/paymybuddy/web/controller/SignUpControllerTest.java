package com.openclassrooms.paymybuddy.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@DisplayName("SignUpController : allow new users to create account")
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @MockBean
    private UserAccountService userAccountService;

    private static Transfer transferBetweenUsers;
    private static Transfer transferWithBank;
    private static UserAccount userAccount1User;
    private static UserAccount userAccount2Admin;
    private static UserInfoWithoutBalanceDTO userInfoWithoutBalanceDTO;

    private static List<Transfer> transfers = new ArrayList<>();


    @BeforeAll
    static void beforeAll() {
        List<Role> roles = new ArrayList<>();
        BankAccount bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1User = new UserAccount("firstName1", "lastName1", "user@test.com",  "password", roles, bankAccount1, 0, null, null);
        userAccount2Admin = new UserAccount("firstName2", "lastName2", "admin@test.com",  "password2", roles, bankAccount2, 0, null, null);
        transferBetweenUsers = new Transfer(userAccount1User, userAccount2Admin, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transferWithBank = new Transfer(userAccount1User, userAccount1User, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transferBetweenUsers);
        transfers.add(transferWithBank);

        userInfoWithoutBalanceDTO = new UserInfoWithoutBalanceDTO();
        userInfoWithoutBalanceDTO.setFirstName(userAccount1User.getFirstName());
        userInfoWithoutBalanceDTO.setLastName(userAccount1User.getLastName());
        userInfoWithoutBalanceDTO.setEmail(userAccount1User.getEmail());
        userInfoWithoutBalanceDTO.setPassword(userAccount1User.getPassword());
        userInfoWithoutBalanceDTO.setBankAccount(userAccount1User.getBankAccount());
    }

    @Test
    void createAccountWithValidArgsAndEmailNotExistsTest() throws Exception {
        // TODO : arguments valides && adresse mail inexistante dans DB
        when(userAccountService.findIfUserAccountExistsByEmail(any(String.class))).thenReturn(false);
        when(userAccountService.saveUserAccount(any(UserAccount.class))).thenReturn(userAccount1User);
        mockMvc.perform(post("/signup")
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void createAccountWithInvalidArgsAndEmailNotExistsTest() throws Exception {
        //TODO : argement invalides
        UserInfoWithoutBalanceDTO userInfoWithoutBalanceDTO1 = new UserInfoWithoutBalanceDTO();
        userInfoWithoutBalanceDTO1.setFirstName(null);
        userInfoWithoutBalanceDTO1.setLastName(userAccount1User.getLastName());
        userInfoWithoutBalanceDTO1.setEmail(userAccount1User.getEmail());
        userInfoWithoutBalanceDTO1.setPassword(userAccount1User.getPassword());
        userInfoWithoutBalanceDTO1.setBankAccount(userAccount1User.getBankAccount());
        mockMvc.perform(post("/signup")
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
