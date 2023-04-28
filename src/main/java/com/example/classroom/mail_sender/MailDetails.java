package com.example.classroom.mail_sender;

import lombok.Builder;

@Builder
public record MailDetails(
        String subject,
        String msgBody,
        String recipientEmail
) {
}
