package com.example.classroom.auth.service;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.AuthenticationResponse;
import com.example.classroom.test.util.UnitTestsInitData;
import com.example.classroom.token.JwtService;
import com.example.classroom.token.Token;
import com.example.classroom.token.TokenRepository;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import com.example.classroom.user.register.RegisterRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    AuthenticationService service;

    @Mock
    UserRepository repository;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    AuthenticationManager authenticationManager;

    @Spy
    UnitTestsInitData initData;

    // ModelMapper object is used in service classes, therefore it needs to be initialized (SPY) here
    @Spy
    ModelMapper mapper;


    @Nested
    class Register {
        @Test
        void returnsAuthenticationResponse_withToken_givenValidRegisterRequest() {
            // Given
            final var role = UserRole.ROLE_STUDENT;
            User user = initData.createUser(role);
            RegisterRequest registerRequest = initData.createRegisterRequest(role);
            String token = "generatedToken";
            String encodedPassword = "encodedPassword";

            // When
            when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
            when(repository.save(any(User.class))).thenReturn(user);
            when(jwtService.generateToken(user)).thenReturn(token);

            AuthenticationResponse response = service.register(registerRequest);

            // Then
            verify(repository).save(any(User.class));
            verify(jwtService).generateToken(user);
            assertThat(response).isNotNull();
            assertThat(response.token()).isNotNull().isNotEmpty().isEqualTo(token);
        }
    }

    @Nested
    class Authenticate {
        @Test
        void returnsAuthenticationResponse_givenValidAuthenticationRequest() {
            // Given
            AuthenticationRequest authRequest = initData.createAuthenticationRequest();
            User user = initData.createUser(null);
            String token = "generatedToken";

            // When
            when(repository.findByEmail(authRequest.email())).thenReturn(Optional.of(user));
            when(jwtService.generateToken(user)).thenReturn(token);
            when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(List.of(new Token()));

            AuthenticationResponse response = service.authenticate(authRequest);

            // Then
            verify(repository).findByEmail(authRequest.email());
            verify(jwtService).generateToken(user);
            verify(tokenRepository).save(any(Token.class));
            verify(tokenRepository).saveAll(anyList());
            assertThat(response).isNotNull();
            assertThat(response.token()).isNotNull().isNotEmpty().isEqualTo(token);
        }

        @Test
        void returnsUsernameNotFoundException_givenNonExistingEmail() {
            // Given
            AuthenticationRequest authRequest = initData.createAuthenticationRequest();

            // When
            when(repository.findByEmail(authRequest.email())).thenReturn(Optional.empty());

            Throwable thrown = catchThrowable(() -> service.authenticate(authRequest));

            // Then
            verify(repository).findByEmail(authRequest.email());
            assertThat(thrown)
                    .isExactlyInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("User with email andrzej.nowak@gmail.com does not exist in database.");
        }

        @Test
        void returnsUsernameNotFoundException_givenWrongCredentials() {
            // Given
            AuthenticationRequest authRequest = initData.createAuthenticationRequest();
            User user = User.builder()
                .id(1L)
                .firstName("Andrzej")
                .lastName("Nowak")
                .password("wrongPassword")
                .email("andrzej.nowak@gmail.com")
                .enabled(true)
                .build();
            String exceptionMsg = "Invalid credentials.";

            // When
            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new BadCredentialsException(exceptionMsg));

            Throwable thrown = catchThrowable(() -> service.authenticate(authRequest));

            // Then
            assertThat(thrown)
                    .isExactlyInstanceOf(BadCredentialsException.class)
                    .hasMessage(exceptionMsg);
        }
    }
}