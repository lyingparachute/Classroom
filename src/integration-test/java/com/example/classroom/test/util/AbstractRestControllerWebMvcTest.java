package com.example.classroom.test.util;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public abstract class AbstractRestControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }
}
