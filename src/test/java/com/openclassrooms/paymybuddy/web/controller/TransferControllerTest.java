package com.openclassrooms.paymybuddy.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    private static Transfer transfer1;
    private static Transfer transfer2;
    private static UserAccount userAccount1;
    private static UserAccount userAccount2;

    private static List<Transfer> transfers = new ArrayList<>();

    private static RoleDAO roleDAO;

    @BeforeAll
    static void beforeAll() {
        List<Role> userRole = new ArrayList<>();
        BankAccount bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1 = new UserAccount("firstName1", "lastName1", "user1@mail.com",  "password1", userRole, bankAccount1, 0, null, null);
        userAccount2 = new UserAccount("firstName2", "lastName2", "user2@mail.com",  "password2", userRole, bankAccount2, 0, null, null);
        transfer1 = new Transfer(userAccount1, userAccount2, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transfer2 = new Transfer(userAccount1, userAccount1, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transfer1);
        transfers.add(transfer2);
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void createTransferAsActualUserAndValidArgsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && arguments valides
        when(transferService.saveTransfer(any(Transfer.class))).thenReturn(transfer1);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transfer1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void createTransferAsActualUserAndInvalidArgsTest() throws Exception {
        Transfer invalidTransfer = new Transfer(userAccount1, userAccount2, null, null, 0, 0, TransferType.TRANSFER_WITH_BANK);
        // TODO :  Rôle USER && USER.id = user_id && arguments invalides
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(invalidTransfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void createTransferAsAdminAndValidArgsTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id && arguments valides
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transfer1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void createTransferAsDifferentUserAndValidArgsTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id && arguments valides
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transfer1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getMyTransfersAsSenderAsActualUserTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1);
        when(transferService.findTransferBySender(any(UserAccount.class))).thenReturn(transfers);
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getMyTransfersAsSenderAsAdminTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void getMyTransfersAsSenderAsDifferentUserTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getTransferAsActualUserAndTransferExistsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id (sender or receiver) && transfer_id existant dans DB
        int transfer_id = 0;
        when(transferService.findTransferById(any(Integer.class))).thenReturn(transfer1);
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getTransferAsActualUserAndTransferNotExistsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id (sender or receiver) && transfer_id inexistant dans DB
        int transfer_id = 0;
        when(transferService.findTransferById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isNotFound());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getTransferAsAdminAndTransferExistsTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id (sender or receiver) && transfer_id existant dans DB
        int transfer_id = 0;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void getTransferAsDifferentUserAndTransferExistsTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id (sender or receiver) && transfer_id existant dans DB
        int transfer_id = 0;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isForbidden());
    }


}
