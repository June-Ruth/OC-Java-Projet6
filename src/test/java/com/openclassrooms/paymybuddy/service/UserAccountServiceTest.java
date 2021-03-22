package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.repository.UserAccountDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserAccountServiceTest {

    @Mock
    private static UserAccountDAO userAccountDAO;

    private static UserAccountService userAccountService;

    private static Transfer transfer1;
    private static Transfer transfer2;
    private static UserAccount userAccount1;
    private static UserAccount userAccount2;
    private static BankAccount bankAccount1;

    private static List<Transfer> transfers = new ArrayList<>();
    private static List<UserAccount> userAccounts = new ArrayList<>();
    private static List<UserAccount> network = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        userAccountService = new UserAccountServiceImpl(userAccountDAO);
        List<Role> userRole = new ArrayList<>();
        bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1 = new UserAccount("firstName1", "lastName1", "user1@mail.com", "password1", userRole, bankAccount1, 0, network, transfers);
        userAccount2 = new UserAccount("firstName2", "lastName2", "user2@mail.com", "password2", userRole, bankAccount2, 0, null, null);
        transfer1 = new Transfer(userAccount1, userAccount2, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transfer2 = new Transfer(userAccount1, userAccount1, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transfer1);
        transfers.add(transfer2);
        userAccounts.add(userAccount1);
        userAccounts.add(userAccount2);
    }

    @Test
    void findUserAccountByIdTest() {
        when(userAccountDAO.findById(anyInt())).thenReturn(userAccount1);
        userAccountService.findUserAccountById(0);
        verify(userAccountDAO, times(1)).findById(0);
    }

    @Test
    void findIfUserAccountExistsByEmailTest() {
        when(userAccountDAO.existsByEmail(anyString())).thenReturn(true);
        userAccountService.findIfUserAccountExistsByEmail("test@test.com");
        verify(userAccountDAO, times(1)).existsByEmail("test@test.com");
    }

    @Test
    void findAllUserAccountsTest() {
        when(userAccountDAO.findAll()).thenReturn(userAccounts);
        userAccountService.findAllUserAccounts();
        verify(userAccountDAO, times(1)).findAll();
    }

    @Test
    void saveUserAccountTest() {
        when(userAccountDAO.save(any(UserAccount.class))).thenReturn(userAccount1);
        userAccountService.saveUserAccount(userAccount1);
        verify(userAccountDAO, times(1)).save(userAccount1);
    }

    @Test
    void updateUserAccountTest() {
        when(userAccountDAO.save(any(UserAccount.class))).thenReturn(userAccount1);
        userAccountService.saveUserAccount(userAccount1);
        verify(userAccountDAO, times(1)).save(userAccount1);
    }

    @Test
    void deleteUserAccountByIdTest() {
        when(userAccountDAO.deleteById(anyInt())).thenReturn(true);
        userAccountService.deleteUserAccountById(0);
        verify(userAccountDAO, times(1)).deleteById(0);
    }

    @Test
    void findUserNetworkTest() {
        when(userAccountDAO.findById(anyInt())).thenReturn(userAccount1);
        assertEquals(userAccount1.getConnection().size(), userAccountService.findUserNetwork(0).size());
        verify(userAccountDAO, times(1)).findById(0);
    }

    @Test
    void saveNewConnectionInUserNetworkTest() {
        when(userAccountDAO.findById(anyInt())).thenReturn(userAccount1);
        when(userAccountDAO.findByEmail(anyString())).thenReturn(userAccount2);
        when(userAccountDAO.save(any(UserAccount.class))).thenReturn(userAccount1);
        assertNotNull(userAccountService.saveNewConnectionInUserNetwork(0, "test@test.com"));
        verify(userAccountDAO, times(1)).save(userAccount1);
    }

    @Test
    void saveDeleteConnectionInUserNetworkTest() {
        List<UserAccount> connections = new ArrayList<>();
        List<Role> userRole = new ArrayList<>();
        UserAccount userAccount3 = new UserAccount("firstName1", "lastName1", "user1@mail.com", "password1", userRole, bankAccount1, 0, connections, null);
        connections.add(userAccount2);
        when(userAccountDAO.findById(anyInt())).thenReturn(userAccount3).thenReturn(userAccount2);
        when(userAccountDAO.save(any(UserAccount.class))).thenReturn(userAccount3);
        assertNotNull(userAccountService.saveDeleteConnectionInUserNetwork(0, 1));
        verify(userAccountDAO, times(1)).save(userAccount3);
    }

    @Test
    void findUserTransfersTest() {
        when(userAccountDAO.findById(anyInt())).thenReturn(userAccount1);
        assertEquals(userAccount1.getTransferLog().size(), userAccountService.findUserTransfers(0).size());
    }

    @Test
    void findUserAccountByEmail() {
        when(userAccountDAO.findByEmail(anyString())).thenReturn(userAccount1);
        userAccountService.findUserAccountByEmail(userAccount1.getEmail());
        verify(userAccountDAO, times(1)).findByEmail(anyString());
    }

}
