package com.example.classroom.mail_sender;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @InjectMocks
    MailSenderService service;
    @Mock
    JavaMailSender javaMailSender;
    @Spy
    SpringTemplateEngine thymeleafTemplateEngine;

    @Spy
    MockHttpServletRequest servletRequest;

    @Nested
    class SendEmail {
        @Test
        void createsAndSendsEmail_givenParameters() {
            //Given
            String userEmail = "user@email.com";
            String subject = "Email Subject";
            String templateLocation = "/location/template.html";
            Map<String, Object> templateModel = Map.ofEntries(
                    Map.entry("email", userEmail)
            );
            String emailBody = "Email body";
            ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            // When
//            when(thymeleafTemplateEngine.process(anyString(), any())).thenReturn(emailBody);
//            when(javaMailSender.createMimeMessage()).thenReturn(MimeMessage.)
            service.sendEmail(userEmail, subject, templateLocation, templateModel);

            // Then

            verify(javaMailSender).send(emailCaptor.capture());

        }
    }

    @Nested
    class GetAppUrl {
        @Test
        void returnsAppUrlAsString_givenHttpServletRequest() {
            // Given
            String expected = "http://localhost:80";

            // When
            String appUrl = MailSenderService.getAppUrl(servletRequest);

            // Then
            assertThat(appUrl).isNotNull().isNotEmpty().isEqualTo(expected);

        }
    }

    @Nested
    class GetSignInLink {
        @Test
        void returnsSignInEndpoint_givenBaseAppUrlAsString() {
            // Given
            String appUrl = "http://localhost:80";
            String expected = "http://localhost:80/sign-in";

            // When
            String signInLink = MailSenderService.getSignInLink(appUrl);

            // Then
            assertThat(signInLink).isNotNull().isNotEmpty().isEqualTo(expected);
        }
    }
}