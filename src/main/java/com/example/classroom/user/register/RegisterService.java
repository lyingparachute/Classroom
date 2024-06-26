package com.example.classroom.user.register;

import com.example.classroom.exception.AccountAlreadyVerifiedException;
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
        if (user.isEnabled())
            throw new AccountAlreadyVerifiedException("Account with email '" + user.getEmail() + "' is already verified.");
        revokeAllVerificationTokensForUser(user.getId());
        final var verificationToken = createAndSaveEmailVerificationToken(user);
        mailService.sendEmail(user.getEmail(),
                "Welcome to Classroom! Verify your email address.",
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
        final var verificationToken = getVerificationToken(token);
        if (!verificationToken.isValid())
            throw new InvalidTokenException("Verification Token expired or revoked: " + token);
    }

    @Transactional
    void verifyAccount(final String token) {
        final var user = getUserByVerificationToken(token);
        user.enableAccount();
        userRepository.save(user);
        revokeVerificationToken(token);
    }

    private void revokeAllVerificationTokensForUser(final Long id) {
        tokenRepository.findAllValidTokenByUserId(id)
                .forEach(VerificationToken::setRevoked);
    }

    @Transactional
    protected void revokeVerificationToken(final String token) {
        final var verificationToken = getVerificationToken(token);
        verificationToken.setRevoked();
        tokenRepository.save(verificationToken);
    }

    private User getUserByVerificationToken(final String token) {
        return getVerificationToken(token).getUser();
    }

    private String getRegistrationConfirmationLink(final HttpServletRequest request, final String token) {
        return getAppUrl(request) + "/account/verify?token=" + token;
    }

    private VerificationToken getVerificationToken(final String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid Verification Token: " + token));
    }

    private String createAndSaveEmailVerificationToken(final User user) {
        final var myToken = new VerificationToken(
                user,
                UUID.randomUUID().toString()
        );
        return tokenRepository.save(myToken).getToken();
    }
}
