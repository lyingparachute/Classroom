package com.example.classroom.controller;

import com.example.classroom.model.User;
import com.example.classroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/dashboard/profile")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserService service;

    public static final String USER_EDIT_FORM = "user/user-edit";

    @GetMapping
    public String getUserDetailsPage(Model model,
                                     Principal principal) {
        addAttributeUserByUsername(model, principal);
        return "user/user-view";
    }


    @GetMapping("/edit")
    public String getEdituserDetailsPage(Model model,
                                         Principal principal) {
        addAttributeUserByUsername(model, principal);
        return USER_EDIT_FORM;
    }

    @PostMapping("/update")
    public String updateUserDetails(@Valid @ModelAttribute User user,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return USER_EDIT_FORM;
        }
        User updated = service.update(user);
        redirectAttributes.addFlashAttribute("editSuccess", updated);
        return "redirect:/dashboard/profile";
    }

    private void addAttributeUserByUsername(Model model, Principal principal) {
        model.addAttribute("user", service.loadUserByUsername(principal.getName()));
    }
}
