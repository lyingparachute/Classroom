package com.example.classroom.user.password;

import com.example.classroom.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("password")
@RequiredArgsConstructor
public class PasswordController {

    private static final String REDIRECT_TO_SIGN_IN_PAGE = "redirect:/sign-in";
    private static final String PASSWORD_CHANGE_TEMPLATE = "auth/password-change";
    private final PasswordService service;

    @PostMapping("reset")
    String sendResetPasswordEmail(@Valid @RequestParam("email") String userEmail,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        boolean emailSent = service.sendEmailWithResetPasswordInstructions(request, userEmail);
        redirectAttributes.addFlashAttribute("resetPassword", userEmail);
        redirectAttributes.addFlashAttribute("resetPasswordResult", emailSent);
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @GetMapping("change")
    String showPasswordChangeForm(@Valid @RequestParam("token") final String token,
                                  Model model,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            service.validatePasswordResetToken(token);
        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("resetPasswordInvalidToken", "fail");
            return REDIRECT_TO_SIGN_IN_PAGE;
        }
        model.addAttribute("token", token);
        return PASSWORD_CHANGE_TEMPLATE;
    }


    @PostMapping("update")
    String changePassword(
            @Valid @RequestParam("token") final String token,
            @Valid @RequestParam("password") final String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        service.resetPassword(request, token, password);
        redirectAttributes.addFlashAttribute("resetPasswordSuccess", "Your password has been reset.");
        return REDIRECT_TO_SIGN_IN_PAGE;
    }
}