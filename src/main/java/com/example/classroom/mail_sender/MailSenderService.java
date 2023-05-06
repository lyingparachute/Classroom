package com.example.classroom.mail_sender;

import com.example.classroom.exception.EmailException;
import com.example.classroom.user.register.RegisterRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailSenderService {

    private static final String APP_NAME = "Classroom";
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    @Async
    public void sendEmail(String userEmail,
                          String subject,
                          String fileLocation,
                          Map<String, Object> templateModel) {
        String htmlBody = createEmailBody(fileLocation, templateModel);
        try {
            sendHtmlMessage(userEmail, subject, htmlBody);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Failed to send email.", e);
        }
    }

    private String createEmailBody(String templateLocation,
                                   Map<String, Object> templateModel) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        return thymeleafTemplateEngine.process(templateLocation, thymeleafContext);
    }

    private void sendHtmlMessage(String to,
                                 String subject,
                                 String htmlBody)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(sender, APP_NAME);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
    }

    public void sendRegistrationConfirmationEmail(HttpServletRequest request, RegisterRequest user) {
        sendEmail(user.getEmail(),
                "Welcome to Classroom",
                "mail/account-create-confirmation.html",
                ofEntries(
                        entry("firstName", user.getFirstName()),
                        entry("confirmLink", getConfirmationLink(request, user.getEmail())),
                        entry("websiteLink", getAppUrl(request))
                )
        );
    }

    private String getConfirmationLink(HttpServletRequest request, String userEmail) {
        return getAppUrl(request) + "/verify/" + userEmail;
    }

    public static String getAppUrl(final HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
