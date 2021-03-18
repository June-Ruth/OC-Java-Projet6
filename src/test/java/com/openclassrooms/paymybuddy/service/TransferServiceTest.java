package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.repository.TransferDAO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(SpringExtension.class)
class TransferServiceTest {

    @Mock
    private static TransferDAO transferDAO;

    private static TransferService transferService;

    private static RoleDAO roleDAO;

    private static Transfer transfer1;
    private static Transfer transfer2;
    private static UserAccount userAccount1;
    private static UserAccount userAccount2;

    private static List<Transfer> transfers = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        List<Role> userRole = new ArrayList<>();
        userRole.add(roleDAO.findByName("ROLE_USER"));
        BankAccount bankAccount1 = new BankAccount("123", "bank1", "iban1", "bic1");
        BankAccount bankAccount2 = new BankAccount("456", "bank2", "iban2", "bic2");
        userAccount1 = new UserAccount("firstName1", "lastName1", "user1@mail.com",  "password1", userRole, bankAccount1, 0, null, null);
        userAccount2 = new UserAccount("firstName2", "lastName2", "user2@mail.com",  "password2", userRole, bankAccount2, 0, null, null);
        transfer1 = new Transfer(userAccount1, userAccount2, "description1", LocalDate.of(2020, 1, 1), 100, 1, TransferType.TRANSFER_BETWEEN_USER);
        transfer2 = new Transfer(userAccount1, userAccount1, "description2", LocalDate.of(2020, 2, 2), 100, 0, TransferType.TRANSFER_WITH_BANK);
        transfers.add(transfer1);
        transfers.add(transfer2);
    }

    @BeforeEach
    void beforeEach() {
        transferService = new TransferServiceImpl(transferDAO);
    }

    @Test
    void saveTransferTest() {
        when(transferDAO.save(any(Transfer.class))).thenReturn(transfer1);
        transferService.saveTransfer(transfer1);
        verify(transferDAO, times(1)).save(transfer1);
    }

    @Test
    void findTransferByIdTest() {
        when(transferDAO.findById(anyInt())).thenReturn(transfer1);
        transferService.findTransferById(0);
        verify(transferDAO, times(1)).findById(0);
    }

    @Test
    void findTransferBySenderTest() {
        when(transferDAO.findAllBySender(any(UserAccount.class))).thenReturn(transfers);
        transferService.findTransferBySender(userAccount1);
        verify(transferDAO, times(1)).findAllBySender(userAccount1);
    }

    @Test
    void findAllTransfersTest() {
        when(transferDAO.findAll()).thenReturn(transfers);
        transferService.findAllTransfers();
        verify(transferDAO, times(1)).findAll();
    }
}
