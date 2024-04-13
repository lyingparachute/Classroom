package com.example.classroom.auth.model;

import com.example.classroom.auth.validation.password.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;


@Builder
public record UpdateRequest(
    @Length(min = 2, max = 30, message = "{message.firstName.length}")
    String firstName,

    @Length(min = 2, max = 30, message = "{message.lastName.length}")
    String lastName,

    @NotBlank(message = "{message.email.empty}")
    @Email(message = "{message.email.valid}")
    String email,

    @ValidPassword
    String password
) {

//    @Length(min = 2, max = 30, message = "{message.firstName.length}")
//    private String firstName;
//
//    @Length(min = 2, max = 30, message = "{message.lastName.length}")
//    private String lastName;
//
//    @NotBlank(message = "{message.email.empty}")
//    @Email(message = "{message.email.valid}")
//    private String email;


    // FIXME - Password shouldn't be editable
    //    @ValidPassword
//    private String password;
}
