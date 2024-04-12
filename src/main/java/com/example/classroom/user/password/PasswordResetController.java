package com.example.classroom.user.password;

import com.example.classroom.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("password")
@RequiredArgsConstructor
public class PasswordResetController {

    private static final String REDIRECT_TO_SIGN_IN_PAGE = "redirect:/sign-in";
    private static final String PASSWORD_CHANGE_TEMPLATE = "auth/password-change";
    private final PasswordResetService service;

    @PostMapping("reset")
    String sendResetPasswordEmail(@RequestParam("email") final String userEmail,
                                  final HttpServletRequest request,
                                  final RedirectAttributes redirectAttributes) {
        final var emailSent = service.sendEmailWithResetPasswordInstructions(request, userEmail);
        redirectAttributes.addFlashAttribute("resetPassword", userEmail);
        redirectAttributes.addFlashAttribute("resetPasswordResult", emailSent);
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @GetMapping("change")
    String showPasswordChangeForm(@RequestParam("token") final String token,
                                  final Model model,
                                  final RedirectAttributes redirectAttributes) {
        try {
            service.validatePasswordResetToken(token);
        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("resetPasswordInvalidToken", "fail");
            return REDIRECT_TO_SIGN_IN_PAGE;
        }
        model.addAttribute("passwordReset", new PasswordRequest());
        model.addAttribute("token", token);
        return PASSWORD_CHANGE_TEMPLATE;
    }


    @PostMapping("update")
    String changePassword(@Valid @ModelAttribute("passwordReset") final PasswordRequest passwordRequest,
                          final BindingResult result,
                          @ModelAttribute("token") final String token,
                          final HttpServletRequest request,
                          final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return PASSWORD_CHANGE_TEMPLATE;
        }
        service.resetPassword(
                request,
                token,
                passwordRequest.password()
        );
        redirectAttributes.addFlashAttribute("resetPasswordSuccess", "Your password has been reset.");
        return REDIRECT_TO_SIGN_IN_PAGE;
    }
}