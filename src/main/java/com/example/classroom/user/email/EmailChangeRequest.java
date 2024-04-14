package com.example.classroom.user.email;

import com.example.classroom.auth.validation.email.EmailMatches;
import jakarta.validation.constraints.Email;
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
@EmailMatches
public class EmailChangeRequest {
    @Email(message = "{message.email.valid}")
    private String email;
    private String matchingEmail;
    private String expectedVerificationCode;
    private String verificationCode;
}
