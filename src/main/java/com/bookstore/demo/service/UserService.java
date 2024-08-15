package com.bookstore.demo.service;

import com.bookstore.demo.model.User;
import com.bookstore.demo.repository.UserRepository;
import com.bookstore.demo.kafka.KafkaProducer;
import com.bookstore.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        logger.debug("Fetching user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.debug("User found: {}", user.get().getUsername());
        } else {
            logger.warn("User not found for username: {}", username);
        }
        return user;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encode the password before saving
        User createdUser = userRepository.save(user);
        kafkaProducer.sendMessage("User created: " + createdUser.getUsername());
        return createdUser;
    }

    public String authenticateUser(String username, String password) {
        try {
            logger.info("Attempting to authenticate user: {}", username);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            logger.info("Authentication successful for user: {}", username);

            String token = jwtTokenProvider.generateToken(authentication);
            logger.info("Generated JWT Token: {}", token);

            kafkaProducer.sendMessage("User logged in: " + username);
            logger.info("Sent login message to Kafka for user: {}", username);

            return token;

        } catch (AuthenticationException ex) {
            logger.error("Failed login attempt for user: {}. Reason: {}", username, ex.getMessage());
            kafkaProducer.sendMessage("Failed login attempt for user: " + username);
            throw ex;
        }
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set the role to "User" if it's null
        if (user.getRole() == null) {
            existingUser.setRole("User");
        } else {
            existingUser.setRole(user.getRole());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        kafkaProducer.sendMessage("User deleted with id: " + id);
    }
}
