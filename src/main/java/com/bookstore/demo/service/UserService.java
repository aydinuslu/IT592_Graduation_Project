package com.bookstore.demo.service;

import com.bookstore.demo.model.User;
import com.bookstore.demo.repository.UserRepository;
import com.bookstore.demo.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        User createdUser = userRepository.save(user);
        kafkaProducer.sendMessage("User created: " + createdUser.getUsername());
        return createdUser;
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        User updatedUser = userRepository.save(user);
        kafkaProducer.sendMessage("User updated: " + updatedUser.getUsername());
        return updatedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        kafkaProducer.sendMessage("User deleted with id: " + id);
    }
}
