package com.example.classroom.user.password;

import com.example.classroom.auth.validation.password.PasswordMatches;
import com.example.classroom.auth.validation.password.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 This class needs to be mutable due to its usage by Thymeleaf
 */
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
