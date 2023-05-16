package com.example.classroom.user.password;

import com.example.classroom.auth.validation.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
        String oldPassword;
        String password;
        String matchingPassword;
}
