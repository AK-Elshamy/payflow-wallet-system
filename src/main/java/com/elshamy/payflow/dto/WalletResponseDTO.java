
package com.elshamy.payflow.dto;

import com.elshamy.payflow.entity.CurrencyCode;
import com.elshamy.payflow.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponseDTO(
        Long id,
        BigDecimal balance,
        CurrencyCode currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static WalletResponseDTO fromEntity(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}
