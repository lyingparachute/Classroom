package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.test.util.UnitTestsInitData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @InjectMocks
    PasswordResetService service;
    @Mock
    UserManagementService userService;
    @Mock
    PasswordResetTokenRepository passwordTokenRepository;
    @Mock
    MailSenderService mailService;
    @Spy
    UnitTestsInitData initData;
    @Spy
    MockHttpServletRequest servletRequest;

    @Nested
    class SendEmailWithResetPasswordInstructions {
        @Test
        void returnsTrue_sendsEmailWithToken_givenValidParameters() {
            // Given
            final var PASSWORD_RESET_TEMPLATE_LOCATION = "mail/password-reset.html";
            final var PASSWORD_RESET_EMAIL_SUBJECT = "Password Reset";

            final var user = initData.createUser();
            final var passwordResetToken = initData.createPasswordResetToken(user);
            final var userEmail = user.getEmail();

            given(userService.loadUserByUsername(userEmail)).willReturn(user);
            given(passwordTokenRepository.save(any(PasswordResetToken.class))).willReturn(passwordResetToken);

            // When
            boolean actual = service.sendEmailWithResetPasswordInstructions(servletRequest, userEmail);

            // Then
            then(userService).should().loadUserByUsername(userEmail);
            then(passwordTokenRepository).should().save(any(PasswordResetToken.class));
            then(mailService).should().sendEmail(
                    eq(userEmail),
                    eq(PASSWORD_RESET_EMAIL_SUBJECT),
                    eq(PASSWORD_RESET_TEMPLATE_LOCATION),
                    anyMap()
            );
            assertThat(actual).as("Check result of tested method").isTrue();
        }

        @Test
        void returnsFalse_givenInvalidUserEmail() {
            // Given
            final var user = initData.createUser();
            final var userEmail = user.getEmail();
            given(userService.loadUserByUsername(userEmail))
                    .willThrow(new UsernameNotFoundException("User with email " + userEmail + " does not exist in database."));

            // When
            boolean actual = service.sendEmailWithResetPasswordInstructions(servletRequest, userEmail);

            // Then
            then(userService).should().loadUserByUsername(userEmail);
            assertThat(actual).as("Check result of tested method").isFalse();
        }
    }

    @Nested
    class ValidatePasswordResetToken {
        @Test
        void doesNothing_givenValidToken() {
            // Given
            final var user = initData.createUser();
            final var passwordResetToken = initData.createPasswordResetToken(user);
            final var token = passwordResetToken.getToken();
            given(passwordTokenRepository.findByToken(anyString())).willReturn(Optional.of(passwordResetToken));

            // When
            service.validatePasswordResetToken(token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenInvalidToken() {
            // Given
            final var token = "Invalid-token";
            given(passwordTokenRepository.findByToken(anyString())).willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> service.validatePasswordResetToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid token: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenRevokedToken() {
            // Given
            final var user = initData.createUser();
            final var passwordResetToken = initData.createPasswordResetToken(user);
            passwordResetToken.setRevoked();
            final var token = passwordResetToken.getToken();
            given(passwordTokenRepository.findByToken(anyString())).willReturn(Optional.of(passwordResetToken));

            // When
            assertThatThrownBy(() -> service.validatePasswordResetToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token expired or revoked: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenExpiredToken() {
            // Given
            final var user = initData.createUser();
            final var passwordResetToken = initData.createPasswordResetToken(user);
            final var token = passwordResetToken.getToken();

            given(passwordTokenRepository.findByToken(anyString())).willReturn(Optional.of(passwordResetToken));
            ReflectionTestUtils.setField(passwordResetToken, "expiryDate", LocalDateTime.now().minusMinutes(1));

            // When
            assertThatThrownBy(() -> service.validatePasswordResetToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token expired or revoked: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }
    }

    @Nested
    class ResetPassword {
        @Test
        void resetsPassword_andRevokesToken_givenValidParameters() {
            // Given
            final var user = initData.createUser();
            final var userEmail = user.getEmail();
            final var passwordResetToken = initData.createPasswordResetToken(user);
            final var token = passwordResetToken.getToken();
            final var newPassword = "newPassword";
            final var PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT = "Password Reset Confirmation";
            final var PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION = "mail/password-reset-confirmation.html";

            given(passwordTokenRepository.findByToken(anyString())).willReturn(Optional.of(passwordResetToken));

            // When
            service.resetPassword(servletRequest, token, newPassword);

            // Then
            then(passwordTokenRepository).should(times(2)).findByToken(token);
            then(userService).should().updateUserPassword(user, newPassword);
            then(passwordTokenRepository).should().save(passwordResetToken);
            then(mailService).should().sendEmail(
                    eq(userEmail),
                    eq(PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT),
                    eq(PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION),
                    anyMap()
            );
        }

        @Test
        void throwsInvalidTokenException_givenInvalidToken() {
            // Given
            final var token = "invalid-token";
            final var newPassword = "newPassword";
            given(passwordTokenRepository.findByToken(anyString())).willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> service.resetPassword(servletRequest, token, newPassword))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid token: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }
    }
}