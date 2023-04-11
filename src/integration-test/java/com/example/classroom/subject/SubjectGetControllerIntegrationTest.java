package com.example.classroom.subject;

import com.example.classroom.fieldOfStudy.FieldOfStudy;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.teacher.TeacherRepository;
import com.example.classroom.test.util.IntegrationTestsInitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration")
@SpringBootTest
@WithMockCustomUser
class SubjectGetControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private IntegrationTestsInitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Nested
    class GetSubject {
        @Test
        void returns200_withSubjectView() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/subjects/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-view"))
                    .andReturn();

            // Then
            String contentAsString = mvcResult.getResponse().getContentAsString();
        }
    }

    @Nested
    class GetSubjects {
        @Test
        void returns200_withAllSubjectsView() throws Exception {
            mockMvc.perform(get("/dashboard/subjects"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/all-subjects"));
        }
    }

    @Nested
    class CreateSubject {
        @Test
        void returns200_withAddNewSubjectView() throws Exception {
            mockMvc.perform(get("/dashboard/subjects/new"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-create-form"));
        }
    }

    @Nested
    class EditSubject {
        @Test
        void returns200_withEditSubjectView() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/subjects/edit/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-edit-form"))
                    .andReturn();

            // Then
            String contentAsString = mvcResult.getResponse().getContentAsString();
        }
    }
}