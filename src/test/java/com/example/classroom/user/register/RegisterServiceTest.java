package com.example.classroom.user.register;

import com.example.classroom.exception.AccountAlreadyVerifiedException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.test.util.UnitTestsInitData;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @InjectMocks
    RegisterService service;
    @Mock
    MailSenderService mailService;
    @Mock
    VerificationTokenRepository tokenRepository;
    @Mock
    UserRepository userRepository;
    @Spy
    MockHttpServletRequest servletRequest;
    @Spy
    UnitTestsInitData initData;

    @Nested
    class SendAccountVerificationEmail {
        @Test
        void sendsVerificationEmail_givenDisabledUser() {
            // Given
            User user = initData.createDisabledUser();
            String emailSubject = "Welcome to Classroom! Verify your email address.";
            String fileLocation = "mail/account-create-confirmation.html";
            String token = "verification-token";
            VerificationToken verificationToken = new VerificationToken(user, token);
            String confirmLink = "http://localhost:80/account/verify?token=" + token;

            // When
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

            service.sendAccountVerificationEmail(servletRequest, user);

            // Then
            verify(tokenRepository).save(any(VerificationToken.class));
            verify(mailService).sendEmail(
                    user.getEmail(),
                    emailSubject,
                    fileLocation,
                    Map.ofEntries(entry("firstName", user.getFirstName()),
                            entry("confirmLink", confirmLink),
                            entry("websiteLink", getAppUrl(servletRequest)))
            );
        }

        @Test
        void throwsAccountAlreadyVerifiedException_givenEnabledUser() {
            // Given
            User user = initData.createUser();
            String token = "verification-token";

            // When
            Throwable thrown = catchThrowable(() -> service.sendAccountVerificationEmail(servletRequest, user));

            // Then
            assertThat(thrown).isExactlyInstanceOf(AccountAlreadyVerifiedException.class)
                    .hasMessage("Account with email '" + user.getEmail() + "' is already verified.");
        }
    }

    @Nested
    class ValidateVerificationToken {
        @Test
        void doesNothing_whenTokenValid() {
            // Given

            // When

            // Then
        }

        @Test
        void throwsInvalidTokenException_whenTokenInvalid() {
            // Given

            // When

            // Then
        }
    }

    @Nested
    class VerifyAccount {
        @Test
        void verifyAccount() {
            // Given

            // When

            // Then
        }
    }
}