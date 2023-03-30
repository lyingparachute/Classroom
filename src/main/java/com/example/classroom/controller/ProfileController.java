package com.example.classroom.controller;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.model.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.security.Principal;

@Controller
@RequestMapping("/dashboard/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserManagementService service;
    private final BreadcrumbService crumb;

    public static final String USER_EDIT_TEMPLATE = "user/user-edit";
    public static final String FIELDS_OF_STUDY_UPLOAD_DIR = "fields-of-study/";

    @GetMapping
    public String getUserDetailsPage(Model model,
                                     HttpServletRequest request,
                                     Principal principal) {
        addAttributeBreadcrumb(model, request);
        addAttributeUserByUsername(model, principal);
        model.addAttribute("imagesPath", Path.of("/img").resolve(FIELDS_OF_STUDY_UPLOAD_DIR));
        return "user/user-view";
    }


    @GetMapping("/edit")
    public String getEditUserDetailsPage(Model model,
                                         HttpServletRequest request,
                                         Principal principal) {
        addAttributeBreadcrumb(model, request);
        addAttributeUserByUsername(model, principal);
        return USER_EDIT_TEMPLATE;
    }

    @PostMapping("/update")
    public String updateUserDetails(@Valid @ModelAttribute User user,
                                    BindingResult result,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            return USER_EDIT_TEMPLATE;
        }
        User updated = service.update(user);
        redirectAttributes.addFlashAttribute("editSuccess", updated);
        return "redirect:/dashboard/profile";
    }

    private void addAttributeUserByUsername(Model model, Principal principal) {
        model.addAttribute("user", service.loadUserByUsername(principal.getName()));
    }

    private void addAttributeBreadcrumb(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }
}
