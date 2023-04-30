package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private static final String PASSWORD_RESET_TEMPLATE_LOCATION = "mail/password-reset.html";
    private static final String PASSWORD_RESET_EMAIL_SUBJECT = "Password Reset";
    private static final String PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION = "mail/password-reset-confirm.html";
    private static final String PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT = "Password Reset Confirmation";
    private final UserManagementService userService;
    private final PasswordResetTokenRepository passwordTokenRepository;
    private final MailSenderService mailSenderService;

    boolean sendEmailWithResetPasswordInstructions(HttpServletRequest request, String userEmail) {
        try {
            User user = userService.loadUserByUsername(userEmail);
            String token = createAndSavePasswordResetToken(user);
            sendPasswordResetEmail(request, user, token);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    private void sendPasswordResetEmail(HttpServletRequest request, User user, String token) {
        mailSenderService.sendEmail(
                user.getEmail(),
                PASSWORD_RESET_EMAIL_SUBJECT,
                PASSWORD_RESET_TEMPLATE_LOCATION,
                createTemplateAttributes(request, user, token)
        );
    }

    private Map<String, Object> createTemplateAttributes(HttpServletRequest request, User user, String token) {
        return ofEntries(
                entry("firstName", user.getFirstName()),
                entry("resetLink", getPasswordChangeLink(request, token)),
                entry("websiteLink", getAppUrl(request))
        );
    }

    private String createAndSavePasswordResetToken(User user) {
        PasswordResetToken myToken = PasswordResetToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .build();
        return passwordTokenRepository.save(myToken).getToken();
    }

    String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = getPasswordResetToken(token);
        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDate.now());
    }

    Optional<User> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }

    private String getPasswordChangeLink(HttpServletRequest request, String token) {
        return getAppUrl(request) + "/password/change?token=" + token;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private PasswordResetToken getPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token);
    }

    private PasswordResetToken getPasswordResetToken(User user) {
        return passwordTokenRepository.findByUser(user);
    }

    void resetPassword(String token, String password) {
        User user = getUserByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token: " + token));
        userService.changeUserPassword(user, password);
        sendConfirmPasswordResetEmail(user);
    }

    private void sendConfirmPasswordResetEmail(User user) {
        mailSenderService.sendEmail(
                user.getEmail(),
                PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT,
                PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION,
                ofEntries(
                        entry("firstName", user.getFirstName())
                )
        );
    }
}