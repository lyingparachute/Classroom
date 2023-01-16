package com.example.classroom.rest;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.Semester;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubjectRestControllerTest {

    @Autowired
    private IntegrationTestsInitData integrationTestsInitData;

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

    @BeforeEach
    public void setup() {
        integrationTestsInitData.cleanUp();
    }

    @Test
    void shouldGetSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = integrationTestsInitData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject expected = integrationTestsInitData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects/" + expected.getId());
        ResponseEntity<SubjectDto> response = restTemplate.getForEntity(url, SubjectDto.class);
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SubjectDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getFieldOfStudy()).isEqualTo(expected.getFieldOfStudy());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
    }

    @Test
    void shouldGetAllSubjects() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = integrationTestsInitData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = integrationTestsInitData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = integrationTestsInitData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = integrationTestsInitData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = integrationTestsInitData.createSubjectTwo(fieldOfStudy2, List.of(teacher3));
        //when
        URI url = createURL("/api/subjects/");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
       }

    @Test
    void shouldCreateSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = integrationTestsInitData.createFieldOfStudyOne(null, List.of(), List.of());

        SubjectDto expected = createSubjectDto(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects");
        ResponseEntity<SubjectDto> response = restTemplate.postForEntity(url, expected, SubjectDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubjectDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        subjectRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Subject with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getSemester()).isEqualTo(expected.getSemester());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getFieldOfStudy()).isEqualTo(fieldOfStudy);
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
    }

    @Test
    void shouldUpdateSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = integrationTestsInitData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject subjectEntity = integrationTestsInitData.createSubjectOne(null, List.of());
        SubjectDto subjectDto = createSubjectDto(fieldOfStudy, List.of(teacher1, teacher2));
        subjectDto.setId(subjectEntity.getId());

        //when
        URI url = createURL("/api/subjects/");
        final HttpEntity<SubjectDto> request = new HttpEntity<>(subjectDto);
        ResponseEntity<SubjectDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, SubjectDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SubjectDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        subjectRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Subject with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getId()).isEqualTo(subjectDto.getId());
        assertThat(actual.getName()).isEqualTo(subjectDto.getName());
        assertThat(actual.getDescription()).isEqualTo(subjectDto.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(subjectDto.getHoursInSemester());
        assertThat(actual.getTeachers()).size().isEqualTo(2);
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
    }

    @Test
    void shouldDeleteSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = integrationTestsInitData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject expected = integrationTestsInitData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects/" + expected.getId());
        restTemplate.delete(url);
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy.getId() + " and name " + fieldOfStudy.getName() + " should not be removed."));
        expected.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));
    }

    @Test
    void shouldDeleteAllSubjects() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = integrationTestsInitData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = integrationTestsInitData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = integrationTestsInitData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = integrationTestsInitData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = integrationTestsInitData.createSubjectTwo(fieldOfStudy2, List.of(teacher3));

        //when
        URI url = createURL("/api/subjects");
        restTemplate.delete(url);
        //then
        Optional<Subject> byId1 = subjectRepository.findById(fieldOfStudy1.getId());
        assertThat(byId1).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy1.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy1.getId() + " and name " + fieldOfStudy1.getName() + " should not be removed."));
        expected1.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));

        Optional<Subject> byId2 = subjectRepository.findById(fieldOfStudy2.getId());
        assertThat(byId2).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy2.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy2.getId() + " and name " + fieldOfStudy2.getName() + " should not be removed."));
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