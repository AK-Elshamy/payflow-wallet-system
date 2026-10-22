
package com.elshamy.payflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransferRequestDTO(

        @NotNull(message = "Destination wallet ID is required")
        @DecimalMin(value = "1", message = "Destination wallet ID must be positive")
        Long destinationWalletId,

        @NotNull(message = "Transfer amount is required")
        @DecimalMin(value = "0.01", message = "Transfer amount must be at least 0.01")
        @Digits(
                integer = 8,
                fraction = 2,
                message = "Amount must have at most 8 integer digits and 2 decimal places"
        )
        BigDecimal amount

) {
}
