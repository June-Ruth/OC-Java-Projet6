package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.constant.BusinessConstant;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.TransferType;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.model.dto.HistoricTransferAsSenderDTO;
import com.openclassrooms.paymybuddy.model.dto.SendingTransferDTO;
import com.openclassrooms.paymybuddy.model.dto.TransferInformationFullDto;
import com.openclassrooms.paymybuddy.service.TransferService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import com.openclassrooms.paymybuddy.util.DtoConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("transfers")
public class TransferController {
    /**
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger(TransferController.class);
    /**
     * @see TransferService
     */
    private TransferService transferService;
    /**
     * @see UserAccountService
     */
    private UserAccountService userAccountService;

    /**
     * Autowired constructor.
     * @param pTransferService .
     * @param pUserAccountService .
     */
    public TransferController(final TransferService pTransferService,
                              final UserAccountService pUserAccountService) {
        transferService = pTransferService;
        userAccountService = pUserAccountService;
    }

    /**
     * Allows user to send a transfer to another user or to its bank account.
     * @param transferDTO .
     * @return 201 Created with Transfer information
     */
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<Transfer> sendTransfer(
            @Valid @RequestBody final SendingTransferDTO transferDTO) {
        LOGGER.info("Try to send a transfer with transfer information :\t"
                + transferDTO.toString());
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        UserAccount sender = userAccountService
                .findUserAccountByEmail(principal.getUsername());

        String description = transferDTO.getDescription();

        UserAccount receiver = userAccountService
                .findUserAccountByEmail(transferDTO.getReceiverEmail());

        double amount = transferDTO.getAmount();

        TransferType transferType;
        double fee;
        if (sender.getEmail().equals(receiver.getEmail())) {
            transferType = TransferType.TRANSFER_WITH_BANK;
            fee = 0;
        } else {
            transferType = TransferType.TRANSFER_BETWEEN_USER;
            fee = amount * BusinessConstant.FEE_RATE;
        }

        Transfer transfer = new Transfer(sender,
                receiver, description, LocalDate.now(),
                amount, fee, transferType);

        transferService.saveTransfer(transfer);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{transfer_id}")
                .buildAndExpand(transfer.getId())
                .toUri();
        LOGGER.info("Success to send transfer : \t" + transfer.toString());
        return ResponseEntity.created(location).body(transfer);
    }

    /**
     * Allows user to get all its transfer as sender.
     * @return list of transfer
     * @see HistoricTransferAsSenderDTO
     */
    @GetMapping
    public List<HistoricTransferAsSenderDTO> getMyTransfersAsSender() {
        LOGGER.info("Try to get user transfer done as sender");
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String email = principal.getUsername();
        UserAccount userAccount =
                userAccountService.findUserAccountByEmail(email);

        List<Transfer> transfers =
                transferService.findTransferBySender(userAccount);
        List<HistoricTransferAsSenderDTO> historicTransfersAsSender =
                new ArrayList<>();
        for (Transfer transfer : transfers) {
            HistoricTransferAsSenderDTO historicTransfer =
                    DtoConverter.convertTransferToHistoricTransferAsSenderDto(
                            transfer);
            historicTransfersAsSender.add(historicTransfer);
        }
        LOGGER.info("Success to get user transfer done as sender");
        return historicTransfersAsSender;
    }

    /**
     * Allows user to see all of information
     * about one of its transfer as sender or receiver.
     * @param transferId .
     * @return transfer information
     * @see TransferInformationFullDto
     */
    @GetMapping(value = "/{transferId}")
    public ResponseEntity<String> getTransfer(
            @PathVariable final int transferId) {
        LOGGER.info("Try to access all information about transfer id : "
                + transferId);
        Transfer transfer = transferService.findTransferById(transferId);
        User principal = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String email = principal.getUsername();
        UserAccount userAccount =
                userAccountService.findUserAccountByEmail(email);

        if (transfer.getReceiver() == userAccount
                || transfer.getSender() == userAccount) {
            TransferInformationFullDto transferInformationFullDto =
                    DtoConverter
                            .convertTransferToTransferInformationFullDto(
                                    transfer);
            LOGGER.info("Success to get information about transfer id : "
                    + transferId + "\t"
                    + transferInformationFullDto.toString());
            return ResponseEntity.ok()
                    .body(transferInformationFullDto.toString());
        } else {
            LOGGER.error("Forbidden access to transfer : " + transferId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
