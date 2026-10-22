
package com.elshamy.payflow.dto;

import com.elshamy.payflow.entity.Transfer;
import com.elshamy.payflow.entity.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponseDTO(
        Long id,
        Long sourceWalletId,
        Long destinationWalletId,
        BigDecimal amount,
        TransferStatus status,
        String failureReason,
        LocalDateTime createdAt
) {

    public static TransferResponseDTO fromEntity(Transfer transfer) {
        return new TransferResponseDTO(
                transfer.getId(),
                transfer.getSourceWallet().getId(),
                transfer.getDestinationWallet().getId(),
                transfer.getAmount(),
                transfer.getStatus(),
                transfer.getFailureReason(),
                transfer.getCreatedAt()
        );
    }
}
