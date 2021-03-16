package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AdminController : allow Admin to access specific information")
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @MockBean
    private TransferService transferService;

    @MockBean
    private UserAccountService userAccountService;

    private static Transfer transferBetweenUsers;
    private static Transfer transferWithBank;
    private static UserAccount userAccount1User;
    private static UserAccount userAccount2Admin;

    private static List<Transfer> transfers = new ArrayList<>();
    private static List<UserAccount> userAccounts = new ArrayList<>();


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
        userAccounts.add(userAccount1User);
        userAccounts.add(userAccount2Admin);
    }

    @DisplayName("Get all user account information as admin")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllUserAccountsAsAdminTest() throws Exception {
        when(userAccountService.findAllUserAccounts()).thenReturn(userAccounts);
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @DisplayName("Get all user account information as user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserAccountsAsUserTest() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all transfers information as admin")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllTransfersAsAdminTest() throws Exception {
        when(transferService.findAllTransfers()).thenReturn(transfers);
        mockMvc.perform(get("/admin/transfers"))
                .andExpect(status().isOk());
    }

    @DisplayName("Get all transfers information as user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllTransfersAsUserTest() throws Exception {
        mockMvc.perform(get("/admin/transfers"))
                .andExpect(status().isForbidden());
    }
}
