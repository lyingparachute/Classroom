package com.example.classroom.user.password;

import com.example.classroom.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@Entity
public class PasswordResetToken {
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

    public PasswordResetToken() {
        setExpiryDate();
    }

    public PasswordResetToken(User user, String token) {
        this.token = token;
        this.user = user;
        setExpiryDate();
    }

    private void setExpiryDate() {
        this.expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_TIME_IN_HOURS);
    }

    public boolean isTokenValid() {
        return !(isExpired() || revoked);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public void setRevoked() {
        this.revoked = true;
    }

    @Override
    public String toString() {
        return "Token [String=" + token + "]" + "[Expires" + expiryDate + "]";
    }
}
