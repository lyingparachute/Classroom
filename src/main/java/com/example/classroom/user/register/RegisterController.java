package com.example.classroom.user.register;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.AccountAlreadyVerifiedException;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.exception.UserAlreadyExistException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
class RegisterController {

    private static final String SIGN_UP_TEMPLATE = "auth/sign-up";
    private static final String REDIRECT_TO_SIGN_IN_PAGE = "redirect:/sign-in";
    private final UserManagementService service;
    private final RegisterService registerService;

    @GetMapping("sign-up")
    String getSignUpPage(final Model model) {
        model.addAttribute("user", new RegisterRequest());
        return SIGN_UP_TEMPLATE;
    }

    @PostMapping("sign-up")
    String createNewUserAccount(@Valid @ModelAttribute("user") final RegisterRequest registerRequest,
                                final BindingResult result,
                                final Model model,
                                final HttpServletRequest request,
                                final RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return SIGN_UP_TEMPLATE;
        try {
            final var registered = service.register(registerRequest);
            registerService.sendAccountVerificationEmail(request, registered);
            redirectAttributes.addFlashAttribute("createSuccess", registered);
        } catch (UserAlreadyExistException e) {
            model.addAttribute("emailExists", registerRequest.email());
            return SIGN_UP_TEMPLATE;
        }
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @GetMapping("account/verify")
    String verifyAccount(@RequestParam("token") final String token,
                         final RedirectAttributes redirectAttributes) {
        try {
            registerService.validateVerificationToken(token);
        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("invalidEmailVerificationToken", "Email not verified");
            return REDIRECT_TO_SIGN_IN_PAGE;
        }
        registerService.verifyAccount(token);
        redirectAttributes.addFlashAttribute("emailVerificationSuccess", "Email verified");
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @PostMapping("verification-email/send")
    String resendVerificationEmail(@RequestParam("email") final String userEmail,
                                   final HttpServletRequest request,
                                   final RedirectAttributes redirectAttributes) {
        try {
            final var user = service.loadUserByUsername(userEmail);
            registerService.sendAccountVerificationEmail(request, user);
            redirectAttributes.addFlashAttribute("resendVerificationEmailSuccess", userEmail);
        } catch (UsernameNotFoundException e) {
            redirectAttributes.addFlashAttribute("accountNotFound", userEmail);
        } catch (AccountAlreadyVerifiedException e) {
            redirectAttributes.addFlashAttribute("accountAlreadyVerified", userEmail);
        }
        return REDIRECT_TO_SIGN_IN_PAGE;
    }
}
