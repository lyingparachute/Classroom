package com.example.classroom.user.password;

import com.example.classroom.auth.validation.PasswordMatches;
import com.example.classroom.auth.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {
        @ValidPassword
        String password;
        String matchingPassword;
}
