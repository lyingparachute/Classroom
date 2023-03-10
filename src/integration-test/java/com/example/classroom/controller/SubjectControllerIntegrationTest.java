package com.example.classroom.controller;

import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("integration")
@SpringBootTest
class SubjectControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private IntegrationTestsInitData integrationTestsInitData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        integrationTestsInitData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

}