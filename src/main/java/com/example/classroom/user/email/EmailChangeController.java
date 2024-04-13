package com.example.classroom.user.email;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class EmailChangeController {

    private static final String EMAIL_CHANGE_TEMPLATE = "user/email-change";
    private final UserManagementService userService;
    private final EmailChangeService emailChangeService;

    @GetMapping("/dashboard/profile/email")
    String getPasswordChangePage(final Model model,
                                 final HttpServletRequest request,
                                 final Principal principal) {
        addAttributesBreadcrumbAndUser(principal, model, request);
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
        model.addAttribute("user", userService.loadUserByUsername(principal.getName()));

        model.addAttribute("currentEmail", principal.getName());
        model.addAttribute("emailChange", new EmailChangeRequest());
        return EMAIL_CHANGE_TEMPLATE;
    }

    @PostMapping("/dashboard/profile/email")
    String updatePassword(@Valid @ModelAttribute("emailChange") final EmailChangeRequest emailChangeRequest,
                          final BindingResult result,
                          final Principal principal,
                          final Model model,
                          final HttpServletRequest request,
                          final RedirectAttributes redirectAttributes) {
        final var user = userService.loadUserByUsername(principal.getName());
        if (result.hasErrors()) {
            addAttributesBreadcrumbAndUser(principal, model, request);
            model.addAttribute("emailChange", emailChangeRequest);
            return EMAIL_CHANGE_TEMPLATE;
        }
        emailChangeService.sendEmailVerificationCode(request, user);
        return "redirect:/dashboard/profile";
    }

    private void addAttributesBreadcrumbAndUser(final Principal principal,
                                                final Model model,
                                                final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
        model.addAttribute("user", userService.loadUserByUsername(principal.getName()));
    }
}
