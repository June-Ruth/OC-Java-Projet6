package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.TransferType;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.HistoricTransferAsSenderDTO;
import com.openclassrooms.paymybuddy.model.dto.SendingTransferDTO;
import com.openclassrooms.paymybuddy.model.dto.TransferInformationFullDto;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("transfers")
public class TransferController {
    //TODO : Logger + JavaDoc
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger(TransferController.class);

    private TransferService transferService;
    private UserAccountService userAccountService;

    public TransferController(final TransferService pTransferService,
                              final UserAccountService pUserAccountService){
        transferService = pTransferService;
        userAccountService = pUserAccountService;
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<Transfer> sendTransfer(@Valid @RequestBody final SendingTransferDTO transferDTO) {

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount sender = userAccountService.findUserAccountByEmail(principal.getUsername());

        String description = transferDTO.getDescription();

        UserAccount receiver = userAccountService.findUserAccountByEmail(transferDTO.getReceiverEmail());
        //TODO : if (receiver == null) throw new EmailNotExistsException;

        double amount = transferDTO.getAmount();
        //TODO : if (amount > sender.getBalance()) throw NotEnoughMoneyException;

        TransferType transferType;
        double fee;
        if (sender.getEmail().equals(receiver.getEmail())) {
            transferType = TransferType.TRANSFER_WITH_BANK;
            fee = 0;
        } else {
            transferType = TransferType.TRANSFER_BETWEEN_USER;
            fee = amount*0.05;
        }

        Transfer transfer = new Transfer(sender, receiver, description, LocalDate.now(), amount, fee, transferType);

        transferService.saveTransfer(transfer);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{transfer_id}")
                .buildAndExpand(transfer.getId())
                .toUri();

        return ResponseEntity.created(location).body(transfer);
    }

    @GetMapping
    public List<HistoricTransferAsSenderDTO> getMyTransfersAsSender() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getUsername();
        UserAccount userAccount = userAccountService.findUserAccountByEmail(email);

        List<Transfer> transfers = transferService.findTransferBySender(userAccount);
        List<HistoricTransferAsSenderDTO> historicTransfersAsSender = new ArrayList<>();
        for (Transfer transfer : transfers) {
            String name = transfer.getReceiver().getFirstName().concat(" ").concat(transfer.getReceiver().getLastName());
            HistoricTransferAsSenderDTO historicTransfer = new HistoricTransferAsSenderDTO(name, transfer.getDescription(), transfer.getAmount());
            historicTransfersAsSender.add(historicTransfer);
        }
        return historicTransfersAsSender;
    }

    @GetMapping(value = "/{transfer_id}")
    public ResponseEntity<String> getTransfer(@PathVariable final int transfer_id) {
        Transfer transfer = transferService.findTransferById(transfer_id);
        if (transfer != null) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.getUsername();
            UserAccount userAccount = userAccountService.findUserAccountByEmail(email);

            if (transfer.getReceiver() == userAccount || transfer.getSender() == userAccount) {
                String senderName = transfer.getSender().getFirstName().concat(" ").concat(transfer.getSender().getLastName());
                String receiverName = transfer.getReceiver().getFirstName().concat(" ").concat(transfer.getReceiver().getLastName());
                TransferInformationFullDto transferInformationFullDto = new TransferInformationFullDto(senderName, receiverName, transfer.getDescription(), transfer.getDate(), transfer.getAmount(), transfer.getFee(), transfer.getTransferType());
                return ResponseEntity.ok().body(transferInformationFullDto.toString());

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

}
