package com.example.classroom.mail_sender;

import com.example.classroom.exception.EmailException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;

import java.util.Map;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static com.example.classroom.mail_sender.MailSenderService.getSignInLink;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @InjectMocks
    MailSenderService service;
    @Mock
    JavaMailSender javaMailSender;
    @Mock
    MimeMessage mimeMessage;
    @Spy
    ITemplateEngine thymeleafTemplateEngine;
    @Spy
    MockHttpServletRequest servletRequest;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(service, "sender", "sender@example.com");
    }

    @Nested
    class SendEmail {
        @Test
        void createsAndSendsEmail_givenParameters() {
            //Given
            String userEmail = "test@example.com";
            String subject = "Test Subject";
            String templateLocation = "/location/template.html";
            Map<String, Object> templateModel = Map.ofEntries(
                    Map.entry("key", "value")
            );
            String htmlBody = "Test HTML Body";

            // When
            when(thymeleafTemplateEngine.process(anyString(), any())).thenReturn(htmlBody);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            service.sendEmail(userEmail, subject, templateLocation, templateModel);

            // Then
            then(javaMailSender).should().send(mimeMessage);
        }

        @Test
        void throwsEmailException_whenMessagingExceptionOccurs() {
            //Given
            String userEmail = "test@example.com";
            String subject = "Test Subject";
            String templateLocation = "/location/template.html";
            Map<String, Object> templateModel = Map.ofEntries(
                    Map.entry("key", "value")
            );
            String htmlBody = "Test HTML Body";

            // When
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(thymeleafTemplateEngine.process(anyString(), any())).thenReturn(htmlBody);
            doThrow(new EmailException("Failed to send email."))
                    .when(javaMailSender)
                    .send(mimeMessage);

            // Then
            assertThatThrownBy(() -> {
                service.sendEmail(userEmail, subject, templateLocation, templateModel);
            })
                    .isExactlyInstanceOf(EmailException.class)
                    .hasMessageContaining("Failed to send email.");
        }
    }

    @Nested
    class GetAppUrl {
        @Test
        void returnsAppUrlAsString_givenHttpServletRequest() {
            // Given
            String expected = "http://localhost:80";

            // When
            String appUrl = getAppUrl(servletRequest);

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
            String signInLink = getSignInLink(appUrl);

            // Then
            assertThat(signInLink).isNotNull().isNotEmpty().isEqualTo(expected);
        }
    }
}