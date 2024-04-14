package com.example.classroom.user.email;

import com.example.classroom.mail_sender.MailSenderService;
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

    String sendEmailVerificationCode(final String email,
                                     final String firstName) {
        final var verificationCode = createEmailVerificationCode();
        mailService.sendEmail(email,
            "Email verification code",
            "mail/change-email-verification.html",
            ofEntries(
                entry("firstName", firstName),
                entry("verificationCode", verificationCode))
        );
        return verificationCode;
    }



    private String createEmailVerificationCode() {
        final var min = (int) Math.pow(10, VERIFICATION_CODE_LENGTH - 1);
        final var max = (int) Math.pow(10, VERIFICATION_CODE_LENGTH) - 1;
        return String.valueOf(RANDOM.nextInt(max - min + 1) + min);
    }
}
