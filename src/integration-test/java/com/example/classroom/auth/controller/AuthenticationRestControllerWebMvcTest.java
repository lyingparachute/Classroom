package com.example.classroom.auth.controller;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.AuthenticationResponse;
import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.auth.service.AuthenticationService;
import com.example.classroom.config.jwt.JwtAuthenticationFilter;
import com.example.classroom.test.util.UnitTestsInitData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationRestController.class)
class AuthenticationRestControllerWebMvcTest {

    @MockBean
    AuthenticationService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    UnitTestsInitData initData = new UnitTestsInitData();

    @Nested
    class Register {

        @Test
        void returnsToken_givenValidRegisterRequest() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest();
            AuthenticationResponse expectedResponse = new AuthenticationResponse("Bearer token");
            // When
            when(service.register(request)).thenReturn(expectedResponse);
            String content = mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Then
            assertThat(content).as("Check for presence of request's response content").isNotNull().isNotEmpty();
            AuthenticationResponse response = objectMapper.readValue(content, AuthenticationResponse.class);
            String token = response.getToken();

        }
    }

    @Nested
    class Authenticate {

        @Test
        void returnsToken_givenValidAuthenticationRequest() throws Exception {
            // Given
            AuthenticationRequest request = initData.createAuthenticationRequest();
            AuthenticationResponse expectedResponse = new AuthenticationResponse("Bearer token");
            // When
            when(service.authenticate(request)).thenReturn(expectedResponse);
            String content = mockMvc.perform(post("/api/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Then
            assertThat(content).as("Check for presence of request's response content").isNotNull().isNotEmpty();
            AuthenticationResponse response = objectMapper.readValue(content, AuthenticationResponse.class);
            String token = response.getToken();
        }
    }
}