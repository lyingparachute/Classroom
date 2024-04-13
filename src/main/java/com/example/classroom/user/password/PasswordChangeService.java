package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.mail_sender.MailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static com.example.classroom.mail_sender.MailSenderService.getSignInLink;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {

    private static final String PASSWORD_CHANGE_CONFIRM_TEMPLATE_LOCATION = "mail/password-change-confirmation.html";
    private static final String PASSWORD_CHANGE_CONFIRM_EMAIL_SUBJECT = "Password Changed Successfully!";
    private final UserManagementService userService;
    private final MailSenderService mailSenderService;

    void changeUserPassword(final PasswordChangeRequest passwordChangeRequest,
                            final String userEmail) {
        final var user = userService.loadUserByUsername(userEmail);
        userService.validateOldPassword(passwordChangeRequest.getOldPassword(), user.getPassword());
        userService.updateUserPassword(user, passwordChangeRequest.getPasswordRequest().getPassword());
    }

    void sendPasswordChangeConfirmationEmail(final HttpServletRequest request,
                                             final String userEmail) {
        final var user = userService.loadUserByUsername(userEmail);
        final var appUrl = getAppUrl(request);
        mailSenderService.sendEmail(
            user.getEmail(),
            PASSWORD_CHANGE_CONFIRM_EMAIL_SUBJECT,
            PASSWORD_CHANGE_CONFIRM_TEMPLATE_LOCATION,
            ofEntries(
                entry("firstName", user.getFirstName()),
                entry("signinLink", getSignInLink(appUrl)),
                entry("websiteLink", appUrl)
            )
        );
    }
}
