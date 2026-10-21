
package com.elshamy.payflow.controller;

import com.elshamy.payflow.dto.WalletResponseDTO;
import com.elshamy.payflow.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponseDTO> getMyWallet() {

        WalletResponseDTO response = walletService.getMyWallet();

        return ResponseEntity.ok(response);
    }
}
