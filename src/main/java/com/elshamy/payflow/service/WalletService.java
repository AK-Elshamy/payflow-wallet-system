
package com.elshamy.payflow.service;

import com.elshamy.payflow.dto.WalletResponseDTO;
import com.elshamy.payflow.entity.User;
import com.elshamy.payflow.entity.Wallet;
import com.elshamy.payflow.repository.WalletRepository;
import com.elshamy.payflow.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CurrentUserService currentUserService;

    public WalletService(
            WalletRepository walletRepository,
            CurrentUserService currentUserService
    ) {
        this.walletRepository = walletRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public WalletResponseDTO getMyWallet() {

        User currentUser = currentUserService.getCurrentUser();

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Wallet not found for the authenticated user"
                        )
                );

        return WalletResponseDTO.fromEntity(wallet);
    }
}
