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

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static com.example.classroom.mail_sender.MailSenderService.getSignInLink;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final String PASSWORD_RESET_TEMPLATE_LOCATION = "mail/password-reset.html";
    private static final String PASSWORD_RESET_EMAIL_SUBJECT = "Password Reset";
    private static final String PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION = "mail/password-reset-confirmation.html";
    private static final String PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT = "Password Reset Confirmation";
    private final UserManagementService userService;
    private final PasswordResetTokenRepository passwordTokenRepository;
    private final MailSenderService mailService;

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


    void validatePasswordResetToken(final String token) throws InvalidTokenException {
        final PasswordResetToken passToken = getPasswordResetToken(token);
        if (!passToken.isTokenValid()) throw new InvalidTokenException("Token expired or revoked: " + token);
    }

    void resetPassword(final HttpServletRequest request,
                       final String token,
                       final String newPassword) {
        final var user = getUserByPasswordResetToken(token);
        userService.updateUserPassword(user, newPassword);
        final var passwordResetToken = getPasswordResetToken(token);
        revokeToken(passwordResetToken);
        sendPasswordResetConfirmationEmail(request, user);
    }

    private void sendPasswordResetEmail(final HttpServletRequest request,
                                        final User user,
                                        final String token) {
        mailService.sendEmail(
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
        final var myToken = new PasswordResetToken(
                user,
                UUID.randomUUID().toString()
        );
        return passwordTokenRepository.save(myToken).getToken();
    }

    private User getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(getPasswordResetToken(token).getUser())
                .orElseThrow(() -> new InvalidTokenException("Invalid token: " + token));
    }

    private String getPasswordChangeLink(final HttpServletRequest request, final String token) {
        return getAppUrl(request) + "/password/change?token=" + token;
    }

    private PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token: " + token));
    }

    private void revokeToken(final PasswordResetToken token) {
        token.setRevoked();
        passwordTokenRepository.save(token);
    }

    private void sendPasswordResetConfirmationEmail(final HttpServletRequest request,
                                                    final User user) {
        final var appUrl = getAppUrl(request);
        mailService.sendEmail(
                user.getEmail(),
                PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT,
                PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION,
                ofEntries(
                        entry("firstName", user.getFirstName()),
                        entry("signinLink", getSignInLink(appUrl)),
                        entry("websiteLink", appUrl)
                )
        );
    }
}