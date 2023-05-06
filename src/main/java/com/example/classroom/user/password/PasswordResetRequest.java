package com.example.classroom.user.password;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;

@Builder
public record PasswordResetRequest(
        String token,
        String userEmail,
        HttpServletRequest httpRequest
) {
}
