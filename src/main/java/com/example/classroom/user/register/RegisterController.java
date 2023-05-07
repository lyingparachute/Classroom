package com.example.classroom.user.register;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.UserAlreadyExistException;
import com.example.classroom.user.User;
import jakarta.servlet.http.HttpServletRequest;
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
class RegisterController {

    private static final String SIGN_UP_TEMPLATE = "auth/sign-up";
    private final UserManagementService service;
    private final RegisterService registerService;

    @GetMapping("/sign-up")
    String getSignUpPage(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return SIGN_UP_TEMPLATE;
    }

    @PostMapping("/sign-up")
    String signUp(@Valid @ModelAttribute("user") RegisterRequest user,
                  BindingResult result,
                  Model model,
                  HttpServletRequest request,
                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return SIGN_UP_TEMPLATE;
        try {
            User registered = service.register(user);
            registerService.sendRegistrationConfirmationEmail(request, user);
            redirectAttributes.addFlashAttribute("createSuccess", registered);
        } catch (UserAlreadyExistException e) {
            model.addAttribute("emailExists", user.getEmail());
            return SIGN_UP_TEMPLATE;
        }
        return "redirect:/sign-in";
    }
}
