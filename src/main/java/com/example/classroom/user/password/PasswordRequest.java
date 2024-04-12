package com.example.classroom.user.password;

import com.example.classroom.auth.validation.PasswordMatches;

@PasswordMatches
public record PasswordRequest(
//    @ValidPassword
    String password,
    String matchingPassword
) {
    PasswordRequest() {
        this(null, null);
    }
}
