package com.example.ibuy.controller;

import com.example.ibuy.dto.LoginRequest;
import com.example.ibuy.dto.LoginResponse;
import com.example.ibuy.dto.RegisterRequest;
import com.example.ibuy.dto.RegisterResponse;
import com.example.ibuy.model.User;
import com.example.ibuy.repository.UserRepository;
import com.example.ibuy.security.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.Optional;

@RestController
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private DataSource dataSource;
    private String sessid;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByMail(request.email().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }

        if (userRepository.existsByUsername(request.username().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already used");
        }

        User newUser = new User();

        newUser.setMail(request.email().trim().toLowerCase());
        newUser.setUsername(request.username().trim());
        newUser.setPassword(passwordEncoder.encode(request.pass()));

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(newUser.getMail(), newUser.getUsername()));
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.username());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = user.getUsername();
        String email = user.getMail();

        String token = jwtService.generateToken(username, email);
        String refreshToken = jwtService.generateRefreshToken(username, email);

        // calculate hex of refreshToken
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            sessid = HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 non disponibile sulla JVM", e);
        }

        user.setSessid(sessid);
        userRepository.save(user);

        return ResponseEntity.ok(new LoginResponse(token, refreshToken));
    }

}
