package com.example.classroom.subject;

import com.example.classroom.enums.Semester;
import com.example.classroom.fieldOfStudy.FieldOfStudy;
import com.example.classroom.fieldOfStudy.FieldOfStudyRepository;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.teacher.TeacherRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration")
@SpringBootTest
@Transactional
@WithMockCustomUser
class SubjectControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

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
    class CreateSubject {
        @Test
        void createsSubject_andRedirectsToAllSubjects_givenAdminRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            SubjectDto expected = createSubjectDto();

            // When
            mockMvc.perform(post("/dashboard/subjects/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<Subject> byId = subjectRepository.findAll().stream().findFirst();
            assertThat(byId).isPresent();
            Subject actual = byId.get();
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isNotNull(),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
                            .extracting(
                                    Teacher::getId,
                                    Teacher::getFirstName,
                                    Teacher::getLastName,
                                    Teacher::getEmail,
                                    Teacher::getAge
                            ).containsExactlyInAnyOrder(
                                    tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                            teacher1.getEmail(), teacher1.getAge()),
                                    tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                            teacher2.getEmail(), teacher2.getAge()))
            );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns403_forbiddenStatus_withServletException_givenStudentRole() {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            SubjectDto expected = createSubjectDto();

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(post("/dashboard/subjects/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
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
            SubjectDto expected = createSubjectDto();

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(post("/dashboard/subjects/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void createsSubject_andRedirectsToAllSubjects_givenDeanRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            SubjectDto expected = createSubjectDto();

            // When
            mockMvc.perform(post("/dashboard/subjects/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    class EditSubject {
        @Test
        void updatesSubject_andRedirectsToAllSubjects_givenAdminRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(post("/dashboard/subjects/update")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("id=" + expected.getId() +
                                    "&name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<Subject> byId = subjectRepository.findAll().stream().findFirst();
            assertThat(byId).isPresent();
            Subject actual = byId.get();
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
                            .extracting(
                                    Teacher::getId,
                                    Teacher::getFirstName,
                                    Teacher::getLastName,
                                    Teacher::getEmail,
                                    Teacher::getAge
                            ).containsExactlyInAnyOrder(
                                    tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                            teacher1.getEmail(), teacher1.getAge()),
                                    tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                            teacher2.getEmail(), teacher2.getAge()))
            );
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void updatesSubject_andRedirectsToAllSubjects_givenDeanRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(post("/dashboard/subjects/update")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("id=" + expected.getId() +
                                    "&name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns403_forbiddenStatus_withServletException_givenStudentRole() {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(post("/dashboard/subjects/update")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("id=" + expected.getId() +
                                    "&name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
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

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(post("/dashboard/subjects/update")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("id=" + expected.getId() +
                                    "&name=" + expected.getName() +
                                    "&description=" + expected.getDescription() +
                                    "&semester=" + expected.getSemester() +
                                    "&hoursInSemester=" + expected.getHoursInSemester() +
                                    "&ectsPoints=" + expected.getEctsPoints() +
                                    "&fieldOfStudy=" + fieldOfStudy.getId() +
                                    "&teachers=" + teacher1.getId() +
                                    "&_teachersList=on" +
                                    "&teachers=" + teacher2.getId() +
                                    "&_teachersList=on" +
                                    "&add="))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

    }

    @Nested
    class DeleteSubject {
        @Test
        void removesSubject_andRedirectsToAllSubjects_givenAdminRole() throws Exception {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            mockMvc.perform(get("/dashboard/subjects/delete/{id}", expected.getId()))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<Subject> byId = subjectRepository.findById(expected.getId());
            assertThat(byId).isNotPresent();
            fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                    "FieldOfStudy with ID= " + fieldOfStudy.getId() + " and name " +
                            fieldOfStudy.getName() + " should not be removed."));
            expected.getTeachers().forEach(teacher ->
                    teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                            "Teacher with ID = " + teacher.getId() + " and name " +
                                    teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_STUDENT)
        void returns403_forbiddenStatus_withServletException_givenStudentRole() {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/subjects/delete/{id}", expected.getId()))
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

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/subjects/delete/{id}", expected.getId()))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        @WithMockCustomUser(role = UserRole.ROLE_DEAN)
        void returns403_forbiddenStatus_withServletException_givenDeanRole() {
            // Given
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));

            // When
            assertThatThrownBy(() ->
                    mockMvc.perform(get("/dashboard/subjects/delete/{id}", expected.getId()))
            ).isExactlyInstanceOf(ServletException.class)
                    .message().contains("Access Denied");
        }

        @Test
        void returns400_withIllegalArgumentExceptionAsContent_givenAdminRole_andWrongSubjectID() throws Exception {
            // Given
            Long id = 100L;
            String expectedExceptionMsg = "Invalid subject id: " + id;

            // When
            MvcResult mvcResult = mockMvc.perform(get("/dashboard/subjects/delete/{id}", id))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .andExpect(content().string(expectedExceptionMsg))
                    .andReturn();
        }
    }

    private SubjectDto createSubjectDto() {
        SubjectDto expected = new SubjectDto();
        expected.setName("name");
        expected.setDescription("description");
        expected.setSemester(Semester.FIFTH);
        expected.setHoursInSemester(25);
        expected.setEctsPoints(22);
        return expected;
    }

}