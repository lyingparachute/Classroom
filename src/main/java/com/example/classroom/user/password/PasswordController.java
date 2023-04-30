package com.example.classroom.user.password;

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
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {

    private static final String REDIRECT_TO_SIGN_IN_PAGE = "redirect:/sign-in";
    private final PasswordService service;

    @PostMapping("/reset")
    String sendResetPasswordEmail(@Valid @RequestParam("email") String userEmail,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        boolean resetResult = service.sendEmailWithResetPasswordInstructions(request, userEmail);
        redirectAttributes.addFlashAttribute("resetPassword", userEmail);
        redirectAttributes.addFlashAttribute("resetPasswordResult", resetResult);
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @GetMapping("/change")
    String getPasswordChangePage(@Valid @RequestParam("token") String token,
                                 Model model,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
//        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        return "auth/password-change";
    }


    @PostMapping("/update")
    String changePassword(@Valid @RequestParam("email") String userEmail,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        boolean resetResult = service.sendEmailWithResetPasswordInstructions(request, userEmail);
        redirectAttributes.addFlashAttribute("changePasswordSuccess", userEmail);
        return REDIRECT_TO_SIGN_IN_PAGE;
    }
}