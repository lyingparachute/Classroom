package com.example.classroom.user.password;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService service;

    @PostMapping("/reset")
    String resetPassword(@Valid @RequestParam("email") String userEmail,
                         RedirectAttributes redirectAttributes) {
        boolean resetResult = service.resetPassword(userEmail);
        redirectAttributes.addFlashAttribute("resetPassword", userEmail);
        redirectAttributes.addFlashAttribute("resetPasswordResult", resetResult);
        return "redirect:/sign-in";
    }
}
