package com.example.ibuy.controller;

import com.example.ibuy.dto.LoginRequest;
import com.example.ibuy.dto.LoginResponse;
import com.example.ibuy.dto.RegisterRequest;
import com.example.ibuy.dto.RegisterResponse;
import com.example.ibuy.model.User;
import com.example.ibuy.repository.UserRepository;
import com.example.ibuy.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

        String token = jwtService.generateToken(user.getUsername(), user.getMail());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
