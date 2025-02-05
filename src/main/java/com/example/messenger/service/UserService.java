package com.example.messenger.service;

import com.example.messenger.dto.UserDTO;
import com.example.messenger.entity.Role;
import com.example.messenger.entity.User;
import com.example.messenger.repository.RoleRepository;
import com.example.messenger.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserService {
    //TODO пока непонятно почему не работают некоторые аннотации lombok
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Пользоваткль не найден");
        }
        return user;
    }

    public User findById(Long id) {
        Optional<User> userFromDB = userRepository.findById(id);
        return userFromDB.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(UserDTO dto) {
        User userFromDB = userRepository.findByUsername(dto.getUsername());
        if (userFromDB != null) {
            return false;
        }
        User newUser = new User();
        Role role = roleRepository.findByName("ROLE_USER");
        newUser.setRoles(Collections.singleton(role));
        newUser.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        newUser.setUsername(dto.getUsername());
        newUser.setEmail(dto.getEmail());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        userRepository.save(newUser);
        return true;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
