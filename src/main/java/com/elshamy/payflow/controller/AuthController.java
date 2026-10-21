
package com.elshamy.payflow.controller;

import com.elshamy.payflow.dto.LoginRequestDTO;
import com.elshamy.payflow.dto.LoginResponseDTO;
import com.elshamy.payflow.dto.RegisterRequestDTO;
import com.elshamy.payflow.dto.RegisterResponseDTO;
import com.elshamy.payflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        RegisterResponseDTO response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(response);
    }

}
