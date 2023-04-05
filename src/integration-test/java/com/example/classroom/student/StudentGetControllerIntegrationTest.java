package com.example.classroom.student;

import com.example.classroom.repository.util.IntegrationTestsInitData;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.teacher.Teacher;
import org.junit.jupiter.api.BeforeEach;
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
//@Transactional
@WithMockCustomUser
class StudentGetControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IntegrationTestsInitData integrationTestsInitData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        integrationTestsInitData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldGetStudentView() throws Exception {
        Student student = integrationTestsInitData.createStudentOne(null, List.of());

        this.mockMvc.perform(get("/dashboard/students/" + student.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("student/student-view"));
    }

    @Test
    void shouldGetParticularStudent() throws Exception {
        // Given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Student student = integrationTestsInitData.createStudentOne(null, List.of(teacher1, teacher2));

        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/students/" + student.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("<div class=\"page-title\">Viewing Student with ID: " + student.getId() + "</div>")
                .contains("    <div class=\"mb-4\">\n" +
                        "        <h4>First Name:</h4>\n" +
                        "        <span>" + student.getFirstName() + "</span>\n" +
                        "    </div>\n" +
                        "    <hr>\n" +
                        "    <div class=\"mb-4\">\n" +
                        "        <h4>Last Name:</h4>\n" +
                        "        <span>" + student.getLastName() + "</span>\n" +
                        "    </div>\n" +
                        "    <hr>\n" +
                        "    <div class=\"mb-4\">\n" +
                        "        <h4>Email:</h4>\n" +
                        "        <span>" + student.getEmail() + "</span>\n" +
                        "    </div>\n" +
                        "    <hr>\n" +
                        "    <div class=\"mb-4\">\n" +
                        "        <h4>Age:</h4>\n" +
                        "        <span>" + student.getAge() + "</span>\n" +
                        "    </div>"
                );
        //TODO - chech if student has teachers assigned
//        assertThat(contentAsString)
//                .contains("                                   href=\"/teachers/" + teacher1.getId() + "\"\n" +
//                        "                                   value=\"" + teacher1.getId() + "\">" + teacher1.getFirstName() + " " + teacher1.getLastName());
//        assertThat(contentAsString)
//                .contains("                                   href=\"/teachers/" + teacher2.getId() + "\"\n" +
//                        "                                   value=\"" + teacher2.getId() + "\">" + teacher2.getFirstName() + " " + teacher2.getLastName());
    }

    @Test
    void shouldGetStudentsView() throws Exception {
        this.mockMvc.perform(get("/dashboard/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("student/all-students"));
    }

    @Test
    void shouldGetStudentsAndContainParticularStudents() throws Exception {
        // Given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = integrationTestsInitData.createStudentOne(null, List.of(teacher1, teacher2));
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of(teacher1));

        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student1.getId() + "\">"
                        + student1.getFirstName() + " " + student1.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student1.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student1.getAge() + "</span>\n")
                .contains("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student2.getId() + "\">"
                        + student2.getFirstName() + " " + student2.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student2.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student2.getAge() + "</span>\n");
    }

    @Test
    void shouldGetStudentsSecondPageSortedByFirstName() throws Exception {
        // Given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = integrationTestsInitData.createStudentOne(null, List.of(teacher1, teacher2));
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of(teacher1));
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of(teacher1, teacher2));
        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/students?page=2&size=2&sortField=firstName&sortDir=asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // used sorting dir by lastName ascending, so student2 will be last - Weronika
        assertThat(contentAsString)
                .doesNotContain("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student1.getId() + "\">"
                        + student1.getFirstName() + " " + student1.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student1.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student1.getAge() + "</span>\n")
                .contains("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student2.getId() + "\">"
                        + student2.getFirstName() + " " + student2.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student2.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student2.getAge() + "</span>\n")
                .doesNotContain("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student3.getId() + "\">"
                        + student3.getFirstName() + " " + student3.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student3.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student3.getAge() + "</span>\n");

    }

    @Test
    void shouldGetStudentsSearchView() throws Exception {
        this.mockMvc.perform(get("/dashboard/students?name=w"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("student/all-students"));
    }

    @Test
    void shouldGetResultOfSearchStudentsByFirstOrLastName() throws Exception {
        // Given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = integrationTestsInitData.createStudentOne(null, List.of(teacher1, teacher2));
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of(teacher1));
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of(teacher1, teacher2));
        // When
        // Searching for letter 'w' in first name or last name
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/students?name=w"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .doesNotContainIgnoringCase("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student1.getId() + "\">"
                        + student1.getFirstName() + " " + student1.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student1.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student1.getAge() + "</span>\n")
                .contains("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student2.getId() + "\">"
                        + student2.getFirstName() + " " + student2.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student2.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student2.getAge() + "</span>\n")
                .contains("                                                <a class=\"text-body\"\n" +
                        "                                                   href=\"/dashboard/students/" + student3.getId() + "\">"
                        + student3.getFirstName() + " " + student3.getLastName() + "</a>\n" +
                        "                                            </p>\n" +
                        "                                            <p class=\"text-muted mb-0\">" + student3.getEmail() + "</p>\n" +
                        "                                            <p class=\"text-muted mb-0\">\n" +
                        "                                                <span>Age:</span>\n" +
                        "                                                <span>" + student3.getAge() + "</span>\n");

    }
}