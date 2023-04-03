package com.example.classroom.auth.service;

import com.example.classroom.token.Token;
import com.example.classroom.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @InjectMocks
    LogoutService service;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    HttpServletRequest request = mock(HttpServletRequest.class);

    @Mock
    HttpServletResponse response = mock(HttpServletResponse.class);

    @Mock
    Authentication authentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    class Logout {

        @Test
        void makesTokenInvalid_givenProperRequest() {
            // Given
            String jwt = "jwt-token";
            Token storedToken = Token.builder()
                    .token(jwt)
                    .expired(false)
                    .revoked(false)
                    .build();

            // When
            when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
            when(tokenRepository.findByToken(jwt)).thenReturn(Optional.of(storedToken));
            service.logout(request, response, authentication);

            // Then
            verify(tokenRepository).findByToken(anyString());
            verify(tokenRepository).save(storedToken);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        void doesNothing_whenRequestHeader_isNull() {
            // When
            when(request.getHeader("Authorization")).thenReturn(null);
            service.logout(request, response, authentication);

            // Then
            verifyNoInteractions(tokenRepository);
            verifyNoInteractions(tokenRepository);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        }

        @Test
        void doesNothing_whenRequestHeader_doesNotStartWithBearer() {
            // Given
            String jwt = "jwt-token";

            // When
            when(request.getHeader("Authorization")).thenReturn(jwt);
            service.logout(request, response, authentication);

            // Then
            verifyNoInteractions(tokenRepository);
            verifyNoInteractions(tokenRepository);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        }

        @Test
        void doesNothing_whenToken_isNull() {
            // Given
            String jwt = "jwt-token";

            // When
            when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
            when(tokenRepository.findByToken(jwt)).thenReturn(Optional.empty());
            service.logout(request, response, authentication);

            // Then
            verify(tokenRepository).findByToken(jwt);
            verifyNoMoreInteractions(tokenRepository);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        }
    }
}