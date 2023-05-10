package com.example.classroom.user.register;

import com.example.classroom.exception.InvalidTokenException;
import com.example.classroom.mail_sender.MailSenderService;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.classroom.mail_sender.MailSenderService.getAppUrl;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MailSenderService mailService;
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    void sendAccountVerificationEmail(final HttpServletRequest request, final User user) {
        revokeAllVerificationTokensForUser(user.getId());
        final String verificationToken = createAndSaveEmailVerificationToken(user);
        mailService.sendEmail(user.getEmail(),
                "Welcome to Classroom!",
                "mail/account-create-confirmation.html",
                ofEntries(
                        entry("firstName", user.getFirstName()),
                        entry("confirmLink", getRegistrationConfirmationLink(request,
                                verificationToken)),
                        entry("websiteLink", getAppUrl(request))
                )
        );
    }

    void validateVerificationToken(final String token) throws InvalidTokenException {
        final VerificationToken verificationToken = getVerificationToken(token);
        if (!verificationToken.isValid())
            throw new InvalidTokenException("Verification Token expired or revoked: " + token);
    }

    @Transactional
    void verifyAccount(final String token) {
        User user = getUserByVerificationToken(token);
        user.enableAccount();
        userRepository.save(user);
        revokeVerificationToken(token);
    }

    private void revokeAllVerificationTokensForUser(Long id) {
        tokenRepository.findAllValidTokenByUserId(id)
                .forEach(VerificationToken::setRevoked);
    }

    @Transactional
    private void revokeVerificationToken(final String token) {
        final VerificationToken verificationToken = getVerificationToken(token);
        verificationToken.setRevoked();
        tokenRepository.save(verificationToken);
    }

    private User getUserByVerificationToken(final String token) {
        return getVerificationToken(token).getUser();
    }

    private String getRegistrationConfirmationLink(final HttpServletRequest request, final String token) {
        return getAppUrl(request) + "/account/verify?token=" + token;
    }

    private VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid Verification Token: " + token));
    }

    private String createAndSaveEmailVerificationToken(final User user) {
        final VerificationToken myToken = new VerificationToken(
                user,
                UUID.randomUUID().toString()
        );
        return tokenRepository.save(myToken).getToken();
    }
}
