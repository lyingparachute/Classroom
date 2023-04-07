package com.example.classroom.auth.controller;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.AuthenticationResponse;
import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.test.util.IntegrationTestsInitData;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
        void returnsToken_givenValidRegisterRequest() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest();

            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/register"), request, AuthenticationResponse.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            AuthenticationResponse actual = response.getBody();
            assertThat(actual).isNotNull();
            assertThat(actual.getToken()).isNotNull().isNotEmpty();
            assertThat(userRepository.findByEmail(request.getEmail())).isPresent();
        }
    }

    @Nested
    class Authenticate {

        @Test
        void returnsToken_givenValidAuthenticationRequest() throws Exception {
            // Given
            AuthenticationRequest request = initData.createAuthenticationRequest();
            User user = initData.createUser();
            request.setEmail(user.getEmail());
            // When
            ResponseEntity<AuthenticationResponse> response = restTemplate
                    .postForEntity(createURL("/api/auth/authenticate"), request, AuthenticationResponse.class);

            // Then
            assertThat(userRepository.findByEmail(user.getEmail())).isPresent();
            assertThat(response.getStatusCode()).as("Check response status code").isEqualTo(HttpStatus.OK);
            AuthenticationResponse actual = response.getBody();
            assertThat(actual).isNotNull();
            assertThat(actual.getToken()).isNotNull().isNotEmpty();
        }
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}