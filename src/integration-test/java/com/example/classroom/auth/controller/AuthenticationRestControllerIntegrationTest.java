package com.example.classroom.auth.controller;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.AuthenticationResponse;
import com.example.classroom.test.util.IntegrationTestsInitData;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import com.example.classroom.user.password.PasswordRequest;
import com.example.classroom.user.register.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationRestControllerIntegrationTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IntegrationTestsInitData initData;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Nested
    class Register {

        @Test
        void returnsStatusCode200_withToken_givenValidRegisterRequest() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest(UserRole.ROLE_STUDENT);

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/register"), request, AuthenticationResponse.class);

            // Then
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.OK);
            assertThat(userRepository.findByEmail(request.email())).isPresent();
            AuthenticationResponse actual = response.getBody();
            assertThat(actual).isNotNull();
            assertThat(actual.token()).isNotNull().isNotEmpty();
        }

        @Test
        void returnsStatusCode400_givenInvalidEmailFormat() throws Exception {
            // Given
            final var request = RegisterRequest.builder()
                .firstName("Andrzej")
                .lastName("Nowak")
                .email("invalid email")
                .passwordRequest(
                    new PasswordRequest("123", "123")
                )
                .role(UserRole.ROLE_STUDENT)
                .build();

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/register"), request, AuthenticationResponse.class);

            // Then
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(userRepository.findByEmail(request.email())).isNotPresent();
        }

        @Disabled("Disabled because @ValidPassword annotation is commented out for demonstration purposes")
        @Test
        void returnsStatusCode400_givenInvalidPasswordFormat() throws Exception {
            // Given
            final var request = RegisterRequest.builder()
                .firstName("Andrzej")
                .lastName("Nowak")
                .email("invalid email")
                .passwordRequest(
                    new PasswordRequest("invalid password", "123")
                )
                .role(UserRole.ROLE_STUDENT)
                .build();

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/register"), request, AuthenticationResponse.class);

            // Then
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(userRepository.findByEmail(request.email())).isNotPresent();
        }
    }

    @Nested
    class Authenticate {

        @Test
        void returnsStatusCode200_withToken_givenAuthRequest_withCorrectCredentials() throws Exception {
            // Given
            final var user = initData.createUser();
            final var request = new AuthenticationRequest(user.getEmail(), "encodedPassword");

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/authenticate"), request, AuthenticationResponse.class);

            // Then
            assertThat(userRepository.findByEmail(user.getEmail())).isPresent();
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.OK);
            AuthenticationResponse actual = response.getBody();
            assertThat(actual).isNotNull();
            assertThat(actual.token()).isNotNull().isNotEmpty();
        }

        @Test
        void returnsStatusCode302_givenAuthRequest_whenUserDoesNotExist() throws Exception {
            // Given
            AuthenticationRequest request = initData.createAuthenticationRequest();

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/authenticate"), request, AuthenticationResponse.class);

            // Then
            assertThat(userRepository.findByEmail(request.email())).isNotPresent();
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.FOUND);
        }

        @Test
        void returnsStatusCode302_givenAuthRequest_withWrongEmail() throws Exception {
            // Given
            final var user = initData.createUser();
            final var request = new AuthenticationRequest("wrong.email@gmail.com", "encodedPassword");

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/authenticate"), request, AuthenticationResponse.class);

            // Then
            assertThat(userRepository.findByEmail(user.getEmail())).isPresent();
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.FOUND);
        }

        @Test
        void returnsStatusCode302_givenAuthRequest_withWrongPassword() throws Exception {
            // Given
            User user = initData.createUser();
            final var request = new AuthenticationRequest(user.getEmail(), "wrongPassword");

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/authenticate"), request, AuthenticationResponse.class);

            // Then
            assertThat(userRepository.findByEmail(user.getEmail())).isPresent();
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.FOUND);
        }
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}