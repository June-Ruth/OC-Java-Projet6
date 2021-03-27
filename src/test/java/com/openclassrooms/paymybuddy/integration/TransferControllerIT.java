package com.openclassrooms.paymybuddy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.paymybuddy.model.dto.SendingTransferDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class TransferControllerIT {

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

        /*
    sendTransfer() Tests
     */

    @DisplayName("Send transfer to other user as user using valid arguments, valid receiver email and valid amount")
    @Test
    @WithMockUser(username = "user2@test.com")
    void sendTransferBetweenUsersAsUserAndValidArgsIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin1@test.com", "description1", 100);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Send transfer to bank as user using valid arguments and valid amount")
    @Test
    @WithMockUser(username = "user2@test.com")
    void sendTransferWithBankAsUserAndValidArgsIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("user2@test.com", "description1", 100);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Send transfer to other user as user using invalid arguments")
    @Test
    @WithMockUser(username = "user2@test.com")
    void sendTransferBetweenUsersAsUserAndInvalidArgsIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO(null, "description1", 0);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Send transfer to a not existing user")
    @Test
    @WithMockUser(username = "user2@test.com")
    void sendTransferBetweenUsersAsUserToNotExistingUserIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin@test.com", "description1", 100);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Send transfer with not enough money on balance")
    @Test
    @WithMockUser(username = "user2@test.com")
    void sendTransferBetweenUsersAsUserWithAmountSupBalanceIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin1@test.com", "description1", 9000);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Send transfer as not a user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void sendTransferNotAsUserAndValidArgsIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin@test.com", "description1", 100);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Send transfer unauthenticated")
    @Test
    @WithAnonymousUser
    void sendTransferBetweenUsersUnauthenticatedAndValidArgsIT() throws Exception {
        SendingTransferDTO transferDTO = new SendingTransferDTO("admin1@test.com", "description1", 100);
        mockMvc.perform(post("/transfers")
                .content(new ObjectMapper().writeValueAsString(transferDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    /*
    getMyTransferAsSender() Tests
     */

    @DisplayName("Get my transfers as a user")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getMyTransfersAsSenderAsUserIT() throws Exception {
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isOk());
    }

    @DisplayName("Get my transfers as not a user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void getMyTransfersAsSenderAsNotUserIT() throws Exception {
        mockMvc.perform(get("/transfers"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get my transfers unhauticated")
    @Test
    @WithAnonymousUser
    void getMyTransfersAsSenderUnauthenticatedIT() throws Exception {
        mockMvc.perform(get("/transfers"))
                .andExpect(status().is3xxRedirection());
    }

    /*
    getTransfer() Test
     */

    @DisplayName("Get a transfer which exists as sender or receiver user")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getTransferAsSenderOrReceiverAndTransferExistsIT() throws Exception {
        int transfer_id = 1;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isOk());
    }

    @DisplayName("Get a transfer which exists as user but not as sender or receiver")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getTransferAsUserNotSenderOrReceiverAndTransferExistsIT() throws Exception {
        int transfer_id = 2;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get a transfer which doesn't exist")
    @Test
    @WithMockUser(username = "user2@test.com")
    void getTransferAsUserAndTransferNotExistsIT() throws Exception {
        int transfer_id = 17;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Get a transfer as not a user")
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void getTransferAsNotUserAndTransferExistsIT() throws Exception {
        int transfer_id = 1;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Get a transfer unauthenticated")
    @Test
    @WithAnonymousUser
    void getTransferUnauthenticatedAndTransferExistsIT() throws Exception {
        int transfer_id = 1;
        mockMvc.perform(get("/transfers/{transfer_id}", transfer_id))
                .andExpect(status().is3xxRedirection());
    }
}
