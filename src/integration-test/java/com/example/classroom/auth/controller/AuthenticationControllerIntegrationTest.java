package com.example.classroom.auth.controller;

import com.example.classroom.test.util.IntegrationTestsInitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration")
@SpringBootTest
class AuthenticationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IntegrationTestsInitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Nested
    class SignIn {
        @Test
        void getsSignInPageView() throws Exception {
            mockMvc.perform(get("/sign-in"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("auth/sign-in"));
        }
    }

    @Nested
    class SignUp {
        @Test
        void getsSignUpPageView() throws Exception {
            mockMvc.perform(get("/sign-up"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("auth/sign-up"));
        }

        @Test
        void createsNewUserAccount_givenValidRegisterRequest() {

        }
    }

    @Nested
    class ResetPassword {
        @Test
        void resetPassword() {

        }
    }
}