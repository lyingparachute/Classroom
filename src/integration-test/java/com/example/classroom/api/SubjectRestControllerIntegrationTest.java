package com.example.classroom.api;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.enums.Semester;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Subject;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubjectRestControllerIntegrationTest {

    @Autowired
    private IntegrationTestsInitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    ModelMapper mapper;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject expected = initData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects/" + expected.getId());
        ResponseEntity<SubjectDto> response = restTemplate.getForEntity(url, SubjectDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SubjectDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Subject").isNotNull();
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
                                Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                        teacher1.getEmail(), teacher1.getAge()),
                                Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                        teacher2.getEmail(), teacher2.getAge()))
        );
    }

    @Test
    void shouldGetAllSubjects() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expectedSubject1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expectedSubject2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
        Subject expectedSubject3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
        //when
        URI url = createURL("/api/subjects/");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set<SubjectDto> actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(3);
    }

    @Test
    void shouldCreateSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        SubjectDto expected = createSubjectDto(fieldOfStudy, List.of(teacher1, teacher2));
        Subject expectedEntity = mapper.map(expected, Subject.class);
        //when
        URI url = createURL("/api/subjects");
        ResponseEntity<SubjectDto> response = restTemplate.postForEntity(url, expected, SubjectDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubjectDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Subject").isNotNull();
        assertThat(actual.getId()).as("Check %s's %s", "Subject", "ID").isNotNull();
        subjectRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Subject with ID= " + actual.getId() + " should not be missing"));
        assertAll("Subject's properties",
                () -> assertThat(actual.getName())
                        .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getDescription())
                        .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                () -> assertThat(actual.getSemester())
                        .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                () -> assertThat(actual.getHoursInSemester())
                        .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester())
        );
        assertAll("Subject's fieldOfStudy properties",
                () -> assertThat(actual.getFieldOfStudy().getId())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                () -> assertThat(actual.getFieldOfStudy().getName())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                () -> assertThat(actual.getFieldOfStudy().getMode())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                () -> assertThat(actual.getFieldOfStudy().getTitle())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                () -> assertThat(actual.getFieldOfStudy().getSubjects())
                        .as("Check %s's %s properties", "fieldOfStudy", "subjects")
                        .extracting(
                                Subject::getName,
                                Subject::getDescription,
                                Subject::getSemester,
                                Subject::getHoursInSemester,
                                Subject::getFieldOfStudy
                        ).containsExactlyInAnyOrder(
                                Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                        expected.getHoursInSemester(), fieldOfStudy))
        );
        assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        assertThat(teacher1.getSubjects()).as("Check if %s' %s list contains subject", "teacher1", "subjects")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(teacher2.getSubjects()).as("Check if %s' %s list contains subject", "teacher2", "subjects")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
    }

    @Test
    void shouldUpdateSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject subjectEntity = initData.createSubjectOne(null, List.of());
        SubjectDto expected = createSubjectDto(fieldOfStudy, List.of(teacher1, teacher2));
        expected.setId(subjectEntity.getId());
        Subject expectedEntity = mapper.map(expected, Subject.class);
        //when
        URI url = createURL("/api/subjects/");
        final HttpEntity<SubjectDto> request = new HttpEntity<>(expected);
        ResponseEntity<SubjectDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, SubjectDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SubjectDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Subject").isNotNull();
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
                        .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester())
        );
        assertAll("Subject's fieldOfStudy properties",
                () -> assertThat(actual.getFieldOfStudy().getId())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                () -> assertThat(actual.getFieldOfStudy().getName())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                () -> assertThat(actual.getFieldOfStudy().getMode())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                () -> assertThat(actual.getFieldOfStudy().getTitle())
                        .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                () -> assertThat(actual.getFieldOfStudy().getSubjects())
                        .as("Check %s's %s properties", "fieldOfStudy", "subjects")
                        .extracting(
                                Subject::getName,
                                Subject::getDescription,
                                Subject::getSemester,
                                Subject::getHoursInSemester,
                                Subject::getFieldOfStudy
                        ).containsExactlyInAnyOrder(
                                Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                        expected.getHoursInSemester(), fieldOfStudy))
        );
        assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        assertThat(teacher1.getSubjects()).as("Check if %s' %s list contains subject", "teacher1", "subjects")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(teacher2.getSubjects()).as("Check if %s' %s list contains subject", "teacher2", "subjects")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
    }

    @Test
    void shouldDeleteSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject expected = initData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects/" + expected.getId());
        restTemplate.delete(url);
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy.getId() + " and name " +
                        fieldOfStudy.getName() + " should not be removed."));
        expected.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));
    }

    @Test
    void shouldDeleteAllSubjects() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectTwo(fieldOfStudy2, List.of(teacher3));
        //when
        URI url = createURL("/api/subjects");
        restTemplate.delete(url);
        //then
        Optional<Subject> byId1 = subjectRepository.findById(fieldOfStudy1.getId());
        assertThat(byId1).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy1.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy1.getId() + " and name " +
                        fieldOfStudy1.getName() + " should not be removed."));
        expected1.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));

        Optional<Subject> byId2 = subjectRepository.findById(fieldOfStudy2.getId());
        assertThat(byId2).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy2.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy2.getId() + " and name " +
                        fieldOfStudy2.getName() + " should not be removed."));
        expected2.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));
    }

    private SubjectDto createSubjectDto(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setName("Speech therapy");
        subjectDto.setDescription("Classes with speech therapy specialist.");
        subjectDto.setSemester(Semester.SEVENTH);
        subjectDto.setHoursInSemester(80);
        subjectDto.setFieldOfStudy(fieldOfStudy);
        subjectDto.setTeachers(new HashSet<>(teachers));
        return subjectDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}