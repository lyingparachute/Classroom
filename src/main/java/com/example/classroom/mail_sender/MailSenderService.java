package com.example.classroom.mail_sender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private static final String APP_NAME = "Classroom";
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendMessageUsingThymeleafTemplate(
            String userEmail, String subject, String fileLocation, Map<String, Object> templateModel)
            throws MessagingException, UnsupportedEncodingException {
        String htmlBody = getContentFromThymeleafTemplate(fileLocation, templateModel);
        sendHtmlMessage(userEmail, subject, htmlBody);
    }

    private String getContentFromThymeleafTemplate(String fileLocation,
                                                   Map<String, Object> templateModel) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        return thymeleafTemplateEngine.process(fileLocation, thymeleafContext);
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
}
