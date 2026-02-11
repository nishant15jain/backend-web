package com.example.pharmabackend.auth;

import com.example.pharmabackend.exceptions.UnauthorizedException;
import com.example.pharmabackend.user.User;
import com.example.pharmabackend.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        
        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        
        // Only allow admin users to login
        if (user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("Access denied. Admin privileges required.");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name(), user.getName()));
    }
}
