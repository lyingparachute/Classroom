package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.exception.InvalidOldPasswordException;
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

import java.security.Principal;

@Controller
@RequestMapping("/dashboard/profile")
@RequiredArgsConstructor
public class PasswordChangeController {

    private static final String PASSWORD_CHANGE_TEMPLATE = "user/password-change";
    private final UserManagementService userService;
    private final PasswordChangeService passwordService;
    private final BreadcrumbService crumb;

    @GetMapping("password")
    String getPasswordChangePage(final Model model,
                                 final HttpServletRequest request,
                                 final Principal principal) {
        addAttributesBreadcrumbAndUser(principal, model, request);
        model.addAttribute("passwordChange", new PasswordChangeRequest());
        return PASSWORD_CHANGE_TEMPLATE;
    }

    @PostMapping("password")
    String updatePassword(@Valid @ModelAttribute("passwordChange") final PasswordChangeRequest passwordChangeRequest,
                          final BindingResult result,
                          final Principal principal,
                          final Model model,
                          final HttpServletRequest request,
                          final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addAttributesBreadcrumbAndUser(principal, model, request);
            model.addAttribute("passwordChange", passwordChangeRequest);
            return PASSWORD_CHANGE_TEMPLATE;
        }

        try {
            passwordService.changeUserPassword(passwordChangeRequest, principal.getName());
            passwordService.sendPasswordChangeConfirmationEmail(request, principal.getName());
            redirectAttributes.addFlashAttribute("passwordChangeSuccess", "success");
        } catch (InvalidOldPasswordException e) {
            model.addAttribute("invalidOldPassword", e.getMessage());
            addAttributesBreadcrumbAndUser(principal, model, request);
            return PASSWORD_CHANGE_TEMPLATE;
        }
        return "redirect:/dashboard/profile";
    }

    private void addAttributesBreadcrumbAndUser(final Principal principal,
                                                final Model model,
                                                final HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
        model.addAttribute("user", userService.loadUserByUsername(principal.getName()));
    }
}
