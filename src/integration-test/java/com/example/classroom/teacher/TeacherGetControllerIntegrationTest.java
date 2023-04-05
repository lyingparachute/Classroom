package com.example.classroom.teacher;

import com.example.classroom.repository.util.IntegrationTestsInitData;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.student.Student;
import jakarta.transaction.Transactional;
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
@Transactional
@WithMockCustomUser
class TeacherGetControllerIntegrationTest {

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
    void shouldGetTeacherView() throws Exception {
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        this.mockMvc.perform(get("/dashboard/teachers/" + teacher1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("teacher/teacher-view"));
    }

    @Test
    void shouldGetParticularTeacher() throws Exception {
        // Given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Teacher teacher = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));

        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers/{id}", teacher.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains(
                        "<div class=\"page-title\">Viewing teacher with ID: " + teacher.getId() + "</div>"
                )
                .contains(
                        "        <h4>First Name:</h4>\n" +
                                "        <span>" + teacher.getFirstName() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Last Name:</h4>\n" +
                                "        <span>" + teacher.getLastName() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Email:</h4>\n" +
                                "        <span>" + teacher.getEmail() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Age:</h4>\n" +
                                "        <span>" + teacher.getAge() + "</span>\n" +
                                "    </div>"
                )
                .contains(
                        "                <a class=\"text-body\"\n" +
                                "                   href=\"/dashboard/students/" + student1.getId() + "\">\n" +
                                "                <span value=\"" + student1.getId() + "\">" + student1.getFirstName() + " " + student1.getLastName() + "</span>\n"
                )
                .contains(
                        "                <a class=\"text-body\"\n" +
                                "                   href=\"/dashboard/students/" + student2.getId() + "\">\n" +
                                "                <span value=\"" + student2.getId() + "\">" + student2.getFirstName() + " " + student2.getLastName() + "</span>\n"
                );

    }

    @Test
    void shouldGetTeachersView() throws Exception {
        this.mockMvc.perform(get("/dashboard/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("teacher/all-teachers"));
    }

    @Test
    void shouldGetTeachersAndContainParticularTeacher() throws Exception {
        // Given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of());
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student3));

        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher1.getId() + "\">"
                                + teacher1.getFirstName() + " " + teacher1.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher1.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher1.getAge() + "</span>\n"
                )
                .contains(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher2.getId() + "\">"
                                + teacher2.getFirstName() + " " + teacher2.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher2.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher2.getAge() + "</span>\n"
                );
    }

    @Test
    void shouldGetTeachersSecondPageSortedByFirstName() throws Exception {
        // Given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of());
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = integrationTestsInitData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));

        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers?page=2&size=2&sortField=firstName&sortDir=asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // used sorting dir by lastName ascending, so student1 will be last - Jarosław
        assertThat(contentAsString)
                .contains(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher1.getId() + "\">"
                                + teacher1.getFirstName() + " " + teacher1.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher1.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher1.getAge() + "</span>\n"
                )
                .doesNotContain(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher2.getId() + "\">"
                                + teacher2.getFirstName() + " " + teacher2.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher2.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher2.getAge() + "</span>\n"
                )
                .doesNotContain(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher3.getId() + "\">"
                                + teacher3.getFirstName() + " " + teacher3.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher3.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher3.getAge() + "</span>\n"
                );
    }

    @Test
    void shouldGetTeachersSearchView() throws Exception {
        this.mockMvc.perform(get("/dashboard/teachers?name=w"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("teacher/all-teachers"));
    }

    @Test
    void shouldGetResultOfSearchTeachersByFirstOrLastName() throws Exception {
        // Given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of());
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = integrationTestsInitData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));

        // When
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers?name=ja"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher1.getId() + "\">"
                                + teacher1.getFirstName() + " " + teacher1.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher1.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher1.getAge() + "</span>\n"
                )
                .contains(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher2.getId() + "\">"
                                + teacher2.getFirstName() + " " + teacher2.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher2.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher2.getAge() + "</span>\n"
                )
                .doesNotContain(
                        "                                                <a class=\"text-body\"\n" +
                                "                                                   href=\"/dashboard/teachers/" + teacher3.getId() + "\">"
                                + teacher3.getFirstName() + " " + teacher3.getLastName() + "</a>\n" +
                                "                                            </p>\n" +
                                "                                            <p class=\"text-muted mb-0\">" + teacher3.getEmail() + "</p>\n" +
                                "                                            <p class=\"text-muted mb-0\">\n" +
                                "                                                <span>Age:</span>\n" +
                                "                                                <span>" + teacher3.getAge() + "</span>\n"
                );
    }
}