package com.bookstore.demo.controller;

import com.bookstore.demo.config.SecurityConfig;
import com.bookstore.demo.model.JwtResponse;
import com.bookstore.demo.model.UserLoginRequest;
import com.bookstore.demo.model.User;
import com.bookstore.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        logger.debug("Fetching current user for authentication: {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("No valid authentication found, returning 403 Forbidden");
            return ResponseEntity.status(403).body(null);
        }

        String username = authentication.getName();
        logger.debug("Authenticated user: {}", username);

        Optional<User> user = userService.getUserByUsername(username);
        logger.debug("User found: {}", user.isPresent());

        return user.map(ResponseEntity::ok).orElseGet(() -> {
            logger.warn("User not found, returning 403 Forbidden");
            return ResponseEntity.status(403).body(null);
        });
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest loginRequest) {
        try {
            String token = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping
    public User registerUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
