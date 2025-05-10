package com.example.messenger.controller;

import com.example.messenger.dto.SignUpRequest;
import com.example.messenger.entity.ConfirmationToken;
import com.example.messenger.entity.User;
import com.example.messenger.repository.ConfirmationTokenRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.EmailService;
import com.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Контроллер для обработки запросов регистрации
 */
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final EmailService emailService;

    private final UserRepository userRepository;

    /**
     * Метод, который регистрирует пользователя
     * @param user сущность с данными для регистрации
     */
    @PostMapping("/register")
    public boolean register(@RequestBody SignUpRequest user) {
        if (!userService.saveUser(user)) {
            return false;
        }
        User userFromDB = userService.findByUsername(user.getUsername());

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

    /**
     * Метод для подтверждения почты пользователя
     * @param confirmationToken токен, подтверждающий почту
     */
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
