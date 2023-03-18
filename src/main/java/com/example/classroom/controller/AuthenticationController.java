package com.example.classroom.controller;

import com.example.classroom.model.UserLogin;
import com.example.classroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService service;

    public static final String LOGIN_FOLDER = "login/";

    @GetMapping("/sign-in")
    public String signIn() {
        return LOGIN_FOLDER + "sign-in";
    }

    @GetMapping("/sign-up")
    public String getSignUpPage(Model model) {
        model.addAttribute("user", new UserLogin());
        return LOGIN_FOLDER + "sign-up";
    }

    @PostMapping("/create-user")
    public String signUp(@ModelAttribute("user") UserLogin user,
                         RedirectAttributes redirectAttributes) {
        UserLogin created = service.create(user);
        redirectAttributes.addFlashAttribute("createSuccess", created);
        return "redirect:/sign-in";
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
