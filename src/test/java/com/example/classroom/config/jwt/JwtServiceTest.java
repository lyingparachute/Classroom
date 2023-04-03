package com.example.classroom.config.jwt;

import com.example.classroom.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static com.example.classroom.config.jwt.JwtService.SECRET_KEY;
import static com.example.classroom.config.jwt.JwtService.TOKEN_EXPIRATION_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService service;

    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .email("user123")
                .password("testPassword")
                .build();
        token = service.generateToken(userDetails);
    }

    @Nested
    class extractUsername {
        @Test
        void returnsUsername_givenValidToken() {
            // Given
            String expectedUsername = "user123";

            // When
            String username = service.extractUsername(token);

            //Then
            assertThat(username).isEqualTo(expectedUsername);
        }

        @Test
        void throwsSignatureException_givenInvalidToken() {
            // Given
            String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIzIiwiaWF0IjoxNjE4NzkzNDIzLCJleHAiOjE2MTg4NzIyMjN9.-bde3Hq3yLQ40jK_oxsA6JiU6wWm2BdVgZ9cluuyfjQ";

            // When
            Throwable thrown = catchThrowable(() -> service.extractUsername(invalidToken));

            //Then
            assertThat(thrown)
                    .isExactlyInstanceOf(SignatureException.class)
                    .hasMessage("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
        }
    }

    @Nested
    class ExtractClaim {
        @Test
        void extractClaim() {

        }
    }

    @Nested
    class GenerateToken {
        @Test
        void generateToken() {

        }

        @Test
        void testGenerateToken() {
        }
    }

    @Nested
    class IsTokenValid {
        @Test
        void returnsTrue_givenValidToken_validUserDetails() {
            // When
            boolean result = service.isTokenValid(token, userDetails);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        void throwsExpiredJwtException_givenExpiredToken_validUserDetails() {
            // Given
            Date pastDate = new Date(System.currentTimeMillis() - TOKEN_EXPIRATION_TIME);
            String futureToken = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(pastDate)
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)), SignatureAlgorithm.HS256)
                    .compact();
            // When
            Throwable thrown = catchThrowable(() -> service.isTokenValid(futureToken, userDetails));

            // Then
            assertThat(thrown).isExactlyInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void returnsFalse_givenValidToken_invalidUserDetails() {
            // Given
            UserDetails invalidUserDetails = User.builder()
                    .email("wrongEmail")
                    .password("wrongPassword")
                    .build();

            // When
            boolean result = service.isTokenValid(token, invalidUserDetails);

            // Then
            assertThat(result).isFalse();
        }
    }

}