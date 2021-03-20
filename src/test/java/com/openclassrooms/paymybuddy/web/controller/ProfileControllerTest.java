package com.openclassrooms.paymybuddy.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.service.UserAccountService;
 import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController : allow users to manage their profile")
@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private RoleDAO roleDAO;

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
        userAccount1User = new UserAccount("firstName1", "lastName1", "user@test.com",  "password", roles, bankAccount1, 0, null, null);
        userAccount2Admin = new UserAccount("firstName2", "lastName2", "admin@test.com",  "password2", roles, bankAccount2, 0, null, null);
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

    @Test
    @WithMockUser(username = "user@test.com")
    void getUserAccountInfoAsActualUserTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getUserAccountInfoAsAdminTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void getUserAccountInfoAsDifferentUserTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getUserAccountInfoAsNotExistsUserTest() throws Exception {
        // TODO : user_id inexistant dans DB
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(get("/profile/{user_id}", user_id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsActualUserAndValidArgsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && arguments valides
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.updateUserAccount(any(UserAccount.class))).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsActualUserAndInvalidArgsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && arguments invalides
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

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void updateUserAccountInfoAsAdminAndValidArgsTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id && arguments valides
        int user_id = 0;
        mockMvc.perform(put("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsDifferentUserAndValidArgsTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id && arguments valides
        int user_id = 0;
        mockMvc.perform(put("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateUserAccountInfoAsNotExistsUserAndValidArgsTest() throws Exception {
        // TODO : user_id inexistant dans DB && arguments valides
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(put("/profile/{user_id}", user_id)
                .content(new ObjectMapper().writeValueAsString(userInfoWithoutBalanceDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsActualUserTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.deleteUserAccountById(any(Integer.class))).thenReturn(true);
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deleteUserAccountAsAdminTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsDifferentUserTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsNotExistsUserTest() throws Exception {
        // TODO : user_id inexistant dans DB
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(delete("/profile/{user_id}", user_id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsActualUserTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.findUserNetwork(any(Integer.class))).thenReturn(connections);
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllUserConnectionsAsAdminTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsDifferentUserTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsNotExistsUserTest() throws Exception {
        // TODO : user_id inexistant dans DB
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        when(userAccountService.findUserNetwork(any(Integer.class))).thenReturn(connections);
        mockMvc.perform(get("/profile/{user_id}/connections", user_id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateToAddNewConnectionAsActualUserAndConnectionExistsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && connection_mail existant dans DB
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.findIfUserAccountExistsByEmail(any(String.class))).thenReturn(true);
        when(userAccountService.saveNewConnectionInUserNetwork(any(Integer.class), any(String.class))).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateToAddNewConnectionAsActualUserAndConnectionNotExistsTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && connection_mail inexistant dans DB
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.findIfUserAccountExistsByEmail(any(String.class))).thenReturn(false);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isNotFound());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void updateToAddNewConnectionAsAdminAndConnectionExistsTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id && connection_mail existant dans DB
        int user_id = 0;
        String email = "connection@mail.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isForbidden());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void updateToAddNewConnectionAsDifferentUserAndConnectionExistsTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id && connection_mail existant dans DB
        int user_id = 0;
        String email = "connection@mail.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateToAddNewConnectionAsNotExistsUserAndConnectionExistsTest() throws Exception {
        // TODO : user_id inexistant dans DB
        int user_id = 0;
        String email = "connection@mail.com";
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, user_id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateToDeleteOldConnectionExistsAsActualUserTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && connection_id existant dans network
        int user_id = 0;
        int connection_id = 1;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.existsConnectionById(any(Integer.class))).thenReturn(true);
        when(userAccountService.saveDeleteConnectionInUserNetwork(any(Integer.class), any(Integer.class))).thenReturn(userAccount1User);
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateToDeleteOldConnectionNotExistsAsActualUserTest() throws Exception {
        // TODO : Rôle USER && USER.id = user_id && connection_id inexistant dans network
        int user_id = 0;
        int connection_id = 1;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.existsConnectionById(any(Integer.class))).thenReturn(false);
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isNotFound());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void updateToDeleteOldConnectionExistsAsAdminTest() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id && connection_id existant dans network
        int user_id = 0;
        int connection_id = 1;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isForbidden());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void updateToDeleteOldConnectionExistsAsDifferentUserTest() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id && connection_id existant dans network
        int user_id = 0;
        int connection_id = 1;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateToDeleteOldConnectionExistsAsNotExistsUserTest() throws Exception {
        // TODO : user_id inexistant dans DB
        int user_id = 0;
        int connection_id = 1;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        when(userAccountService.existsConnectionById(any(Integer.class))).thenReturn(true);
        mockMvc.perform(put("/users/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsActualUser() throws Exception {
        // TODO : Rôle USER && USER.id = user_id
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(userAccount1User);
        when(userAccountService.findUserTransfers(any(Integer.class))).thenReturn(transfers);
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllUserTransfersAsAdmin() throws Exception {
        // TODO : Rôle ADMIN && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isOk());
    }

    @Disabled
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsDifferentUser() throws Exception {
        // TODO : Rôle USER && USER.id ≠ user_id
        int user_id = 0;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsNotExistsUser() throws Exception {
        // TODO : user_id inexistant dans DB
        int user_id = 0;
        when(userAccountService.findUserAccountById(any(Integer.class))).thenReturn(null);
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isNotFound());
    }

}
