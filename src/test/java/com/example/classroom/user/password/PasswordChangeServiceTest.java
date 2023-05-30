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

import static org.mockito.BDDMockito.*;

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

    @Test
    void changesUserPassword_givenValidPasswordChangeRequest_andUserEmail() {
        // Given
        User user = initData.createUser();
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
        then(userService).should().validateOldPassword(anyString(), anyString());
        then(userService).should().updateUserPassword(user, newPassword);
    }

    @Test
    void sendsPasswordChangeConfirmationEmail_givenValidHttpServletRequest_andUserEmail() {
        // Given

        // When
//        service.sendPasswordChangeConfirmationEmail();

        // Then

    }
}