package com.example.classroom.subject;

import com.example.classroom.fieldofstudy.FieldOfStudy;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.test.util.IntegrationTestsInitData;
import com.example.classroom.user.UserRole;
import jakarta.servlet.ServletException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration")
@SpringBootTest
@WithMockCustomUser
class SubjectGetControllerIntegrationTest {

    private static final String SUBJECTS_ENDPOINT_NAME = "subjects";
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
    class GetSubject {
        @Test
        void returns200_withSubjectView_andContent_givenAdminRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-view"))
                    .andReturn();

            // Then
            String contentAsString = mvcResult.getResponse().getContentAsString();

            assertThat(contentAsString)
                    .contains(
                            "<div class=\"page-title\">Viewing Subject with ID: " + subject.getId() + "</div>"
                    )
                    .contains(
                            "    <div class=\"mb-4\">\n" +
                                    "        <h4>Subject name:</h4>\n" +
                                    "        <span>" + subject.getName() + "</span>\n" +
                                    "    </div>\n" +
                                    "    <hr>\n" +
                                    "    <div class=\"mb-4\">\n" +
                                    "        <h4>Description:</h4>\n" +
                                    "        <span>" + subject.getDescription() + "</span>\n" +
                                    "    </div>\n" +
                                    "    <hr>\n" +
                                    "    <div class=\"mb-4\">\n" +
                                    "        <h4>Hours in semester:</h4>\n" +
                                    "        <span>" + subject.getHoursInSemester() + "</span>\n" +
                                    "    </div>\n" +
                                    "    <hr>\n" +
                                    "    <div class=\"mb-4\">\n" +
                                    "        <h4>ECTS points:</h4>\n" +
                                    "        <span>" + subject.getEctsPoints() + "</span>\n" +
                                    "    </div>"
                    )
                    .contains(
                            "        <h4>Field of study:</h4>\n" +
                                    "        <a class=\"text-body\"\n" +
                                    "           href=\"/dashboard/fields-of-study/" + fieldOfStudy.getId() + "\">\n" +
                                    "            <span>" + fieldOfStudy.getName() + "</span>\n" +
                                    "            <i class=\"fas fa-regular fa-up-right-from-square\"></i>\n" +
                                    "        </a>"
                    );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns200_OKStatus_givenStudentRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-view"));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns200_OKStatus_givenTeacherRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-view"));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void returns200_OKStatus_givenDeanRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-view"));
        }
    }

    @Nested
    class GetSubjects {
        @Test
        void returns200_withAllSubjectsView_andContent_givenAdminRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Subject subject2 = initData.createSubjectTwo(fieldOfStudy2, List.of());

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/all-" + SUBJECTS_ENDPOINT_NAME))
                    .andReturn();

            // Then
            String contentAsString = mvcResult.getResponse().getContentAsString();
            assertThat(contentAsString)
                    .contains(
                            "                                    <td>" + subject1.getId() + "</td>\n" +
                                    "                                    <td>" + subject1.getName() + "</td>\n" +
                                    "                                    <td>" + subject1.getDescription() + "</td>\n" +
                                    "                                    <td>" + subject1.getHoursInSemester() + "</td>\n" +
                                    "                                    <td>\n" +
                                    "                                        <a class=\"text-body mb-1\" href=\"/dashboard/fields-of-study/" + fieldOfStudy1.getId() + "\">\n" +
                                    "                                            <span>" + fieldOfStudy1 + "</span>\n" +
                                    "                                            <i class=\"fas fa-regular fa-up-right-from-square ml-1\"></i>\n" +
                                    "                                        </a>\n" +
                                    "                                        \n" +
                                    "                                    </td>\n"
                    )
                    .contains(
                            "                                    <td>" + subject2.getId() + "</td>\n" +
                                    "                                    <td>" + subject2.getName() + "</td>\n" +
                                    "                                    <td>" + subject2.getDescription() + "</td>\n" +
                                    "                                    <td>" + subject2.getHoursInSemester() + "</td>\n" +
                                    "                                    <td>\n" +
                                    "                                        <a class=\"text-body mb-1\" href=\"/dashboard/fields-of-study/" + fieldOfStudy2.getId() + "\">\n" +
                                    "                                            <span>" + fieldOfStudy2 + "</span>\n" +
                                    "                                            <i class=\"fas fa-regular fa-up-right-from-square ml-1\"></i>\n" +
                                    "                                        </a>\n" +
                                    "                                        \n" +
                                    "                                    </td>\n"
                    );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns200_OKStatus_givenStudentRole() throws Exception {
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/all-" + SUBJECTS_ENDPOINT_NAME));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns200_OKStatus_givenTeacherRole() throws Exception {
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/all-" + SUBJECTS_ENDPOINT_NAME));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void returns200_OKStatus_givenDeanRole() throws Exception {
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/all-" + SUBJECTS_ENDPOINT_NAME));
        }
    }

    @Nested
    class CreateSubject {
        @Test
        void returns200_withAddNewSubjectView_andContent_givenAdminRole() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/new"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-create-form"))
                    .andReturn();

            // Then
            String contentAsString = mvcResult.getResponse().getContentAsString();
            assertThat(contentAsString)
                    .contains(
                            "<h4>Subject name:</h4>"
                    )
                    .contains(
                            "<h4>Description:</h4>"
                    )
                    .contains(
                            "        <h4>Semester:</h4>\n" +
                                    "        <select class=\"form-select\" id=\"semester\" name=\"semester\">\n" +
                                    "            <option value=\"FIRST\">I (first)</option>\n" +
                                    "            <option value=\"SECOND\">II (second)</option>\n" +
                                    "            <option value=\"THIRD\">III (third)</option>\n" +
                                    "            <option value=\"FOURTH\">IV (fourth)</option>\n" +
                                    "            <option value=\"FIFTH\">V (fifth)</option>\n" +
                                    "            <option value=\"SIXTH\">VI (sixth)</option>\n" +
                                    "            <option value=\"SEVENTH\">VII (seventh)</option>\n" +
                                    "        </select>\n"
                    )
                    .contains(
                            "        <h4>Hours in semester:</h4>\n" +
                                    "        <div class=\"form-outline\">\n" +
                                    "            <input class=\"form-control\"\n" +
                                    "                   type=\"number\" id=\"hoursInSemester\" name=\"hoursInSemester\" value=\"0\"/>\n" +
                                    "            <div class=\"form-helper\">How many hours are conducted within this subject in one semester?</div>\n"
                    )
                    .contains(
                            "        <h4>ECTS points:</h4>\n" +
                                    "        <div class=\"form-outline\">\n" +
                                    "            <input class=\"form-control\"\n" +
                                    "                   type=\"number\" id=\"ectsPoints\" name=\"ectsPoints\" value=\"0\"/>\n" +
                                    "            <div class=\"form-helper\">How important this subject is? Value between 5 and 60.</div>\n"
                    )
                    .contains(
                            "        <h4>Field of study:</h4>\n" +
                                    "        <div class=\"col-sm\">\n" +
                                    "            <select class=\"form-select\" id=\"field_of_study\" name=\"fieldOfStudy\">"
                    );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns403_forbiddenStatus_withServletException_givenStudentRole() {
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/new"))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns403_forbiddenStatus_withServletException_givenTeacherRole() {
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/new"))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void returns200_withAddNewSubjectView_givenDeanRole() throws Exception {
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/new"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-create-form"));
        }
    }

    @Nested
    class EditSubject {
        @Test
        void returns200_withEditSubjectView_andContent_givenAdminRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/edit/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-edit-form"))
                    .andReturn();

            // Then
            String contentAsString = mvcResult.getResponse().getContentAsString();
            assertThat(contentAsString)
                    .contains(
                            "        <h4>Subject name:</h4>\n" +
                                    "        <div class=\"form-outline\">\n" +
                                    "            <input class=\"form-control\"\n" +
                                    "                   data-mdb-showcounter=\"true\"\n" +
                                    "                   maxlength=\"30\" type=\"text\" id=\"name\" name=\"name\" value=\"" + subject.getName() + "\"/>"
                    )
                    .contains(
                            "        <h4>Description:</h4>\n" +
                                    "        <div class=\"form-outline\">\n" +
                                    "            <textarea class=\"form-control\"\n" +
                                    "                      data-mdb-showcounter=\"true\"\n" +
                                    "                      maxlength=\"500\" id=\"description\" name=\"description\">" + subject.getDescription() + "</textarea>"
                    )
                    .contains(
                            "        <h4>Semester:</h4>\n" +
                                    "        <select class=\"form-select\" id=\"semester\" name=\"semester\">\n" +
                                    "            <option value=\"FIRST\">I (first)</option>\n" +
                                    "            <option value=\"SECOND\">II (second)</option>\n" +
                                    "            <option value=\"THIRD\">III (third)</option>\n" +
                                    "            <option value=\"FOURTH\">IV (fourth)</option>\n" +
                                    "            <option value=\"FIFTH\">V (fifth)</option>\n" +
                                    "            <option value=\"SIXTH\" selected=\"selected\">VI (sixth)</option>\n" +
                                    "            <option value=\"SEVENTH\">VII (seventh)</option>\n" +
                                    "        </select>"
                    )
                    .contains(
                            "        <h4>Hours in semester:</h4>\n" +
                                    "        <div class=\"form-outline\">\n" +
                                    "            <input class=\"form-control\"\n" +
                                    "                   type=\"number\" id=\"hoursInSemester\" name=\"hoursInSemester\" value=\"" + subject.getHoursInSemester() + "\"/>"
                    )
                    .contains(
                            "        <h4>ECTS points:</h4>\n" +
                                    "        <div class=\"form-outline\">\n" +
                                    "            <input class=\"form-control\"\n" +
                                    "                   type=\"number\" id=\"ectsPoints\" name=\"ectsPoints\" value=\"" + subject.getEctsPoints() + "\"/>"
                    )
                    .contains(
                            "        <h4>Field of study:</h4>\n" +
                                    "        <div class=\"col-sm\">\n" +
                                    "            <select class=\"form-select\" id=\"field_of_study\" name=\"fieldOfStudy\">\n" +
                                    "                <option value=\"0\"></option>\n" +
                                    "                <option value=\"" + fieldOfStudy.getId() + "\" selected=\"selected\">"
                                    + fieldOfStudy.getName() + ", " + fieldOfStudy.getLevelOfEducation().getValue() + ", " + fieldOfStudy.getMode().getValue() + "</option>"
                    )
                    .contains(
                            "h4>Teachers assigned to this subject:</h4>\n"
                    )
                    .contains(
                            "<input class=\"form-check-input m-2\" type=\"checkbox\" value=\"" + teacher1.getId() + "\" id=\"teachers_list\" " +
                                    "name=\"teachers\"><input type=\"hidden\" name=\"_teachers\" value=\"on\"/>" + teacher1 + "</input>"
                    )
                    .contains(
                            "<input class=\"form-check-input m-2\" type=\"checkbox\" value=\"" + teacher2.getId() + "\" id=\"teachers_list\" " +
                                    "name=\"teachers\"><input type=\"hidden\" name=\"_teachers\" value=\"on\"/>" + teacher2 + "</input>"
                    );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns403_forbiddenStatus_withServletException_givenStudentRole() {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/edit/{id}", subject.getId()))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns403_forbiddenStatus_withServletException_givenTeacherRole() {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/edit/{id}", subject.getId()))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        void returns200_withEditSubjectView_givenDeanRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject subject = initData.createSubjectFour(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(get("/dashboard/" + SUBJECTS_ENDPOINT_NAME + "/edit/{id}", subject.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("subject/subject-edit-form"));
        }
    }
}