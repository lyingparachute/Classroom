package com.example.classroom.user.password;

import com.example.classroom.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("password")
@RequiredArgsConstructor
public class PasswordResetController {

    private static final String REDIRECT_TO_SIGN_IN_PAGE = "redirect:/sign-in";
    private static final String PASSWORD_CHANGE_TEMPLATE = "auth/password-change";
    private final PasswordService service;

    @PostMapping("reset")
    String sendResetPasswordEmail(@RequestParam("email") String userEmail,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        boolean emailSent = service.sendEmailWithResetPasswordInstructions(request, userEmail);
        redirectAttributes.addFlashAttribute("resetPassword", userEmail);
        redirectAttributes.addFlashAttribute("resetPasswordResult", emailSent);
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @GetMapping("change")
    String showPasswordChangeForm(@RequestParam("token") final String token,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            service.validatePasswordResetToken(token);
        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("resetPasswordInvalidToken", "fail");
            return REDIRECT_TO_SIGN_IN_PAGE;
        }
        model.addAttribute("passwordReset", new PasswordResetRequest());
        model.addAttribute("token", token);
        return PASSWORD_CHANGE_TEMPLATE;
    }


    @PostMapping("update")
    String changePassword(@Valid @ModelAttribute("passwordReset") final PasswordResetRequest passwordResetRequest,
                          BindingResult result,
                          @ModelAttribute("token") final String token,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return PASSWORD_CHANGE_TEMPLATE;
        }
        service.resetPassword(
                request,
                token,
                passwordResetRequest.getPassword()
        );
        redirectAttributes.addFlashAttribute("resetPasswordSuccess", "Your password has been reset.");
        return REDIRECT_TO_SIGN_IN_PAGE;
    }
}