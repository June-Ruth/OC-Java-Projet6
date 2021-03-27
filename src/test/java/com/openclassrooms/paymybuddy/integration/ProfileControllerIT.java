package com.openclassrooms.paymybuddy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.dto.UserInfoWithoutBalanceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("create_default_value.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GET USER'S INFORMATION TEST //

    @DisplayName("Get my user account information as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getUserAccountInfoAsActualUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}", 2))
                .andExpect(status().isOk());
    }

    @DisplayName("Get user account information as other user")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getUserAccountInfoAsOtherUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}", 1))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get user account information as anonymous")
    @Test
    @WithAnonymousUser
    void getUserAccountInfoAsNotExistsUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}", 2))
                .andExpect(status().is3xxRedirection());
    }

    @DisplayName("Get user account information not as user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void getUserAccountInfoAsNotUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}", 1))
                .andExpect(status().isForbidden());
    }

    // UPDATE USER'S INFORMATION TEST //

    @DisplayName("Update user account information with valid arguments as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void updateUserAccountInfoAsActualUserAndValidArgsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO userUpdate = new UserInfoWithoutBalanceDTO();
        userUpdate.setFirstName("FirstName2");
        userUpdate.setLastName("LastName2");
        userUpdate.setEmail("user2@test.com");
        userUpdate.setPassword("password");
        userUpdate.setBankAccount(bankAccount);

        mockMvc.perform(put("/profile/{user_id}", 2)
                .content(new ObjectMapper().writeValueAsString(userUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Update user account information with invalid arguments as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void updateUserAccountInfoAsActualUserAndInvalidArgsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO userUpdate = new UserInfoWithoutBalanceDTO();
        userUpdate.setFirstName(null);
        userUpdate.setLastName("LastName2");
        userUpdate.setEmail("user2@test.com");
        userUpdate.setPassword("password");
        userUpdate.setBankAccount(bankAccount);

        mockMvc.perform(put("/profile/{user_id}", 2)
                .content(new ObjectMapper().writeValueAsString(userUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update user account information with valid argument not as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void updateUserAccountInfoAsDifferentUserAndValidArgsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO userUpdate = new UserInfoWithoutBalanceDTO();
        userUpdate.setFirstName("FirstName1");
        userUpdate.setLastName("LastName1");
        userUpdate.setEmail("user1@test.com");
        userUpdate.setPassword("password");
        userUpdate.setBankAccount(bankAccount);

        mockMvc.perform(put("/profile/{user_id}", 1)
                .content(new ObjectMapper().writeValueAsString(userUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Try to update not existing user")
    @Test
    @WithMockUser(username = "user2@test.com")
    void updateUserAccountInfoAsNotExistsUserAndValidArgsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO userUpdate = new UserInfoWithoutBalanceDTO();
        userUpdate.setFirstName("FirstName2");
        userUpdate.setLastName("LastName2");
        userUpdate.setEmail("user2@test.com");
        userUpdate.setPassword("password");
        userUpdate.setBankAccount(bankAccount);

        mockMvc.perform(put("/profile/{user_id}", 17)
                .content(new ObjectMapper().writeValueAsString(userUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Update user account information with valid argument not as user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void updateUserAccountInfoAsNotUserAndValidArgsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO userUpdate = new UserInfoWithoutBalanceDTO();
        userUpdate.setFirstName("FirstName2");
        userUpdate.setLastName("LastName2");
        userUpdate.setEmail("user2@test.com");
        userUpdate.setPassword("password");
        userUpdate.setBankAccount(bankAccount);

        mockMvc.perform(put("/profile/{user_id}", 1)
                .content(new ObjectMapper().writeValueAsString(userUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Update user account information with valid argument as unauthenticated")
    @Test
    @WithAnonymousUser
    void updateUserAccountInfoAsAnonymousAndValidArgsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO userUpdate = new UserInfoWithoutBalanceDTO();
        userUpdate.setFirstName("FirstName2");
        userUpdate.setLastName("LastName2");
        userUpdate.setEmail("user2@test.com");
        userUpdate.setPassword("password");
        userUpdate.setBankAccount(bankAccount);

        mockMvc.perform(put("/profile/{user_id}", 1)
                .content(new ObjectMapper().writeValueAsString(userUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    // DELETE USER ACCOUNT TEST //

    @DisplayName("Delete user account as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void deleteUserAccountAsActualUserIT() throws Exception {
        mockMvc.perform(delete("/profile/{user_id}", 2))
                .andExpect(status().isOk());
    }

    @DisplayName("Delete user account not as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void deleteUserAccountAsDifferentUserIT() throws Exception {
        mockMvc.perform(delete("/profile/{user_id}", 3))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Delete account of not existing user")
    @Test
    @WithMockUser(username = "user@test.com")
    void deleteUserAccountAsNotExistsUserIT() throws Exception {
        mockMvc.perform(delete("/profile/{user_id}", 17))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Delete user account not as owner")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void deleteUserAccountAsNotUserIT() throws Exception {
        mockMvc.perform(delete("/profile/{user_id}", 1))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Delete user account unauthenticated")
    @Test
    @WithAnonymousUser
    void deleteUserAccountUnauthenticatedIT() throws Exception {
        mockMvc.perform(delete("/profile/{user_id}", 1))
                .andExpect(status().is3xxRedirection());
    }

    // GET ALL USER'S CONNECTIONS TEST //

    @DisplayName("Get all connection as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getAllUserConnectionsAsActualUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}/connections", 2))
                .andExpect(status().isOk());
    }

    @DisplayName("Get all user connection not as user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void getAllUserConnectionsAsNotUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}/connections", 1))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all user connection not as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getAllUserConnectionsAsDifferentUserIt() throws Exception {
        mockMvc.perform(get("/profile/{user_id}/connections", 3))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all user connection for not existing user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserConnectionsAsNotExistsUserIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}/connections", 17))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get all user connection unauthenticated")
    @Test
    @WithAnonymousUser
    void getAllUserConnectionsUnauthenticatedIT() throws Exception {
        mockMvc.perform(get("/profile/{user_id}/connections", 2))
                .andExpect(status().is3xxRedirection());
    }

    // ADD A NEW CONNECTION TEST //

    @DisplayName("Add new existing connection as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void addNewConnectionAsActualUserAndConnectionExistsIT() throws Exception {
        String email = "user3@test.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, 2))
                .andExpect(status().isCreated());
    }

    @DisplayName("Add not existing connection as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void addNewConnectionAsActualUserAndConnectionNotExistsIT() throws Exception {
        String email = "connection@mail.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, 2))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Add new connection not as user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void addNewConnectionNotAsUserAndConnectionExistsIT() throws Exception {
        String email = "user3@test.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, 1))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Add new connection not as owner")
    @Test
    @WithMockUser(username = "user4@test.com")
    void addNewConnectionAsDifferentUserAndConnectionExistsIT() throws Exception {
        String email = "user3@test.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, 2))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Add a connection already existing in network as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void addNewConnectionAsActualUserAndConnectionAlreadyExistsIT() throws Exception {
        String email = "user4@test.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, 2))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Add new existing connection unauthenticated")
    @Test
    @WithAnonymousUser
    void addNewConnectionUnauthenticatedAndConnectionExistsIT() throws Exception {
        String email = "user3@test.com";
        mockMvc.perform(put("/profile/{user_id}/connections?email=" + email, 2))
                .andExpect(status().is3xxRedirection());
    }

    // DELETE CONNECTION TEST //

    @DisplayName("Delete existing connection as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void deleteConnectionExistsAsActualUserIT() throws Exception {
        int user_id = 2;
        int connection_id = 4;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Delete not existing connection as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void deleteConnectionNotExistsAsActualUserIT() throws Exception {
        int user_id = 2;
        int connection_id = 3;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Delete existing connection not as user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void deleteConnectionExistsAsNotUserIT() throws Exception {
        int user_id = 1;
        int connection_id = 5;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Delete existing connection not as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void deleteOldConnectionExistsAsDifferentUserIT() throws Exception {
        int user_id = 3;
        int connection_id = 2;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Delete existing connection unhauthenticated")
    @Test
    @WithAnonymousUser
    void deleteConnectionExistsUnautenticatedIT() throws Exception {
        int user_id = 2;
        int connection_id = 4;
        mockMvc.perform(put("/profile/{user_id}/connections/{connection_id}", user_id, connection_id))
                .andExpect(status().is3xxRedirection());
    }

    //GET ALL USER'S TRANSFERS TEST //

    @DisplayName("Get all transfers as owner (sender and receiver)")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getAllUserTransfersAsActualUserIT() throws Exception {
        int user_id = 2;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Get all transfers not as user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void getAllUserTransfersAsNotUserIT() throws Exception {
        int user_id = 1;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all transfers not as owner")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getAllUserTransfersAsDifferentUserIT() throws Exception {
        int user_id = 3;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get all transfers of inexisting user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserTransfersAsNotExistsUserIT() throws Exception {
        int user_id = 17;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get all transfers unauthenticated")
    @Test
    @WithAnonymousUser
    void getAllUserTransfersUnauthenticatedIT() throws Exception {
        int user_id = 2;
        mockMvc.perform(get("/profile/{user_id}/transfers", user_id))
                .andExpect(status().is3xxRedirection());
    }

}
