package com.example.classroom.auth.controller;

import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
class AuthenticationController {

    static final String SIGN_UP_TEMPLATE = "auth/sign-up";
    private final UserManagementService service;

    @GetMapping("/sign-in")
    String signIn() {
        return "auth/sign-in";
    }

    @GetMapping("/sign-up")
    String getSignUpPage(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return SIGN_UP_TEMPLATE;
    }

    @PostMapping("/sign-up")
    String signUp(@Valid @ModelAttribute("user") RegisterRequest user,
                  BindingResult result,
                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return SIGN_UP_TEMPLATE;
        User created = service.register(user);
        redirectAttributes.addFlashAttribute("createSuccess", created);
        return "redirect:/sign-in";
    }
}
