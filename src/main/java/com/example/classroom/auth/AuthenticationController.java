package com.example.classroom.auth;

import com.example.classroom.model.User;
import com.example.classroom.repository.RoleRepository;
import com.example.classroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService service;
    private final RoleRepository roleRepository;

    private static final String AUTH_FOLDER = "auth/";

    @GetMapping("/sign-in")
    public String signIn() {
        return AUTH_FOLDER + "sign-in";
    }

    @GetMapping("/sign-up")
    public String getSignUpPage(Model model) {
        //TODO change to Register request
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return AUTH_FOLDER + "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute RegisterRequest user,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return AUTH_FOLDER + "sign-up";
        User created = service.register(user);
        redirectAttributes.addFlashAttribute("createSuccess", created);
        return "redirect:/sign-in";
    }

    @GetMapping("/password/reset")
    public String getPasswordResetPage() {
        return AUTH_FOLDER + "password-reset";
    }

    @PostMapping("/password/reset")
    public String resetPassword() {
        return AUTH_FOLDER + "sign-in";
    }
}
