package com.example.classroom.user.password;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordController {

    @GetMapping("forgot-password")
    String getResetPasswordPage() {

        return "redirect:/sign-up";
    }

    @PostMapping("reset-password")
    String resetPassword(@RequestParam("email") String userEmail,
                         HttpServletRequest request) {

        return "redirect:/sign-up";
    }
}
