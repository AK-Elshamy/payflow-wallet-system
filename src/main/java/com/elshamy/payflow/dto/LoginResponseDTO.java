package com.elshamy.payflow.dto;

import com.elshamy.payflow.entity.User;

public record LoginResponseDTO(
        Long id,
        String username,
        String email,
        String message
) {
    public static LoginResponseDTO fromEntity(User user, String message) {
        return new LoginResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                message
        );
    }
}