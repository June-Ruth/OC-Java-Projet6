package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.repository.TransferDAO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TransferServiceTest {

    @Mock
    private static TransferDAO transferDAO;

    @Mock
    private static UserAccountService userAccountService;

    private static TransferService transferService;

    private Transfer transfer1;
    private Transfer transfer2;
    private UserAccount userAccount1;
    private UserAccount userAccount2;

    private static List<Transfer> transfers = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        transferService = new TransferServiceImpl(transferDAO, userAccountService);
        List<Role> userRole = new ArrayList<>();
        BankAccount bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1 = new UserAccount("firstName1", "lastName1", "user1@mail.com",  "password1", userRole, bankAccount1, 1000, null, null);
        userAccount2 = new UserAccount("firstName2", "lastName2", "user2@mail.com",  "password2", userRole, bankAccount2, 1000, null, null);
        transfer1 = new Transfer(userAccount1, userAccount2, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transfer2 = new Transfer(userAccount1, userAccount1, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transfer1);
        transfers.add(transfer2);
    }

    @DisplayName("Save a Transfer Type = TRANSFER_BETWEEN_USER")
    @Test
    void saveTransferBetweenUserTest() {
        when(transferDAO.save(any(Transfer.class))).thenReturn(transfer1);
        when(userAccountService.updateUserAccount(any(UserAccount.class))).thenReturn(userAccount1).thenReturn(userAccount2);
        transferService.saveTransfer(transfer1);
        verify(transferDAO, times(1)).save(any(Transfer.class));
        verify(userAccountService, times(2)).updateUserAccount(any(UserAccount.class));
        assertEquals(900d, userAccount1.getBalance());
        assertEquals(1100d, userAccount2.getBalance());
    }

    @DisplayName("Save a Transfer Type = TRANSFER_WITH_BANK")
    @Test
    void saveTransferWithBankTest() {
        when(transferDAO.save(any(Transfer.class))).thenReturn(transfer2);
        when(userAccountService.updateUserAccount(any(UserAccount.class))).thenReturn(userAccount1);
        transferService.saveTransfer(transfer2);
        verify(transferDAO, times(1)).save(any(Transfer.class));
        verify(userAccountService, times(1)).updateUserAccount(any(UserAccount.class));
        assertEquals(900d, userAccount1.getBalance());
    }

    @DisplayName("Find a transfer by its id")
    @Test
    void findTransferByIdTest() {
        when(transferDAO.findById(anyInt())).thenReturn(transfer1);
        transferService.findTransferById(0);
        verify(transferDAO, times(1)).findById(0);
    }

    @DisplayName("Find all transfer by their sender")
    @Test
    void findTransferBySenderTest() {
        when(transferDAO.findAllBySender(any(UserAccount.class))).thenReturn(transfers);
        transferService.findTransferBySender(userAccount1);
        verify(transferDAO, times(1)).findAllBySender(userAccount1);
    }

    @DisplayName("Find all transfer in database")
    @Test
    void findAllTransfersTest() {
        when(transferDAO.findAll()).thenReturn(transfers);
        transferService.findAllTransfers();
        verify(transferDAO, times(1)).findAll();
    }
}
