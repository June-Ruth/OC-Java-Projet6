package com.openclassrooms.paymybuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.exception.ElementAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ElementNotFoundException;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ProfileController : allow users to manage their profile")
@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

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
    private static List<UserAccount> connections = new ArrayList<>();


    @BeforeAll
    static void beforeAll() {
        List<Role> roles = new ArrayList<>();
        BankAccount bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1User = new UserAccount("firstName1", "lastName1", "user@test.com",  "password", roles, bankAccount1, 0, connections, new ArrayList<>());
        userAccount2Admin = new UserAccount("firstName2", "lastName2", "admin@test.com",  "password2", roles, bankAccount2, 0, new ArrayList<>(), new ArrayList<>());
        transferBetweenUsers = new Transfer(userAccount1User, userAccount2Admin, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transferWithBank = new Transfer(userAccount1User, userAccount1User, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transferBetweenUsers);
        transfers.add(transferWithBank);
        connections.add(userAccount2Admin);

        userInfoWithoutBalanceDTO = new UserInfoWithoutBalanceDTO();
        userInfoWithoutBalanceDTO.setFirstName(userAccount1User.getFirstName());
        userInfoWithoutBalanceDTO.setLastName(userAccount1User.getLastName());
        userInfoWithoutBalanceDTO.setEmail(userAccount1User.getEmail());
        userInfoWithoutBalanceDTO.setPassword(userAccount1User.getPassword());
        userInfoWithoutBalanceDTO.setBankAccount(userAccount1User.getBankAccount());
    }

    // GET USER'S INFORMATION TEST //

    @DisplayName("Get my user account information as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void getUserAccountInfoAsActualUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Get user account information as other user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getUserAccountInfoAsOtherUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get user account information of inexisting user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getUserAccountInfoAsNotExistsUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenThrow(ElementNotFoundException.class);
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get user account information not as user")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getUserAccountInfoAsNotUserTest() throws Exception {
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    // UPDATE USER'S INFORMATION TEST //

    @DisplayName("Update user account information with valid arguments as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsActualUserAndValidArgsTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        when(userAccountService.saveUserAccount(any(UserAccount.class))).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Update user account information with invalid arguments as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsActualUserAndInvalidArgsTest() throws Exception {
        int user_id = 0;
        UserInfoWithoutBalanceDTO userInfoWithoutBalanceDTO1 = new UserInfoWithoutBalanceDTO();
        userInfoWithoutBalanceDTO1.setFirstName(null);
        userInfoWithoutBalanceDTO1.setLastName(userAccount1User.getLastName());
        userInfoWithoutBalanceDTO1.setEmail(userAccount1User.getEmail());
        userInfoWithoutBalanceDTO1.setPassword(userAccount1User.getPassword());
        userInfoWithoutBalanceDTO1.setBankAccount(userAccount1User.getBankAccount());
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update user account information with valid argument not as owner")
    @Test
    @WithMockUser(username = "admin@test.com")
    void updateUserAccountInfoAsDifferentUserAndValidArgsTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Try to update not existing user")
    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsNotExistsUserAndValidArgsTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenThrow(ElementNotFoundException.class);
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Update user account information with valid argument not as user")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void updateUserAccountInfoAsNotUserAndValidArgsTest() throws Exception {
        int user_id = 0;
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // DELETE USER ACCOUNT TEST //

    @DisplayName("Delete user account as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsActualUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        doNothing().when(userAccountService).deleteUserAccountById(anyInt());
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Delete user account not as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsDifferentUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Delete account of not existing user")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsNotExistsUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenThrow(ElementNotFoundException.class);
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Delete user account not as owner")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deleteUserAccountAsNotUserTest() throws Exception {
        int user_id = 0;
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    // GET ALL USER'S CONNECTIONS TEST //

    @DisplayName("Get all connection as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsActualUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        when(userAccountService.findUserNetwork(anyInt())).thenReturn(connections);
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Get all user connection not as user")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllUserConnectionsAsNotUserTest() throws Exception {
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all user connection not as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsDifferentUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all user connection for not existing user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsNotExistsUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenThrow(ElementNotFoundException.class);
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isNotFound());
    }

    // ADD A NEW CONNECTION TEST //

    @DisplayName("Add new existing connection as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void addNewConnectionAsActualUserAndConnectionExistsTest() throws Exception {
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        when(userAccountService.saveNewConnectionInUserNetwork(anyInt(), anyString())).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isCreated());
    }

    @DisplayName("Add not existing connection as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void addNewConnectionAsActualUserAndConnectionNotExistsTest() throws Exception {
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User).thenThrow(ElementNotFoundException.class);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Add new connection not as user")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void addNewConnectionNotAsUserAndConnectionExistsTest() throws Exception {
        int user_id = 0;
        String email = "connection@mail.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Add new connection not as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void addNewConnectionAsDifferentUserAndConnectionExistsTest() throws Exception {
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount2Admin).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Add a connection already existing in network as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void addNewConnectionAsActualUserAndConnectionAlreadyExistsTest() throws Exception {
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User).thenReturn(userAccount2Admin);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        when(userAccountService.saveNewConnectionInUserNetwork(anyInt(), anyString())).thenThrow(ElementAlreadyExistsException.class);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isBadRequest());
    }

    // DELETE CONNECTION TEST //

    @DisplayName("Delete existing connection as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteConnectionExistsAsActualUserTest() throws Exception {
        int user_id = 0;
        int connection_id = 1;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User).thenReturn(userAccount2Admin);
        when(userAccountService.saveDeleteConnectionInUserNetwork(anyInt(), anyInt())).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Delete not existing connection as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteConnectionNotExistsAsActualUserTest() throws Exception {
        int user_id = 0;
        int connection_id = 1;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User).thenThrow(ElementNotFoundException.class);
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Delete existing connection not as user")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deleteConnectionExistsAsNotUserTest() throws Exception {
        int user_id = 0;
        int connection_id = 1;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Delete existing connection not as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteOldConnectionExistsAsDifferentUserTest() throws Exception {
        int user_id = 0;
        int connection_id = 1;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount2Admin).thenReturn(userAccount2Admin);
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isForbidden());
    }

    //GET ALL USER'S TRANSFERS TEST //

    @DisplayName("Get all transfers as owner (sender and receiver)")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsActualUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount1User);
        when(userAccountService.findUserTransfers(anyInt())).thenReturn(transfers);
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Get all transfers not as user")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllUserTransfersAsNotUserTest() throws Exception {
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all transfers not as owner")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsDifferentUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenReturn(userAccount2Admin);
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all transfers of inexisting user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsNotExistsUserTest() throws Exception {
        int user_id = 0;
        when(userAccountService.findUserAccountByEmail(anyString())).thenReturn(userAccount1User);
        when(userAccountService.findUserAccountById(anyInt())).thenThrow(ElementNotFoundException.class);
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isNotFound());
    }

}
