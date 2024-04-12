package com.example.classroom.user.register;

import com.example.classroom.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@Entity
public class VerificationToken {
    private static final int TOKEN_EXPIRATION_TIME_IN_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    private boolean expired;

    private boolean revoked;

    public VerificationToken() {
        setExpiryDate();
    }

    VerificationToken(final User user, final String token) {
        this.token = token;
        this.user = user;
        setExpiryDate();
    }

    private void setExpiryDate() {
        expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_TIME_IN_HOURS);
    }

    boolean isValid() {
        return !(isExpired() || revoked);
    }

    private boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    void setRevoked() {
        revoked = true;
    }

    @Override
    public String toString() {
        return "Token [String=" + token + "]" + "[Expires" + expiryDate + "]";
    }
}
