package com.example.classroom.user.password;

import com.example.classroom.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class PasswordResetToken {
    private static final int EXPIRATION_HOURS = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDate expiryDate;

    private boolean expired;

    private boolean revoked;

    public boolean isTokenValid() {
        return !(isExpired() || revoked);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(LocalDate.now().plusDays(1));
    }

    @Override
    public String toString() {
        return "Token [String=" + token + "]" + "[Expires" + expiryDate + "]";
    }
}
