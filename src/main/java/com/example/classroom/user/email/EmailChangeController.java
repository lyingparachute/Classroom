package com.example.classroom.user.email;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class EmailChangeController {

    private static final String EMAIL_CHANGE_TEMPLATE = "user/email-change";
    private static final String EMAIL_VERIFY_TEMPLATE = "user/email-change-verify";
    private final UserManagementService userService;
    private final EmailChangeService emailChangeService;

    @GetMapping("/dashboard/profile/email")
    String getPasswordChangePage(final Model model,
                                 final HttpServletRequest request,
                                 final Principal principal) {
        final var user = userService.loadUserByUsername(principal.getName());
        addAttributesBreadcrumbAndUser(user, model, request);
        model.addAttribute("emailChange", new EmailChangeRequest());
        return EMAIL_CHANGE_TEMPLATE;
    }

    @PostMapping("/dashboard/profile/email")
    String sendVerificationCode(@Valid @ModelAttribute("emailChange") final EmailChangeRequest emailChangeRequest,
                                final BindingResult result,
                                final Principal principal,
                                final Model model,
                                final HttpServletRequest request,
                                final RedirectAttributes redirectAttributes) {
        final var user = userService.loadUserByUsername(principal.getName());
        addAttributesBreadcrumbAndUser(user, model, request);
        model.addAttribute("emailChange", emailChangeRequest);
        if (result.hasErrors()) {
            return EMAIL_CHANGE_TEMPLATE;
        }
        final var verificationCode = emailChangeService.sendEmailVerificationCode(emailChangeRequest.getEmail(), user.getFirstName());
        emailChangeRequest.setExpectedVerificationCode(verificationCode);
        return EMAIL_VERIFY_TEMPLATE;
    }

    @PostMapping("/dashboard/profile/email/update")
    String changeEmail(@ModelAttribute("emailChange") final EmailChangeRequest emailChangeRequest,
                       final BindingResult result,
                       final Principal principal,
                       final Model model,
                       final HttpServletRequest request,
                       final RedirectAttributes redirectAttributes) {
        final var user = userService.loadUserByUsername(principal.getName());

        if (!emailChangeRequest.getVerificationCode().equals(emailChangeRequest.getExpectedVerificationCode())) {
            result.addError(new FieldError("emailChange", "verificationCode", "Invalid verification code!"));
            addAttributesBreadcrumbAndUser(user, model, request);
            model.addAttribute("emailChange", emailChangeRequest);
            return EMAIL_VERIFY_TEMPLATE;
        }
        userService.updateUserEmail(user, emailChangeRequest.getEmail());
        return "redirect:/dashboard/profile";
    }

    private void addAttributesBreadcrumbAndUser(final User user,
                                                final Model model,
                                                final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
        model.addAttribute("user", user);
    }
}