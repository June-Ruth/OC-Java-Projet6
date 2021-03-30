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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class SignUpControllerIT {

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

    @DisplayName("Create an account with valid arguments and not existing mail")
    @Test
    @WithAnonymousUser
    void createAccountWithValidArgsAndEmailNotExistsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO newUser = new UserInfoWithoutBalanceDTO();
        newUser.setFirstName("FirstName6");
        newUser.setLastName("LastName6");
        newUser.setEmail("user6@test.com");
        newUser.setPassword("password");
        newUser.setBankAccount(bankAccount);

        mockMvc.perform(post("/signup")
                .content(new ObjectMapper().writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Create an account with invalid arguments")
    @Test
    @WithAnonymousUser
    void createAccountWithInvalidArgsAndEmailNotExistsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO newUser = new UserInfoWithoutBalanceDTO();
        newUser.setFirstName(null);
        newUser.setLastName("LastName6");
        newUser.setEmail("user6@test.com");
        newUser.setPassword("password");
        newUser.setBankAccount(bankAccount);

        mockMvc.perform(post("/signup")
                .content(new ObjectMapper().writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Create an account with valid arguments and existing mail")
    @Test
    @WithAnonymousUser
    void createAccountWithValidArgsAndEmailExistsIT() throws Exception {
        BankAccount bankAccount = new BankAccount("RIB test", "Bank test", "IBAN test", "BIC test");

        UserInfoWithoutBalanceDTO newUser = new UserInfoWithoutBalanceDTO();
        newUser.setFirstName("FirstName2");
        newUser.setLastName("LastName2");
        newUser.setEmail("user2@test.com");
        newUser.setPassword("password");
        newUser.setBankAccount(bankAccount);

        mockMvc.perform(post("/signup")
                .content(new ObjectMapper().writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
