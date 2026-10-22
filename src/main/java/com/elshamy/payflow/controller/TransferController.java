
package com.elshamy.payflow.controller;

import com.elshamy.payflow.dto.CreateTransferRequestDTO;
import com.elshamy.payflow.dto.TransferResponseDTO;
import com.elshamy.payflow.service.TransferService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponseDTO> createTransfer(
            @Valid @RequestBody CreateTransferRequestDTO request
    ) {
        TransferResponseDTO response =
                transferService.createTransfer(request);

        return ResponseEntity
                .status(
                        response.status().name().equals("FAILED")
                                ? HttpStatus.UNPROCESSABLE_ENTITY
                                : HttpStatus.CREATED
                )
                .body(response);
    }
}
