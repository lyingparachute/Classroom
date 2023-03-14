package com.example.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    public static final String LOGIN_FOLDER = "login/";

    @GetMapping("/sign-in")
    public String signIn() {
        return LOGIN_FOLDER + "sign-in";
    }

    @GetMapping("/sign-out")
    public String signOut() {
        return LOGIN_FOLDER + "sign-out";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        return LOGIN_FOLDER + "sign-up";
    }

    @GetMapping("password-reset")
    public String passwordReset() {
        return LOGIN_FOLDER + "password-reset";
    }
}
