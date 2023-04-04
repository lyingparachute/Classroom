package com.example.classroom.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

    @Length(min = 2, max = 30, message = "{message.firstName.length}")
    private String firstName;

    @Length(min = 2, max = 30, message = "{message.lastName.length}")
    private String lastName;

    @NotBlank(message = "{message.email.empty}")
    @Email(message = "{message.email.valid}")
    private String email;

    //    @ValidPassword
    private String password;
}
