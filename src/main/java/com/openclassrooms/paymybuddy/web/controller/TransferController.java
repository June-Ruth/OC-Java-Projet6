package com.openclassrooms.paymybuddy.web.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
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

    //TODO : create a transfer
    @PostMapping(value = "/transfers", consumes = {"application/json"})
    public ResponseEntity<String> createTransfer(@Valid @RequestBody final Transfer transfer) {

        transferService.saveTransfer(transfer);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{transfer_id}")
                .buildAndExpand(transfer.getId())
                .toUri();

        return ResponseEntity.created(location).body(transfer.toString());
    }

    //TODO : see les derniers transferts que j'ai effectué en tant que user en version simplifié (voir maquette)
    @GetMapping(value = "/transfers")
    public List<Transfer> getMyTransfersAsSender(Principal principal) {
        String email = principal.getName();
        UserAccount userAccount = userAccountService.findUserAccountByEmail(email);
        List<Transfer> transfers = transferService.findTransferBySender(userAccount);
        return transfers;
    }

    //TODO : see a specific transfer as sender or receiver (users concerned only)
    @GetMapping(value = "/transfers/{transfer_id}")
    public ResponseEntity<String> getTransfer(@PathVariable final int transfer_id) {
        Transfer transfer = transferService.findTransferById(transfer_id);
        if (transfer != null) {
            return ResponseEntity.ok().body(transfer.toString());
        }
        return ResponseEntity.notFound().build();
    }

}
