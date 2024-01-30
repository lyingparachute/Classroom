package com.example.classroom.user.register;

import com.example.classroom.exception.AccountAlreadyVerifiedException;
import com.example.classroom.exception.InvalidTokenException;
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
import java.util.Optional;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
            User user = initData.createUnverifiedUser();
            String emailSubject = "Welcome to Classroom! Verify your email address.";
            String fileLocation = "mail/account-create-confirmation.html";
            String token = "verification-token";
            VerificationToken verificationToken = new VerificationToken(user, token);
            String confirmationLink = "http://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort() +
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
            User user = initData.createUser();

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
            User user = initData.createUnverifiedUser();
            String token = "verification-token";
            VerificationToken verificationToken = new VerificationToken(user, token);

            // When
            when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
            service.validateVerificationToken(token);

            // Then
            then(tokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenRevokedToken() {
            // Given
            User user = initData.createUnverifiedUser();
            String token = "verification-token";
            VerificationToken verificationToken = new VerificationToken(user, token);
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
            User user = initData.createUnverifiedUser();
            String token = "verification-token";
            VerificationToken verificationToken = new VerificationToken(user, token);

            // When, Then
//            Clock clock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.of("UTC"));
//            LocalDateTime dateTime = LocalDateTime.now(clock);
//            doReturn(dateTime).when(LocalDateTime.now());
            when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
            assertThatThrownBy(() -> service.validateVerificationToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Verification Token expired or revoked: " + token);
        }

        @Test
        void throwsInvalidTokenException_givenNotExistingToken() {
            // Given
            String token = "verification-token";

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
            User user = initData.createUnverifiedUser();
            String token = "verification-token";
            VerificationToken verificationToken = new VerificationToken(user, token);

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
            String token = "verification-token";

            // When, Then
            when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.validateVerificationToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid Verification Token: " + token);
        }
    }
}