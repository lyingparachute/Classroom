package com.example.classroom.user.register;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.exception.UserAlreadyExistException;
import com.example.classroom.user.User;
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
    String getSignUpPage(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return SIGN_UP_TEMPLATE;
    }

    @PostMapping("sign-up")
    String createNewUserAccount(@Valid @ModelAttribute("user") RegisterRequest registerRequest,
                                BindingResult result,
                                Model model,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return SIGN_UP_TEMPLATE;
        try {
            User registered = service.register(registerRequest);
            registerService.sendAccountVerificationEmail(request, registered);
            redirectAttributes.addFlashAttribute("createSuccess", registered);
        } catch (UserAlreadyExistException e) {
            model.addAttribute("emailExists", registerRequest.getEmail());
            return SIGN_UP_TEMPLATE;
        }
        return REDIRECT_TO_SIGN_IN_PAGE;
    }

    @GetMapping("account/verify")
    String verifyAccount(@RequestParam("token") final String token,
                         RedirectAttributes redirectAttributes) {
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

    @PostMapping("account/verify/resend")
    String resendVerificationEmail(@RequestParam("email") String userEmail,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            registerService.resendAccountVerificationEmail(request, userEmail);
            redirectAttributes.addFlashAttribute("newVerificationEmailSent", userEmail);
        } catch (UsernameNotFoundException e) {
            redirectAttributes.addFlashAttribute("newVerificationEmailFail", userEmail);
        }
        return REDIRECT_TO_SIGN_IN_PAGE;
    }
}
