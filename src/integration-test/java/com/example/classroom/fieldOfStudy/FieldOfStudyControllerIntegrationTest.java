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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration")
@SpringBootTest
@Transactional
@WithMockCustomUser
class FieldOfStudyControllerIntegrationTest {

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
    class CreateFieldOfStudy {
        @Test
        void createsFieldOfStudy_andRedirectsToAllFieldsOfStudy_givenAdminRole() throws Exception {
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

            // Then
            Optional<FieldOfStudy> byId = fieldOfStudyRepository.findAll().stream().findFirst();
            assertThat(byId).isPresent();
            FieldOfStudy actual = byId.get();
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isNotNull(),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getDepartment())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(department),
                    () -> assertThat(actual.getStudents()).as("Check %s's %s properties", "Subject", "teachers")
                            .extracting(
                                    Student::getId,
                                    Student::getFirstName,
                                    Student::getLastName,
                                    Student::getEmail,
                                    Student::getAge
                            ).containsExactlyInAnyOrder(
                                    tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                            student1.getEmail(), student1.getAge()),
                                    tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                            student2.getEmail(), student2.getAge()))
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
