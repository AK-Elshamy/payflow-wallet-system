
package com.elshamy.payflow.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String message,
        LocalDateTime timestamp,
        String path
) {
    public static ErrorResponseDTO of(
            int status,
            String message,
            String path
    ) {
        return new ErrorResponseDTO(
                status,
                message,
                LocalDateTime.now(),
                path
        );
    }
}
