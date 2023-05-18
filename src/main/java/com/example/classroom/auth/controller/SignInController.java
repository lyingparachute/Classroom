package com.example.classroom.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
class SignInController {

    @GetMapping("/sign-in")
    String signIn() {
        return "auth/sign-in";
    }
}
