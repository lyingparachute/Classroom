package com.example.classroom.controller;

import com.example.classroom.model.Student;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.util.IntegrationTestsInitData;
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
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Teacher teacher = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers/" + teacher.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                <div class=\"card-header py-3 bg-secondary bg-gradient bg-opacity-25\">\n" +
                        "                    <h3 class=\"mb-0 text-center\">\n" +
                        "                        <strong>Viewing teacher with ID: " + teacher.getId() + "</strong>");
        assertThat(contentAsString)
                .contains("                        <li class=\"list-group-item\">ID number: " + teacher.getId() + "</li>\n" +
                        "                        <li class=\"list-group-item\">First Name: " + teacher.getFirstName() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Last Name: " + teacher.getLastName() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Email: " + teacher.getEmail() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Age: " + teacher.getAge() + "</li>\n" +
                        "                        <li class=\"list-group-item\">List of assigned students:\n" +
                        "                            <ul class=\"list-group\">"
                );
        assertThat(contentAsString)
                .containsIgnoringWhitespaces("                                       href=\"/students/" + student1.getId() + "\"\n" +
                        "                                       value=\"" + student1.getId() + "\">" + student1.getFirstName() + " " + student1.getLastName());
        assertThat(contentAsString)
                .containsIgnoringWhitespaces("                                       href=\"/students/" + student2.getId() + "\"\n" +
                        "                                       value=\"" + student2.getId() + "\">" + student2.getFirstName() + " " + student2.getLastName());

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
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of());
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student3));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                             <td>" + teacher1.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher1.getLastName() + "</td>\n" +
                        "                                <td>" + teacher1.getAge() + "</td>\n" +
                        "                                <td>" + teacher1.getEmail() + "</td>\n" +
                        "                                <td>" + teacher1.getStudents().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                             <td>" + teacher2.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher2.getLastName() + "</td>\n" +
                        "                                <td>" + teacher2.getAge() + "</td>\n" +
                        "                                <td>" + teacher2.getEmail() + "</td>\n" +
                        "                                <td>" + teacher2.getStudents().size() + "</td>\n");
    }

    @Test
    void shouldGetTeachersSecondPageSortedByFirstName() throws Exception {
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of());
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = integrationTestsInitData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers?page=2&size=2&sortField=firstName&sortDir=asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // used sorting dir by lastName ascending, so student1 will be last - Jarosław
        assertThat(contentAsString)
                .contains("                             <td>" + teacher1.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher1.getLastName() + "</td>\n" +
                        "                                <td>" + teacher1.getAge() + "</td>\n" +
                        "                                <td>" + teacher1.getEmail() + "</td>\n" +
                        "                                <td>" + teacher1.getStudents().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                             <td>" + teacher2.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher2.getLastName() + "</td>\n" +
                        "                                <td>" + teacher2.getAge() + "</td>\n" +
                        "                                <td>" + teacher2.getEmail() + "</td>\n" +
                        "                                <td>" + teacher2.getStudents().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                             <td>" + student3.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher3.getLastName() + "</td>\n" +
                        "                                <td>" + teacher3.getAge() + "</td>\n" +
                        "                                <td>" + teacher3.getEmail() + "</td>\n" +
                        "                                <td>" + teacher3.getStudents().size() + "</td>\n");
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
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Student student3 = integrationTestsInitData.createStudentThree(null, List.of());
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = integrationTestsInitData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/teachers?name=ja"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                                <td>" + teacher1.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher1.getLastName() + "</td>\n" +
                        "                                <td>" + teacher1.getAge() + "</td>\n" +
                        "                                <td>" + teacher1.getEmail() + "</td>\n" +
                        "                                <td>" + teacher1.getStudents().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                                <td>" + teacher2.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher2.getLastName() + "</td>\n" +
                        "                                <td>" + teacher2.getAge() + "</td>\n" +
                        "                                <td>" + teacher2.getEmail() + "</td>\n" +
                        "                                <td>" + teacher2.getStudents().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                                <td>" + student3.getFirstName() + "</td>\n" +
                        "                                <td>" + teacher3.getLastName() + "</td>\n" +
                        "                                <td>" + teacher3.getAge() + "</td>\n" +
                        "                                <td>" + teacher3.getEmail() + "</td>\n" +
                        "                                <td>" + teacher3.getStudents().size() + "</td>\n");
    }

}