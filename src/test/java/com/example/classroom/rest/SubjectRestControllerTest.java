package com.example.classroom.rest;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.InitData;
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
    private InitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Subject subject = initData.createSubjectOne(null, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects/" + subject.getId());
        ResponseEntity<SubjectDto> response = restTemplate.getForEntity(url, SubjectDto.class);
        Optional<Subject> byId = subjectRepository.findById(subject.getId());
        assertThat(byId).isPresent();
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SubjectDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(subject.getId());
        assertThat(actual.getName()).isEqualTo(subject.getName());
        assertThat(actual.getDescription()).isEqualTo(subject.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(subject.getHoursInSemester());
        assertThat(actual.getTeachers())
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
    }

    @Test
    void shouldGetAllSubjects() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        initData.createSubjectTwo(null, List.of(teacher1));
        initData.createSubjectFour(null, List.of(teacher2));
        //when
        URI url = createURL("/api/subjects/");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual).isNotEmpty();
        assertThat(actual).size().isEqualTo(2);
       }

    @Test
    void shouldCreateSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        SubjectDto subjectDto = createSubjectDto(List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/subjects/create");
        ResponseEntity<SubjectDto> response = restTemplate.postForEntity(url, subjectDto, SubjectDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubjectDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        subjectRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Subject with ID= " + actual.getId() + " should not be missing"));
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
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void shouldUpdateSubject() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Subject subjectEntity = initData.createSubjectOne(null, List.of());
        SubjectDto subjectDto = createSubjectDto(List.of(teacher1, teacher2));
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
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void shouldDeleteSubject() throws URISyntaxException {
        //given
        Subject subject = initData.createSubjectFour(null, List.of(
                initData.createTeacherOne(null, List.of(), List.of()),
                initData.createTeacherTwo(null, List.of(), List.of())));
        //when
        URI url = createURL("/api/subjects/" + subject.getId());
        restTemplate.delete(url);
        //then
        Optional<Subject> byId = subjectRepository.findById(subject.getId());
        assertThat(byId).isNotPresent();
        subject.getTeachers().forEach(i -> {
            teacherRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + i.getId() + " should not be removed."));
        });
    }

    @Test
    void shouldDeleteAllSubjects() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectFour(null, List.of(
                initData.createTeacherOne(null, List.of(), List.of()),
                initData.createTeacherTwo(null, List.of(), List.of())));
        Subject subject2 = initData.createSubjectOne(null, List.of(
                initData.createTeacherThree(null, List.of(), List.of()),
                initData.createTeacherTwo(null, List.of(), List.of())));
        //when
        URI url = createURL("/api/subjects");
        restTemplate.delete(url);
        //then
        Optional<Subject> byId1 = subjectRepository.findById(subject1.getId());
        Optional<Subject> byId2 = subjectRepository.findById(subject1.getId());
        assertThat(byId1).isNotPresent();
        assertThat(byId2).isNotPresent();
        subject1.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
        subject2.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
    }

    private SubjectDto createSubjectDto(List<Teacher> teachers) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setName("Speech therapy");
        subjectDto.setDescription("Classes with speech therapy specialist.");
        subjectDto.setHoursInSemester(80);
        subjectDto.setTeachers(new HashSet<>(teachers));
        return subjectDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}