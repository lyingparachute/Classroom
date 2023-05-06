package com.example.classroom.user.register;

import com.example.classroom.mail_sender.MailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MailSenderService mailService;

    void sendRegistrationConfirmationEmail(HttpServletRequest request, RegisterRequest user) {
        mailService.sendEmail(user.getEmail(),
                "Welcome to Classroom",
                "mail/account-create-confirmation.html",
                ofEntries(
                        entry("firstName", user.getFirstName()),
                        entry("confirmLink", getConfirmationLink(request, user.getEmail())),
                        entry("websiteLink", getAppUrl(request))
                )
        );
    }

    private String getConfirmationLink(HttpServletRequest request, String userEmail) {
        return getAppUrl(request) + "/verify/" + userEmail;
    }
}
