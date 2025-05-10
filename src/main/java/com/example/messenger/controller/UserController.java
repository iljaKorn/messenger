package com.example.messenger.controller;

import com.example.messenger.entity.User;
import com.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с пользователем
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Метод для получения всех пользователей
     */
    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userService.allUsers();
    }

    /**
     * Метод для получения пользователя по id
     * @param id идентификатор пользователя
     */
    @GetMapping("/users/get_user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
