
package com.elshamy.payflow.service;

import com.elshamy.payflow.dto.LoginRequestDTO;
import com.elshamy.payflow.dto.LoginResponseDTO;
import com.elshamy.payflow.dto.RegisterRequestDTO;
import com.elshamy.payflow.dto.RegisterResponseDTO;
import com.elshamy.payflow.entity.User;
import com.elshamy.payflow.exception.EmailAlreadyExistsException;
import com.elshamy.payflow.exception.UsernameAlreadyExistsException;
import com.elshamy.payflow.repository.UserRepository;
import com.elshamy.payflow.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(
                    "Username is already taken"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.username(),
                request.email(),
                passwordHash
        );

        User savedUser = userRepository.save(user);

        return RegisterResponseDTO.fromEntity(savedUser);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.usernameOrEmail(),
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsernameOrEmail(
                        request.usernameOrEmail(),
                        request.usernameOrEmail()
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"
                        )
                );

        return LoginResponseDTO.fromEntity(
                user,
                token,
                "Login successful"
        );
    }
}
