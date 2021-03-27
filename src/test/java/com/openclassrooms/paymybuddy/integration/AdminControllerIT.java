package com.openclassrooms.paymybuddy.integration;

import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private TransferService transferService;

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("create_default_value.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Get all user account information as admin")
    //@Sql(scripts = "classpath:create_default_value.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @WithMockUser(username = "admin1@test.com", roles = {"ADMIN"})
    void getAllUserAccountsAsAdminIT() throws Exception {
        mockMvc.perform(get("/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("admin1@test.com")));
    }

    @DisplayName("Get all user account information as user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllUserAccountsAsUserIT() throws Exception {

    }

    @DisplayName("Get all user account information unauthenticated")
    @Test
    @WithAnonymousUser
    void getAllUserAccountsAsUnAuthenticatedIT() throws Exception {

    }

    @DisplayName("Get all transfers information as admin")
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getAllTransfersAsAdminIT() throws Exception {

    }

    @DisplayName("Get all transfers information as user")
    @Test
    @WithMockUser(username = "user@test.com")
    void getAllTransfersAsUserIT() {

    }

    @DisplayName("Get all transfers information unauthenticated")
    @Test
    @WithAnonymousUser
    void getAllTransfersUnAuthenticatedIT() {

    }

}
