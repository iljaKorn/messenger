package com.example.messenger.service;

import com.example.messenger.dto.JwtAuthResponse;
import com.example.messenger.dto.SignInRequest;
import com.example.messenger.entity.JwtAuthentication;
import com.example.messenger.entity.User;
import com.example.messenger.utils.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для авторизации пользователей
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Метод для авторизации пользователя
     * @param authRequest сущность с данными пользователя
     */
    public JwtAuthResponse login(@NonNull SignInRequest authRequest) throws AuthException {
        User user = userService.findByUsername(authRequest.getUsername());
        if (user == null){
            throw new AuthException("Пользователь не найден");
        }
        if (bCryptPasswordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getUsername(), refreshToken);
            return new JwtAuthResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Неправильный пароль");
        }
    }

    /**
     * Метод для получения токена
     */
    public JwtAuthResponse getAccessToken(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findByUsername(login);
                if (user == null){
                    throw new AuthException("Пользователь не найден");
                }
                String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtAuthResponse(accessToken, null);
            }
        }
        return new JwtAuthResponse(null, null);
    }

    /**
     * Метод для обновления токена
     */
    public JwtAuthResponse refresh(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findByUsername(login);
                if (user == null){
                    throw new AuthException("Пользователь не найден");
                }
                String accessToken = jwtProvider.generateAccessToken(user);
                String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getUsername(), newRefreshToken);
                return new JwtAuthResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
