package com.example.classroom.mailSender;

import lombok.Builder;

@Builder
public record MailDetails(
        String subject,
        String msgBody,
        String recipientEmail) {
}
