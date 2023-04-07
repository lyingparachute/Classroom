package com.example.classroom.test.util;

import com.example.classroom.auth.model.RegisterRequest;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class SignUpUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SignUpUtils() {
    }

    public static String getTokenForLogin(String username, String password, MockMvc mockMvc) throws Exception {
        String content = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + password + "\", \"email\": \"" + username + "\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationResponse authResponse = OBJECT_MAPPER.readValue(content, AuthenticationResponse.class);

        return authResponse.getToken();
    }

    public static String getTokenForRegister(RegisterRequest request, MockMvc mockMvc) throws Exception {
        byte[] content = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": \"" + request.getFirstName() + "\",  " +
                                "\"lastName\": \"" + request.getLastName() + "\"," +
                                "\"email\": \"" + request.getEmail() + "\", " +
                                "\"password\": \"" + request.getPassword() +
                                "\"}"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        AuthenticationResponse authResponse = OBJECT_MAPPER.readValue(content, AuthenticationResponse.class);

        return authResponse.getToken();
    }

    private static class AuthenticationResponse {

        @JsonAlias("id_token")
        private String token;

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}
