package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    boolean sendEmailWithResetPasswordInstructions(final HttpServletRequest request,
                                                   final String userEmail) {
        try {
            final User user = userService.loadUserByUsername(userEmail);
            final String token = createAndSavePasswordResetToken(user);
            sendPasswordResetEmail(request, user, token);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    private void sendPasswordResetEmail(final HttpServletRequest request,
                                        final User user,
                                        final String token) {
        mailSenderService.sendEmail(
                user.getEmail(),
                PASSWORD_RESET_EMAIL_SUBJECT,
                PASSWORD_RESET_TEMPLATE_LOCATION,
                createTemplateAttributes(request, user, token)
        );
    }

    private Map<String, Object> createTemplateAttributes(final HttpServletRequest request,
                                                         final User user,
                                                         final String token) {
        return ofEntries(
                entry("firstName", user.getFirstName()),
                entry("resetLink", getPasswordChangeLink(request, token)),
                entry("websiteLink", getAppUrl(request))
        );
    }

    private String createAndSavePasswordResetToken(final User user) {
        final PasswordResetToken myToken = new PasswordResetToken(
                user,
                UUID.randomUUID().toString()
        );
        return passwordTokenRepository.save(myToken).getToken();
    }

    String validatePasswordResetToken(final String token) throws InvalidTokenException {
        final PasswordResetToken passToken = getPasswordResetToken(token);
        if (!passToken.isTokenValid()) throw new InvalidTokenException("Token expired or revoked: " + token);
        return "valid";
    }

    User getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(getPasswordResetToken(token).getUser())
                .orElseThrow(() -> new InvalidTokenException("Invalid token: " + token));
    }

    private String getPasswordChangeLink(final HttpServletRequest request, final String token) {
        return getAppUrl(request) + "/password/change?token=" + token;
    }

    private String getAppUrl(final HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token: " + token));
    }

    private PasswordResetToken getPasswordResetToken(final User user) {
        return passwordTokenRepository.findByUser(user)
                .orElseThrow(() -> new InvalidTokenException("No tokens for user with email: " + user.getEmail()));
    }

    void resetPassword(final String token, final String newPassword) {
        User user = getUserByPasswordResetToken(token);
        userService.resetUserPassword(user, newPassword);
        PasswordResetToken passwordResetToken = getPasswordResetToken(token);
        revokeToken(passwordResetToken);

        // TODO - send confirm reset email
//        sendConfirmPasswordResetEmail(user);
    }

    void revokeToken(final PasswordResetToken token) {
        token.setRevoked();
        passwordTokenRepository.save(token);
    }

    private void sendConfirmPasswordResetEmail(final User user) {
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