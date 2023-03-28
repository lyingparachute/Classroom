package com.example.classroom.auth;

import com.example.classroom.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Length(min = 2, max = 30, message = "{message.firstName.length}")
    private String firstName;

    @Length(min = 2, max = 30, message = "{message.lastName.length}")
    private String lastName;

    @NotBlank(message = "{message.email.empty}")
    @Email(message = "{message.email.valid}")
    private String email;

    //    @ValidPassword
    private String password;

    @NotNull(message = "{role.not.null}")
    private UserRole role;
}
