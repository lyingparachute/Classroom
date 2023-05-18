package com.example.classroom.user.password;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    @NotBlank(message = "{message.old.password.empty}")
    private String oldPassword;
    @Valid
    private NewPasswordRequest newPasswordRequest;
}
