package com.example.hrms.controller;

import com.example.hrms.dto.AuthSessionDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/session")
    public AuthSessionDto session(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .sorted(Comparator.naturalOrder())
                .toList();
        return new AuthSessionDto(
                authentication.getName(),
                toDisplayName(authentication.getName()),
                roles,
                roles.contains("ADMIN") || roles.contains("HR")
        );
    }

    private String toDisplayName(String username) {
        if (username == null || username.isBlank()) {
            return "HR Team";
        }

        String normalized = username.replace('.', ' ')
                .replace('_', ' ')
                .replace('-', ' ')
                .trim();

        StringBuilder displayName = new StringBuilder();
        for (String part : normalized.split("\\s+")) {
            if (part.isBlank()) {
                continue;
            }
            if (!displayName.isEmpty()) {
                displayName.append(' ');
            }
            displayName.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                displayName.append(part.substring(1).toLowerCase());
            }
        }
        return displayName.toString();
    }
}
