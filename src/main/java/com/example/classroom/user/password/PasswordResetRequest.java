package com.example.classroom.user.password;

import com.example.classroom.auth.validation.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
public record PasswordResetRequest(
        String password,
        String matchingPassword
) {
}
