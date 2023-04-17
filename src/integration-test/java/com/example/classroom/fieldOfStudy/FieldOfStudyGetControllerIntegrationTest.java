package com.example.classroom.fieldOfStudy;

import com.example.classroom.department.Department;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.student.Student;
import com.example.classroom.subject.Subject;
import com.example.classroom.test.util.IntegrationTestsInitData;
import com.example.classroom.user.UserRole;
import jakarta.servlet.ServletException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
            MvcResult mvcResult = mockMvc.perform(get("/dashboard" + ENDPOINT_NAME + "/" + expected.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("field-of-study/fieldOfStudy-view"))
                    .andReturn();

            // Then
            assertThat(mvcResult.getResponse().getContentAsString()).isNotNull().isNotEmpty().satisfies(content -> {
                        assertThat(content)
                                .contains("<div class=\"page-title\">Inżynieria mechaniczno-medyczna</div>")
                                .contains();
                    }
            );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns403_forbiddenStatus_withServletException_givenStudentRole() {
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(
                    department, List.of(subject1, subject2), List.of(student1, student2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/" + ENDPOINT_NAME + "/" + expected.getId()))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                            .andExpect(view().name("subject/subject-view"))
                            .andReturn()
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_TEACHER)
        void returns403_forbiddenStatus_withServletException_givenTeacherRole() {
            // Given
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudyDto expected = createFieldOfStudyDto();

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(post("/dashboard/" + ENDPOINT_NAME + "/new")
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .content("name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getLevelOfEducation() +
                                    "&hoursInSemester=" + expected.getMode() +
                                    "&ectsPoints=" + expected.getTitle() +
                                    "&image=" + expected.getImage() +
                                    "&department=" + department.getId() +
                                    "&subjects=" + subject1.getId() +
                                    "&_subjectsList=on" +
                                    "&subjects=" + subject2.getId() +
                                    "&_subjectsList=on" +
                                    "&add=" +
                                    "&students=" + student1.getId() +
                                    "&_studentsList=on" +
                                    "&students=" + student2.getId() +
                                    "&_studentsList=on" +
                                    "&add="))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void createsSubject_andRedirectsToAllSubjects_givenDeanRole() throws Exception {
            // Given
            Department department = initData.createDepartmentOne(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudyDto expected = createFieldOfStudyDto();

            // When
            mockMvc.perform(post("/dashboard/" + ENDPOINT_NAME + "/new")
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .content("name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getLevelOfEducation() +
                                    "&hoursInSemester=" + expected.getMode() +
                                    "&ectsPoints=" + expected.getTitle() +
                                    "&image=" + expected.getImage() +
                                    "&department=" + department.getId() +
                                    "&subjects=" + subject1.getId() +
                                    "&_subjectsList=on" +
                                    "&subjects=" + subject2.getId() +
                                    "&_subjectsList=on" +
                                    "&add=" +
                                    "&students=" + student1.getId() +
                                    "&_studentsList=on" +
                                    "&students=" + student2.getId() +
                                    "&_studentsList=on" +
                                    "&add="))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());
        }
    }

    private FieldOfStudyDto createFieldOfStudyDto() {
        FieldOfStudyDto expected = new FieldOfStudyDto();
        expected.setName("name");
        expected.setDescription("description");
        expected.setLevelOfEducation(LevelOfEducation.SECOND);
        expected.setMode(ModeOfStudy.FT);
        expected.setTitle(AcademicTitle.MGR);
        expected.setImage("Image.jpg");
        return expected;
    }
}
