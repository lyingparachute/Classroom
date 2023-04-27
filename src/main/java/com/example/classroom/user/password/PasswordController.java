package com.example.classroom.user.password;

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
    String resetPassword(@RequestParam("email") String userEmail,
                         RedirectAttributes redirectAttributes) {
        service.resetPassword(userEmail);
        redirectAttributes.addFlashAttribute("reset-success", userEmail);
        return "redirect:/sign-in";
    }
}
