package com.example.classroom.user.register;

import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @InjectMocks
    RegisterService service;
    @Mock
    MailSenderService mailService;
    @Mock
    VerificationTokenRepository tokenRepository;
    @Mock
    UserRepository userRepository;

    @Test
    void sendAccountVerificationEmail() {
    }

    @Test
    void validateVerificationToken() {
    }

    @Test
    void verifyAccount() {
    }
}