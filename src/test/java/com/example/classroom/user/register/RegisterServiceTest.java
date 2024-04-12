package com.example.classroom.user.register;

import com.example.classroom.exception.AccountAlreadyVerifiedException;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.test.util.UnitTestsInitData;
import com.example.classroom.user.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
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
            final var user = initData.createUnverifiedUser();
            final var emailSubject = "Welcome to Classroom! Verify your email address.";
            final var fileLocation = "mail/account-create-confirmation.html";
            final var token = "verification-token";
            final var verificationToken = new VerificationToken(user, token);
            final var confirmationLink = "http://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort() +
                servletRequest.getContextPath() + "/account/verify?token=" + token;

            // When
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

            service.sendAccountVerificationEmail(servletRequest, user);

            // Then
            then(tokenRepository).should().save(any(VerificationToken.class));
            then(mailService).should().sendEmail(
                user.getEmail(),
                emailSubject,
                fileLocation,
                Map.ofEntries(entry("firstName", user.getFirstName()),
                    entry("confirmLink", confirmationLink),
                    entry("websiteLink", getAppUrl(servletRequest)))
            );
        }

        @Test
        void throwsAccountAlreadyVerifiedException_givenEnabledUser() {
            // Given
            final var user = initData.createUser(null);

            // When, Then
            assertThatThrownBy(() -> service.sendAccountVerificationEmail(servletRequest, user))
                .isExactlyInstanceOf(AccountAlreadyVerifiedException.class)
                .hasMessage("Account with email '" + user.getEmail() + "' is already verified.");
        }
    }

    @Nested
    class ValidateVerificationToken {
        @Test
        void returnsNothing_givenValidToken() {
            // Given
            final var user = initData.createUnverifiedUser();
            final var token = "verification-token";
            final var verificationToken = new VerificationToken(user, token);

            // When
            when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
            service.validateVerificationToken(token);

            // Then
            then(tokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenRevokedToken() {
            // Given
            final var user = initData.createUnverifiedUser();
            final var token = "verification-token";
            final var verificationToken = new VerificationToken(user, token);
            verificationToken.setRevoked();

            // When, Then
            when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
            assertThatThrownBy(() -> service.validateVerificationToken(token))
                .isExactlyInstanceOf(InvalidTokenException.class)
                .hasMessage("Verification Token expired or revoked: " + token);
        }

        @Test
        void throwsInvalidTokenException_givenExpiredToken() {
            // Given
            final var user = initData.createUnverifiedUser();
            final var token = "verification-token";
            final var verificationToken = new VerificationToken(user, token);
            ReflectionTestUtils.setField(verificationToken, "expiryDate", LocalDateTime.now().minusMinutes(1));
            given(tokenRepository.findByToken(token)).willReturn(Optional.of(verificationToken));

            // When, Then
            assertThatThrownBy(() -> service.validateVerificationToken(token))
                .isExactlyInstanceOf(InvalidTokenException.class)
                .hasMessage("Verification Token expired or revoked: " + token);
        }

        @Test
        void throwsInvalidTokenException_givenNotExistingToken() {
            // Given
            final var token = "verification-token";

            // When, Then
            when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.validateVerificationToken(token))
                .isExactlyInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid Verification Token: " + token);

        }
    }

    @Nested
    class VerifyAccount {
        @Test
        void savesVerifiedUser_givenValidToken() {
            // Given
            final var user = initData.createUnverifiedUser();
            final var token = "verification-token";
            final var verificationToken = new VerificationToken(user, token);

            // When
            when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
            service.verifyAccount(token);

            // Then
            then(tokenRepository).should(times(2)).findByToken(token);
            then(userRepository).should().save(user);
        }

        @Test
        void throwsInvalidTokenException_givenNotExistingToken() {
            // Given
            final var token = "verification-token";

            // When, Then
            when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.validateVerificationToken(token))
                .isExactlyInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid Verification Token: " + token);
        }
    }
}