package com.example.classroom.fieldOfStudy;

import com.example.classroom.department.Department;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.student.Student;
import com.example.classroom.subject.Subject;
import com.example.classroom.test.util.IntegrationTestsInitData;
import com.example.classroom.user.UserRole;
import jakarta.transaction.Transactional;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration")
@SpringBootTest
@Transactional
@WithMockCustomUser
class FieldOfStudyGetControllerIntegrationTest {

    private static final String ENDPOINT_NAME = "fields-of-study";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private IntegrationTestsInitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Nested
    class GetFieldOfStudy {
        @Test
        void returns200_withFieldOfStudyView_andContent_givenAdminRole() throws Exception {
            // Given
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(
                    department, List.of(subject1, subject2), List.of(student1, student2));

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME + "/" + expected.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/fieldOfStudy-view"))
                    .andReturn();

            // Then
            assertThat(mvcResult.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                    .contains(
                            "<div class=\"page-title\">" + expected.getName() + "</div>"
                    ).contains(
                            "<h4>Field of study name:</h4>\n" +
                                    "        <span>" + expected.getName() + "</span>"
                    ).contains(
                            "<h4>Level of education:</h4>\n" +
                                    "        <span>" + expected.getLevelOfEducation().getValue() + "</span>"
                    ).contains(
                            "        <h4>Mode of studies:</h4>\n" +
                                    "        <span>" + expected.getMode().getValue() + "</span>"
                    ).contains(
                            "        <h4>Department:</h4>\n" +
                                    "        <a class=\"text-body\"\n" +
                                    "           href=\"/dashboard/departments/" + department.getId() + "\">\n" +
                                    "            <span>" + department.getName() + "</span>\n" +
                                    "            <i class=\"fas fa-regular fa-up-right-from-square\"></i>\n" +
                                    "        </a>"
                    ).contains(
                            "            <div class=\"mb-2\">\n" +
                                    "                <i class=\"fas fa-regular fa-check\"></i>\n" +
                                    "                <span>" + expected.getDescription() + "</span>\n" +
                                    "            </div>"
                    );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns200_OKStatus_givenStudentRole() throws Exception {
            // Given
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(
                    department, List.of(subject1, subject2), List.of(student1, student2));

            // When
            mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME + "/" + expected.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/fieldOfStudy-view"));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns200_OKStatus_givenTeacherRole() throws Exception {
            // Given
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(
                    department, List.of(subject1, subject2), List.of(student1, student2));

            // When
            mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME + "/" + expected.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/fieldOfStudy-view"));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void returns200_OKStatus_givenDeanRole() throws Exception {
            // Given
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(
                    department, List.of(subject1, subject2), List.of(student1, student2));

            // When
            mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME + "/" + expected.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/fieldOfStudy-view"));
        }
    }

    @Nested
    class GetAllFieldsOfStudy {
        @Test
        void returns200_withAllFieldsOfStudyView_andContent_givenAdminRole() throws Exception {
            // Given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());
            FieldOfStudy expected1 = initData.createFieldOfStudyOne(
                    department1, List.of(subject1, subject2), List.of(student1, student2));

            FieldOfStudy expected2 = initData.createFieldOfStudyTwo(
                    department2, List.of(subject3), List.of(student3));

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/all-fieldsOfStudy"))
                    .andReturn();

            // Then
            assertThat(mvcResult.getResponse().getContentAsString()).isNotNull().isNotEmpty()
                    .contains(
                            "                <div class=\"card-body\">\n" +
                                    "                    <h5 class=\"card-title\">" + expected1.getName() + "</h5>\n" +
                                    "                    <p class=\"card-text\">" + expected1.getDescription() + "</p>\n" +
                                    "                </div>"
                    )
                    .contains(
                            "                                    <a class=\"dropdown-item\"\n" +
                                    "                                       href=\"/dashboard/fields-of-study/" + expected1.getId() + "\">\n" +
                                    "                                                                    <span>"
                                    + expected1.getLevelOfEducation().getValue() + ", " + expected1.getMode().getValue().toLowerCase() + "</span>\n" +
                                    "                                        <i class=\"fas fa-regular fa-up-right-from-square\"></i>\n" +
                                    "                                    </a>"
                    )
                    .contains(
                            "                <div class=\"card-body\">\n" +
                                    "                    <h5 class=\"card-title\">" + expected2.getName() + "</h5>\n" +
                                    "                    <p class=\"card-text\">" + expected2.getDescription() + "</p>\n" +
                                    "                </div>"
                    ).contains(
                            "                                    <a class=\"dropdown-item\"\n" +
                                    "                                       href=\"/dashboard/fields-of-study/" + expected2.getId() + "\">\n" +
                                    "                                                                    <span>"
                                    + expected2.getLevelOfEducation().getValue() + ", " + expected2.getMode().getValue().toLowerCase() + "</span>\n" +
                                    "                                        <i class=\"fas fa-regular fa-up-right-from-square\"></i>\n" +
                                    "                                    </a>"
                    );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns200_OKStatus_givenStudentRole() throws Exception {
            // Given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());
            FieldOfStudy expected1 = initData.createFieldOfStudyOne(
                    department1, List.of(subject1, subject2), List.of(student1, student2));

            FieldOfStudy expected2 = initData.createFieldOfStudyTwo(
                    department2, List.of(subject3), List.of(student3));

            // When
            mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/all-fieldsOfStudy"));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns200_OKStatus_givenTeacherRole() throws Exception {
            // Given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());
            FieldOfStudy expected1 = initData.createFieldOfStudyOne(
                    department1, List.of(subject1, subject2), List.of(student1, student2));

            FieldOfStudy expected2 = initData.createFieldOfStudyTwo(
                    department2, List.of(subject3), List.of(student3));

            // When
            mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/all-fieldsOfStudy"));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void returns200_OKStatus_givenDeanRole() throws Exception {
            // Given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());
            FieldOfStudy expected1 = initData.createFieldOfStudyOne(
                    department1, List.of(subject1, subject2), List.of(student1, student2));

            FieldOfStudy expected2 = initData.createFieldOfStudyTwo(
                    department2, List.of(subject3), List.of(student3));

            // When
            mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/all-fieldsOfStudy"));
        }
    }

}
