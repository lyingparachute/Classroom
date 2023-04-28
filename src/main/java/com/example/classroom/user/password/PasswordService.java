package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.user.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserManagementService userService;
    private final PasswordResetTokenRepository passwordTokenRepository;
    private final MailSenderService mailSenderService;

    boolean resetPassword(HttpServletRequest request, String userEmail) {
        try {
            User user = userService.loadUserByUsername(userEmail);
            String token = createAndSavePasswordResetToken(user);
            mailSenderService.sendResetPasswordEmail(
                    PasswordResetRequest.builder()
                            .httpRequest(request)
                            .userEmail(userEmail)
                            .token(token)
                            .build()
            );

            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        } catch (MessagingException e) {
            String exception = e.toString();
            e.getCause();
        }
        return false;
    }

    private String createAndSavePasswordResetToken(User user) {
        PasswordResetToken myToken = PasswordResetToken.builder()
                .user(user)
                .token(createRandomToken())
                .build();
        return savePasswordResetToken(myToken).getToken();
    }

    private String createRandomToken() {
        return UUID.randomUUID().toString();
    }

    private PasswordResetToken savePasswordResetToken(PasswordResetToken myToken) {
        return passwordTokenRepository.save(myToken);
    }

    String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().isBefore(LocalDate.now());
    }

    Optional<User> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }
}