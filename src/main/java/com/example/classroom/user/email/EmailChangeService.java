package com.example.classroom.user.email;

import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@RequiredArgsConstructor
public class EmailChangeService {

    private static final double VERIFICATION_CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();
    private final MailSenderService mailService;

    void sendEmailVerificationCode(final HttpServletRequest request,
                                   final User user) {
        final var verificationCode = createEmailVerificationCode();
        mailService.sendEmail(user.getEmail(),
            "Verification code",
            "mail/change-email-verification.html",
            ofEntries(
                entry("firstName", user.getFirstName()),
                entry("verificationCode", verificationCode))
        );
    }

    private String createEmailVerificationCode() {
        final var min = (int) Math.pow(10, VERIFICATION_CODE_LENGTH - 1);
        final var max = (int) Math.pow(10, VERIFICATION_CODE_LENGTH) - 1;
        return String.valueOf(RANDOM.nextInt(max - min + 1) + min);
    }
}
