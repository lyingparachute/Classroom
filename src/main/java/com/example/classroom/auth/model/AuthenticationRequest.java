package com.example.classroom.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthenticationRequest(
    @NotBlank(message = "{message.email.empty}")
    @Email(message = "{message.email.valid}")
    String email,
    String password
) {
}
