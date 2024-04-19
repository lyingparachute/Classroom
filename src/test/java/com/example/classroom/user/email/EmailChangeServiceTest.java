package com.example.classroom.user.email;

import com.example.classroom.mail_sender.MailSenderService;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailChangeServiceTest {

    @Mock
    MailSenderService mailSenderService;

    @InjectMocks
    EmailChangeService underTest;

    @Test
    void sendEmailVerificationCode_shouldSendEmailVerificationCode() {
        //given
        final var email = "test@example.com";
        final var firstName = "Tester";

        //when
        final var verificationCode = underTest.sendEmailVerificationCode(email, firstName);

        //then
        verify(mailSenderService).sendEmail(eq(email), anyString(), anyString(), any());
        assertThat(verificationCode).isNotBlank().hasSize(6);
        assertThat(NumberUtils.isCreatable(verificationCode)).isTrue(); // Check if String is a number
    }
}