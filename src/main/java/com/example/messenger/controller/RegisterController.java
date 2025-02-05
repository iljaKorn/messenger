package com.example.messenger.controller;

import com.example.messenger.dto.UserDTO;
import com.example.messenger.entity.User;
import com.example.messenger.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@AllArgsConstructor
public class RegisterController {
    @Autowired
    private UserService userService;

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userService.allUsers();
    }

    @PostMapping("/register")
    public boolean register(@RequestBody UserDTO user) {
        return userService.saveUser(user);
    }
}
