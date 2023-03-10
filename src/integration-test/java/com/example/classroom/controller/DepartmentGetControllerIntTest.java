package com.example.classroom.controller;

import com.example.classroom.model.Department;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.util.IntegrationTestsInitData;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration")
@SpringBootTest
class DepartmentGetControllerIntTest {

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

    @Test
    void shouldGetAllDepartmentsView() throws Exception {
        this.mockMvc.perform(get("/dashboard/departments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("department/all-departments"));
    }

    @Test
    void shouldGetAllDepartments_givenData() throws Exception {
        //given
        Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher dean2 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
        Department expected2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/departments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        //then
        assertThat(content).as("Page contains %s details", "department1")
                .contains("                                            <div class=\"card-body\">\n" +
                        "                                                <a class=\"card-title h5\"\n" +
                        "                                                   href=\"/dashboard/departments/" + expected1.getId()
                        + "\">" + expected1.getName() + "</a>");
        assertThat(content).as("Page contains %s details", "department2")
                .contains("                                            <div class=\"card-body\">\n" +
                        "                                                <a class=\"card-title h5\"\n" +
                        "                                                   href=\"/dashboard/departments/" + expected2.getId()
                        + "\">" + expected2.getName() + "</a>");
    }

    @Test
    void shouldGetDepartmentView() throws Exception {
        Department expected = initData.createDepartmentOne(null, List.of());
        this.mockMvc.perform(get("/dashboard/departments/" + expected.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("department/department-view"));
    }

    @Test
    void shouldGetDepartment_givenCorrectData() throws Exception {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/departments/" + expected.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        //then
        assertThat(content).as("Page contains %s details", "department")
                .contains(
                        "        <h4>Department name:</h4>\n" +
                                "        <span>" + expected.getName() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Address:</h4>\n" +
                                "        <span>" + expected.getAddress() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Phone Number:</h4>\n" +
                                "        <span>+48 " + expected.getTelNumber() + "</span>\n"
                );
        assertThat(content).as("Page contains %s details", "department's dean")
                .contains("        <a class=\"text-body\" href=\"/dashboard/teachers/" + dean.getId() + "\">\n" +
                        "            <span>" + dean.getFirstName() + " " + dean.getLastName() + "</span>\n" +
                        "            <i class=\"fas fa-regular fa-up-right-from-square ml-1\"></i>\n" +
                        "        </a>"
                );
        assertThat(content).as("Page contains %s details", "department's fieldOfStudy1")
                .contains(
                        "                    <!-- ======= FieldOfStudy Details ======= -->\n" +
                                "                    <div class=\"card h-100\">\n" +
                                "                        <div class=\"bg-image hover-overlay ripple\" data-mdb-ripple-color=\"light\">\n" +
                                "                            <img class=\"field-of-study-img\" alt=\"" + fieldOfStudy1.getName() + "\" src=\"/img/fields-of-study/ " + fieldOfStudy1.getImage() + "\">\n" +
                                "                            \n" +
                                "                            <a href=\"/dashboard/fields-of-study/" + fieldOfStudy1.getId() + "\">\n" +
                                "                                <div class=\"mask\" style=\"background-color: rgba(251, 251, 251, 0.15);\"></div>\n" +
                                "                            </a>\n" +
                                "                        </div>\n" +
                                "                        <div class=\"card-body\">\n" +
                                "                            <h5 class=\"card-title\">" + fieldOfStudy1.getName() + "</h5>\n" +
                                "                            <span class=\"text-muted\">" +
                                fieldOfStudy1.getLevelOfEducation().getValue() + ", " + fieldOfStudy1.getMode().getValue().toLowerCase() + "</span>\n" +
                                "                            <p class=\"card-text\"></p>\n" +
                                "                        </div>\n" +
                                "                        <div class=\"card-footer text-center\">\n" +
                                "                            <a class=\"btn btn-primary\" href=\"/dashboard/fields-of-study/" + fieldOfStudy1.getId()
                );
        assertThat(content).as("Page contains %s details", "department's fieldOfStudy2")
                .contains(
                        "                    <!-- ======= FieldOfStudy Details ======= -->\n" +
                                "                    <div class=\"card h-100\">\n" +
                                "                        <div class=\"bg-image hover-overlay ripple\" data-mdb-ripple-color=\"light\">\n" +
                                "                            <img class=\"field-of-study-img\" alt=\"" + fieldOfStudy1.getName() + "\" src=\"/img/fields-of-study/ " + fieldOfStudy1.getImage() + "\">\n" +
                                "                            \n" +
                                "                            <a href=\"/dashboard/fields-of-study/" + fieldOfStudy1.getId() + "\">\n" +
                                "                                <div class=\"mask\" style=\"background-color: rgba(251, 251, 251, 0.15);\"></div>\n" +
                                "                            </a>\n" +
                                "                        </div>\n" +
                                "                        <div class=\"card-body\">\n" +
                                "                            <h5 class=\"card-title\">" + fieldOfStudy1.getName() + "</h5>\n" +
                                "                            <span class=\"text-muted\">" +
                                fieldOfStudy1.getLevelOfEducation().getValue() + ", " + fieldOfStudy1.getMode().getValue().toLowerCase() + "</span>\n" +
                                "                            <p class=\"card-text\"></p>\n" +
                                "                        </div>\n" +
                                "                        <div class=\"card-footer text-center\">\n" +
                                "                            <a class=\"btn btn-primary\" href=\"/dashboard/fields-of-study/" + fieldOfStudy1.getId()
                );
    }

    @Test
    void shouldGetDepartment_givenDepartmentWithNoDeanAndFieldsOfStudies() throws Exception {
        //given
        Department expected = initData.createDepartmentTwo(null, List.of());
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/dashboard/departments/" + expected.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        //then
        assertThat(content).as("Page contains %s details", "department")
                .contains(
                        "        <h4>Department name:</h4>\n" +
                                "        <span>" + expected.getName() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Address:</h4>\n" +
                                "        <span>" + expected.getAddress() + "</span>\n" +
                                "    </div>\n" +
                                "    <hr>\n" +
                                "    <div class=\"mb-4\">\n" +
                                "        <h4>Phone Number:</h4>\n" +
                                "        <span>+48 " + expected.getTelNumber() + "</span>\n"
                );
        assertThat(content).as("Page contains %s details", "department's fieldsOfStudies")
                .contains(
                        "        <span class=\"h4 mr-2\">Fields of studies:</span>\n" +
                                "        \n" +
                                "        <div class=\"mb-3\">\n" +
                                "            <span>No fields of studies, add one:</span>\n" +
                                "            <a class=\"btn btn-outline-primary btn-rounded\"\n" +
                                "   href=\"/dashboard/departments/edit/" + expected.getId() + "\">\n" +
                                "    <i class=\"fas fa-pen mr-1\"></i>\n" +
                                "    <span>Edit</span>"
                );
        assertThat(content).as("Page contains %s details", "department's fieldsOfStudies")
                .contains(
                        "        <h4>Department&#39;s dean:</h4>\n" +
                                "        \n" +
                                "        <div>\n" +
                                "            <span>Assign dean:</span>\n" +
                                "            <a class=\"btn btn-outline-primary btn-rounded\"\n" +
                                "   href=\"/dashboard/departments/edit/" + expected.getId() + "\">\n" +
                                "    <i class=\"fas fa-pen mr-1\"></i>\n" +
                                "    <span>Edit</span>"
                );
    }

    @Test
    void getNewDepartmentFormView() throws Exception {
        this.mockMvc.perform(get("/dashboard/departments/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("department/department-create-form"));
    }

    @Test
    void getEditDepartmentFormView() throws Exception {
        Department expected = initData.createDepartmentOne(null, List.of());
        this.mockMvc.perform(get("/dashboard/departments/edit/" + expected.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("department/department-edit-form"));
    }
}