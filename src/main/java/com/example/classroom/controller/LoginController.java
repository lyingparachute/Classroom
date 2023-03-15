package com.example.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    public static final String LOGIN_FOLDER = "login/";

    @GetMapping("/sign-in")
    public String signIn() {
        return LOGIN_FOLDER + "sign-in";
    }

    @GetMapping("/sign-up")
    public String getSignUpPage() {
        return LOGIN_FOLDER + "sign-up";
    }

    @PostMapping("/api/sign-up")
    public String signUp() {
        return LOGIN_FOLDER + "sign-in";
    }

    @GetMapping("/password/reset")
    public String getPasswordResetPage() {
        return LOGIN_FOLDER + "password-reset";
    }

    @PostMapping("/password/reset")
    public String resetPassword() {
        return LOGIN_FOLDER + "sign-in";
    }
}
