package com.example.classroom.rest;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.assertj.core.api.Assertions;
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
class StudentRestControllerTest {

    @Autowired
    private IntegrationTestsInitData integrationTestsInitData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        integrationTestsInitData.cleanUp();
    }

    @Test
    void shouldGetStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Student student = integrationTestsInitData.createStudentOne(null, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/students/" + student.getId());
        ResponseEntity<StudentDto> response = restTemplate.getForEntity(url, StudentDto.class);
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isPresent();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(student.getId());
        assertThat(actual.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(student.getLastName());
        assertThat(actual.getEmail()).isEqualTo(student.getEmail());
        assertThat(actual.getAge()).isEqualTo(student.getAge());
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
    void shouldGetAllStudents() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());

        integrationTestsInitData.createStudentOne(null, List.of(teacher1));
        integrationTestsInitData.createStudentTwo(null, List.of(teacher2));
        //when
        URI url = createURL("/api/students/");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
       }

    @Test
    void shouldCreateStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        StudentDto studentDto = createStudentDto(List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/students");
        ResponseEntity<StudentDto> response = restTemplate.postForEntity(url, studentDto, StudentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        StudentDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        studentRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Student with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getFirstName()).isEqualTo(studentDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(studentDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(studentDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(studentDto.getAge());
        Assertions.assertThat(actual.getTeachers()).size().isEqualTo(2);
        Assertions.assertThat(actual.getTeachers())
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
    void shouldUpdateStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Student studentEntity = integrationTestsInitData.createStudentOne(null, List.of());
        StudentDto studentDto = new StudentDto();
        studentDto.setId(studentEntity.getId());
        studentDto.setFirstName("Pamela");
        studentDto.setLastName("Gonzales");
        studentDto.setEmail("p.gonzales@gmail.com");
        studentDto.setAge(20);
        studentDto.setTeachers(new HashSet<>(List.of(teacher1, teacher2)));

        //when
        URI url = createURL("/api/students/");
        final HttpEntity<StudentDto> request = new HttpEntity<>(studentDto);
        ResponseEntity<StudentDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, StudentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        studentRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Student with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getId()).isEqualTo(studentDto.getId());
        assertThat(actual.getFirstName()).isEqualTo(studentDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(studentDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(studentDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(studentDto.getAge());
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
    void shouldDeleteStudent() throws URISyntaxException {
        //given
        Student student = integrationTestsInitData.createStudentOne(null, List.of(
                integrationTestsInitData.createTeacherOne(null, List.of(), List.of()),
                integrationTestsInitData.createTeacherTwo(null, List.of(), List.of())));
        //when
        URI url = createURL("/api/students/" + student.getId());
        restTemplate.delete(url);
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isNotPresent();
        student.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
    }

    @Test
    void shouldDeleteAllStudents() throws URISyntaxException {
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of(
                integrationTestsInitData.createTeacherOne(null, List.of(), List.of()),
                integrationTestsInitData.createTeacherTwo(null, List.of(), List.of())));
        Student student2 = integrationTestsInitData.createStudentOne(null, List.of(
                integrationTestsInitData.createTeacherOne(null, List.of(), List.of()),
                integrationTestsInitData.createTeacherTwo(null, List.of(), List.of())));
        //when
        URI url = createURL("/api/students");
        restTemplate.delete(url);
        //then
        Optional<Student> byId1 = studentRepository.findById(student1.getId());
        Optional<Student> byId2 = studentRepository.findById(student1.getId());
        assertThat(byId1).isNotPresent();
        assertThat(byId2).isNotPresent();
        student1.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
        student2.getTeachers().forEach(teacher ->
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed.")));
    }

    private StudentDto createStudentDto(List<Teacher> teachers) {
        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("Weronika");
        studentDto.setLastName("Romanski");
        studentDto.setEmail("w.romanski@gmail.com");
        studentDto.setAge(21);
        studentDto.setTeachers(new HashSet<>(teachers));
        return studentDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}