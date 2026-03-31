package com.example.hrms.dto;

import java.util.List;

public record AuthSessionDto(
        String username,
        String displayName,
        List<String> roles,
        boolean canViewPayroll
) {
}
