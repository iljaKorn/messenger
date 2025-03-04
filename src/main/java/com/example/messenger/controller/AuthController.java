package com.example.messenger.controller;

import com.example.messenger.dto.JwtAuthResponse;
import com.example.messenger.dto.RefreshJwtRequest;
import com.example.messenger.dto.SignInRequest;
import com.example.messenger.service.AuthService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody SignInRequest authRequest) throws AuthException {
        final JwtAuthResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtAuthResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtAuthResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtAuthResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
