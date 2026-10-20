package com.elshamy.payflow.dto;

import com.elshamy.payflow.entity.User;
import java.time.LocalDateTime;

public record RegisterResponseDTO(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
    public static RegisterResponseDTO fromEntity(User user) {
        return new RegisterResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}