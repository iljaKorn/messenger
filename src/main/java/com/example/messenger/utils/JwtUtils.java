package com.example.messenger.utils;

import com.example.messenger.entity.JwtAuthentication;
import com.example.messenger.entity.Role;
import com.example.messenger.repository.RoleRepository;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final RoleRepository roleRepository;

    public JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private Set<Role> getRoles(Claims claims) {
        final List<String> roles = new ArrayList<>();
        String str = claims.get("role", String.class);
        roles.add(str);
        Set<Role> roles1 = new HashSet<>();
        for (String r : roles) {
            roles1.add(roleRepository.findByName(r));
        }
        return roles1;
    }
}
