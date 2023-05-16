package com.example.classroom.user;

import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.user.password.PasswordChangeRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.security.Principal;

@Controller
@RequestMapping("/dashboard/profile")
@RequiredArgsConstructor
class UserProfileController {

    private static final String PASSWORD_CHANGE_TEMPLATE = "user/password-change";
    private final UserManagementService service;
    private final BreadcrumbService crumb;

    private static final String USER_EDIT_TEMPLATE = "user/user-edit";
    private static final String FIELDS_OF_STUDY_UPLOAD_DIR = "fields-of-study/";

    @GetMapping
    String getUserDetailsPage(Model model,
                              HttpServletRequest request,
                              Principal principal) {
        addAttributeBreadcrumb(model, request);
        addAttributeUserByUsername(model, principal);
        model.addAttribute("imagesPath", Path.of("/img").resolve(FIELDS_OF_STUDY_UPLOAD_DIR));
        return "user/user-view";
    }


    @GetMapping("/edit")
    String getEditUserDetailsPage(Model model,
                                  HttpServletRequest request,
                                  Principal principal) {
        addAttributeBreadcrumb(model, request);
        addAttributeUserByUsername(model, principal);
        return USER_EDIT_TEMPLATE;
    }

    @PostMapping("/update")
    String updateUserDetails(@Valid @ModelAttribute UpdateRequest userRequest,
                             BindingResult result,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            return USER_EDIT_TEMPLATE;
        }
        User updated = service.update(userRequest);
        redirectAttributes.addFlashAttribute("editSuccess", updated);
        return "redirect:/dashboard/profile";
    }

    @GetMapping("delete/{id}")
    String deleteAccount(@PathVariable Long id,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        service.invalidateSession(request);
        service.removeById(id);
        redirectAttributes.addFlashAttribute("deleteSuccess", "removed");
        return "redirect:/sign-up";
    }

    @GetMapping("password/edit")
    String getPasswordChangePage(Model model,
                                 HttpServletRequest request,
                                 Principal principal) {
        addAttributeBreadcrumb(model, request);
        addAttributeUserByUsername(model, principal);
        return PASSWORD_CHANGE_TEMPLATE;
    }

    @PostMapping("password/edit")
    String updatePassword(@Valid @ModelAttribute("passwordReset") final PasswordChangeRequest passwordChangeRequest,
                          BindingResult result,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return PASSWORD_CHANGE_TEMPLATE;
        }
        service.changeUserPassword(principal.getName(), passwordChangeRequest);
        redirectAttributes.addFlashAttribute("passwordUpdateSuccess", "removed");
        return "redirect:/dashboard/profile";
    }

    private void addAttributeUserByUsername(Model model, Principal principal) {
        model.addAttribute("user", service.loadUserByUsername(principal.getName()));
    }

    private void addAttributeBreadcrumb(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }
}
