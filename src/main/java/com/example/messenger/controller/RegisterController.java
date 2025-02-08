package com.example.messenger.controller;

import com.example.messenger.dto.UserDTO;
import com.example.messenger.entity.ConfirmationToken;
import com.example.messenger.entity.User;
import com.example.messenger.repository.ConfirmationTokenRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.EmailService;
import com.example.messenger.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@AllArgsConstructor
public class RegisterController {
    @Autowired
    private UserService userService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userService.allUsers();
    }

    @PostMapping("/register")
    public boolean register(@RequestBody UserDTO user) {
        if (!userService.saveUser(user)) {
            return false;
        }
        User userFromDB = userService.loadUserByUsername(user.getUsername());

        ConfirmationToken confirmationToken = new ConfirmationToken(userFromDB);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        System.out.println(userFromDB.toString());
        mailMessage.setTo(userFromDB.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom(System.getenv("email"));
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());

        emailService.sendEmail(mailMessage);
        return true;
    }

    @GetMapping("/confirm-account")
    public boolean confirmEmail(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userService.findByEmail(token.getUser().getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
}
