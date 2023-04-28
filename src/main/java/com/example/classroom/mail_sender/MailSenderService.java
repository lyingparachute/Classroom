package com.example.classroom.mail_sender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendResetPasswordEmail(HttpServletRequest request,
                                       String token,
                                       String userEmail
    ) throws MessagingException {
        javaMailSender.send(constructResetTokenEmail(getAppUrl(request), token, userEmail));
    }

    private MimeMessage constructResetTokenEmail(
            String contextPath, String token, String userEmail) throws MessagingException {
        String resetLink = contextPath + "/password/change?token=" + token;
        String subject = "Reset Your Password";
        String body = "<html><body>" +
                "<p>Dear user,</p>" +
                "<p>We have received a request to reset the password for your account. If you did not request this reset, please disregard this email.</p>" +
                "<p>To reset your password, please click on the following link: <a href=\"" + resetLink + "\">Reset Password</a></p>" +
                "<p>This link will expire in 24 hours.</p>" +
                "<p>If you have any questions or concerns, please contact us at support@classroom.com/p>" +
                "<p>Best regards,<br>Classroom Team</p>" +
                "</body></html>";
        return constructEmail(MailDetails.builder()
                .recipientEmail(userEmail)
                .subject(subject)
                .msgBody(body)
                .build());
    }

    private MimeMessage constructEmail(MailDetails details) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject(details.subject());
        helper.setText(details.msgBody());
        helper.setTo(details.recipientEmail());
        helper.setFrom(sender);
        return message;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
