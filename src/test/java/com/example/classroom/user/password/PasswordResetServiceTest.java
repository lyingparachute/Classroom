package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.test.util.UnitTestsInitData;
import com.example.classroom.user.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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
            String PASSWORD_RESET_TEMPLATE_LOCATION = "mail/password-reset.html";
            String PASSWORD_RESET_EMAIL_SUBJECT = "Password Reset";

            User user = initData.createUser();
            PasswordResetToken passwordResetToken = initData.createPasswordResetToken(user);
            String userEmail = user.getEmail();

            // When
            when(userService.loadUserByUsername(userEmail)).thenReturn(user);
            when(passwordTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
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
            User user = initData.createUser();
            String userEmail = user.getEmail();

            // When
            doThrow(new UsernameNotFoundException("User with email " + userEmail + " does not exist in database."))
                    .when(userService).loadUserByUsername(userEmail);

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
            User user = initData.createUser();
            PasswordResetToken passwordResetToken = initData.createPasswordResetToken(user);
            String token = passwordResetToken.getToken();

            // When
            when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));

            service.validatePasswordResetToken(token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenInvalidToken() {
            // Given
            String token = "Invalid-token";

            // When
            when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.validatePasswordResetToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid token: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenRevokedToken() {
            // Given
            User user = initData.createUser();
            PasswordResetToken passwordResetToken = initData.createPasswordResetToken(user);
            passwordResetToken.setRevoked();
            String token = passwordResetToken.getToken();

            // When
            when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));

            assertThatThrownBy(() -> service.validatePasswordResetToken(token))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token expired or revoked: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }

        @Test
        void throwsInvalidTokenException_givenExpiredToken() {
            // Given
            User user = initData.createUser();
            PasswordResetToken passwordResetToken = initData.createPasswordResetToken(user);
            String token = passwordResetToken.getToken();

            // When
            when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
//            try(MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
//                doReturn(LocalDateTime.of(2022,01,01,22,22,22))
//                        .when(mock);
//                // Put the execution of the test inside of the try, otherwise it won't work
//            }
            when(passwordResetToken.isExpired()).thenReturn(true);
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
            User user = initData.createUser();
            String userEmail = user.getEmail();
            PasswordResetToken passwordResetToken = initData.createPasswordResetToken(user);
            String token = passwordResetToken.getToken();
            String newPassword = "newPassword";
            String PASSWORD_RESET_CONFIRM_EMAIL_SUBJECT = "Password Reset Confirmation";
            String PASSWORD_RESET_CONFIRM_TEMPLATE_LOCATION = "mail/password-reset-confirmation.html";

            // When
            when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));

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
            String token = "invalid-token";
            String newPassword = "newPassword";

            // When
            when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.resetPassword(servletRequest, token, newPassword))
                    .isExactlyInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid token: " + token);

            // Then
            then(passwordTokenRepository).should().findByToken(token);
        }
    }
}