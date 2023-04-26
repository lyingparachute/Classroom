package com.example.classroom.user.password;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordController {

    @PostMapping("reset-password")
    String getResetPasswordPage(@RequestParam("email") String userEmail,
                                HttpServletRequest request) {

        return "redirect:/sign-up";
    }
}
