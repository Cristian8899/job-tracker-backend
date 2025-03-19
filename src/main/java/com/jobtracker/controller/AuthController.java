package com.jobtracker.controller;

import com.jobtracker.model.AuthResponse;
import com.jobtracker.model.User;
import com.jobtracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {

            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                return ResponseEntity.status(400).body("Missing or invalid Authorization header ❌");
            }

            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] values = credentials.split(":", 2);
            if (values.length != 2) {
                return ResponseEntity.status(400).body("Invalid Authorization header format ❌");
            }

            String username = values[0];
            String password = values[1];

            User user = authService.authenticate(username, password);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful ✅");
            response.put("data", new AuthResponse(user.getToken(), user.getId().toString(), user.getUsername(), user.getFullName(), user.getEmail(), user.getExpiry()));

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password ❌");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong! ❌");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = authService.register(user.getUsername(), user.getPassword(), user.getFullName(), user.getEmail());
            return ResponseEntity.status(201).body(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body("User already exists! ❌");
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully! 🔓");
    }
}
