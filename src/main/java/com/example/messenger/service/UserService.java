package com.example.messenger.service;

import com.example.messenger.dto.SignUpRequest;
import com.example.messenger.entity.Role;
import com.example.messenger.entity.User;
import com.example.messenger.repository.RoleRepository;
import com.example.messenger.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Метод для получения пользователя по id
     * @param id идентификатор пользователя
     */
    public User findById(Long id) {
        Optional<User> userFromDB = userRepository.findById(id);
        return userFromDB.orElse(new User());
    }

    /**
     * Метод для получения пользователя по почте
     * @param email почта пользователя
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Метод для получения пользователя по имени
     * @param username имя пользователя
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Метод для получения всех пользователей
     */
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    /**
     * Метод для сохранений пользователя в базе данных
     * @param dto сущность с данными пользователя
     */
    public boolean saveUser(SignUpRequest dto) {
        if (userRepository.findByUsername(dto.getUsername()) != null) {
            return false;
        }
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            return false;
        }
        User newUser = new User();
        Role role = roleRepository.findByName("ROLE_USER");
        newUser.setRole(role);
        newUser.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        userRepository.save(newUser);
        return true;
    }
}
