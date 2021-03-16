package com.openclassrooms.paymybuddy.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.dto.SendingTransferDTO;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TransferController : allow a user to send and see transfers")
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

    private static Transfer transferBetweenUsers;
    private static Transfer transferWithBank;
    private static UserAccount userAccount1User;
    private static UserAccount userAccount2Admin;

    private static List<Transfer> transfers = new ArrayList<>();

    private static RoleDAO roleDAO;

    @BeforeAll
    static void beforeAll() {
        List<Role> userRoles = new ArrayList<>();
        List<Role> adminRoles = new ArrayList<>();
        BankAccount bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1User = new UserAccount("firstName1", "lastName1", "user@test.com",  "password", userRoles, bankAccount1, 0, null, null);
        userAccount2Admin = new UserAccount("firstName2", "lastName2", "admin@test.com",  "password2", adminRoles, bankAccount2, 0, null, null);
        transferBetweenUsers = new Transfer(userAccount1User, userAccount2Admin, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transferWithBank = new Transfer(userAccount1User, userAccount1User, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transferBetweenUsers);
        transfers.add(transferWithBank);
    }

    /*
    sendTransfer() Tests
     */

    @DisplayName("Send transfer to other user as user using valid arguments, valid receiver email and valid amount")
    @Test
    @WithMockUser(username = "user@test.com")
    void sendTransferBetweenUsersAsUserAndValidArgsTest() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin@test.com", "description1", 100);
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User).thenReturn(userAccount2Admin);
        when(transferService.saveTransfer(any(Transfer.class))).thenReturn(transferBetweenUsers);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Send transfer to bank as user using valid arguments and valid amount")
    @Test
    @WithMockUser(username = "user@test.com")
    void sendTransferWithBankAsUserAndValidArgsTest() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin@test.com", "description1", 100);
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User).thenReturn(userAccount1User);
        when(transferService.saveTransfer(any(Transfer.class))).thenReturn(transferWithBank);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Send transfer to other user as user using invalid arguments")
    @Test
    @WithMockUser(username = "user@test.com")
    void sendTransferBetweenUsersAsUserAndInvalidArgsTest() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO(null, "description1", 0);
        // TODO :  Rôle USER && USER.id = user_id && arguments invalides
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //TODO : avec email du receiver qui n'existe pas

    //TODO : avec amount > balance disponible

    @DisplayName("Send transfer as not a user")
    @Test
    @WithMockUser(username = "test@test.com", roles = {""})
    void sendTransferNotAsUserAndValidArgsTest() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin@test.com", "description1", 100);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /*
    getMyTransferAsSender() Tests
     */

    @DisplayName("Get my transfers as a user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getMyTransfersAsSenderAsUserTest() throws Exception {
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(transferService.findTransferBySender(any(UserAccount.class))).thenReturn(transfers);
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isOk());
    }

    @DisplayName("Get my transfers as not a user")
    @Test
    @WithMockUser(username = "test@test.com", roles = {""})
    void getMyTransfersAsSenderAsNotUserTest() throws Exception {
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isForbidden());
    }

    /*
    getTransfer() Test
     */

    @DisplayName("Get a transfer which exists as sender or receiver user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getTransferAsSenderOrReceiverAndTransferExistsTest() throws Exception {
        int transfer_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(transferService.findTransferById(any(Integer.class))).thenReturn(transferBetweenUsers);
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Get a transfer which exists as user but not as sender or receiver")
    @Test
    @WithMockUser(username = "user@test.com")
    void getTransferAsUserNotSenderOrReceiverAndTransferExistsTest() throws Exception {
        int transfer_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(transferService.findTransferById(any(Integer.class))).thenReturn(transferWithBank);
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get a transfer which doesn't exist")
    @Test
    @WithMockUser(username = "user@test.com")
    void getTransferAsUserAndTransferNotExistsTest() throws Exception {
        int transfer_id = 0;
        when(transferService.findTransferById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get a transfer as not a user")
    @Test
    @WithMockUser(username = "test@test.com", roles = {""})
    void getTransferAsNotUserAndTransferExistsTest() throws Exception {
        int transfer_id = 0;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isForbidden());
    }
}
