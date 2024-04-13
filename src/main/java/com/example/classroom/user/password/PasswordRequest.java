package com.example.classroom.user.password;

import com.example.classroom.auth.validation.PasswordMatches;
import com.example.classroom.auth.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class PasswordRequest {
    @ValidPassword
    private String password;
    private String matchingPassword;
}
