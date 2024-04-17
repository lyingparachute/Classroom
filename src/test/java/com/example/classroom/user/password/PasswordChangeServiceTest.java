package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.test.util.UnitTestsInitData;
import com.example.classroom.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.BDDMockito.anyMap;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordChangeServiceTest {

    @InjectMocks
    PasswordChangeService service;
    @Mock
    MailSenderService mailService;
    @Mock
    UserManagementService userService;
    @Spy
    UnitTestsInitData initData;
    @Spy
    MockHttpServletRequest servletRequest;

    @Test
    void changesUserPassword_givenValidPasswordChangeRequest_andUserEmail() {
        // Given
        User user = initData.createUser(null);
        String userEmail = user.getEmail();
        String newPassword = "newPassword";
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(
                "oldPassword",
                new PasswordRequest(newPassword, newPassword)
        );

        // When
        when(userService.loadUserByUsername(userEmail)).thenReturn(user);
        service.changeUserPassword(passwordChangeRequest, userEmail);

        // Then
        then(userService).should().loadUserByUsername(userEmail);
        then(userService).should().validateOldInputPassword(anyString(), anyString());
        then(userService).should().updateUserPassword(user, newPassword);
    }

    @Test
    void sendsPasswordChangeConfirmationEmail_givenValidHttpServletRequest_andUserEmail() {
        // Given
        User user = initData.createUser(null);
        String userEmail = user.getEmail();
        String PASSWORD_CHANGE_CONFIRM_TEMPLATE_LOCATION = "mail/password-change-confirmation.html";
        String PASSWORD_CHANGE_CONFIRM_EMAIL_SUBJECT = "Password Changed Successfully!";

        // When
        when(userService.loadUserByUsername(userEmail)).thenReturn(user);
        service.sendPasswordChangeConfirmationEmail(servletRequest, userEmail);

        // Then
        then(userService).should().loadUserByUsername(userEmail);
        then(mailService).should().sendEmail(
                eq(userEmail),
                eq(PASSWORD_CHANGE_CONFIRM_EMAIL_SUBJECT),
                eq(PASSWORD_CHANGE_CONFIRM_TEMPLATE_LOCATION),
                anyMap()
        );
    }
}