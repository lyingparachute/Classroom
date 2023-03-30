package com.example.classroom.api;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Student;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.StudentRepository;
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
class StudentRestControllerIntegrationTest {

    @Autowired
    private IntegrationTestsInitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    StudentRepository studentRepository;

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
    void shouldGetStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/students/" + expected.getId());
        ResponseEntity<StudentDto> response = restTemplate.getForEntity(url, StudentDto.class);
        //then
        Optional<Student> byId = studentRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
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
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Student expectedStudent1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Student expectedStudent2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher2, teacher3));
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
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        StudentDto expected = createStudentDto(fieldOfStudy, List.of(teacher1, teacher2));
        Student expectedEntity = mapper.map(expected, Student.class);
        //when
        URI url = createURL("/api/students");
        ResponseEntity<StudentDto> response = restTemplate.postForEntity(url, expected, StudentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        StudentDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Student").isNotNull();
        assertThat(actual.getId()).as("Check %s's %s", "Student", "ID").isNotNull();
        studentRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Student with ID= " + actual.getId() + " should not be missing"));
        assertAll("Student's properties",
                () -> assertThat(actual.getFirstName())
                        .as("Check %s's %s", "Student", "First Name").isEqualTo(expected.getFirstName()),
                () -> assertThat(actual.getLastName())
                        .as("Check %s's %s", "Student", "Last Name").isEqualTo(expected.getLastName()),
                () -> assertThat(actual.getEmail())
                        .as("Check %s's %s", "Student", "Email").isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getAge())
                        .as("Check %s's %s", "Student", "Age").isEqualTo(expected.getAge())
        );
        assertThat(actual.getFieldOfStudy()).as("Check if %s is not null", "Student's fieldOfStudy").isNotNull();
        assertAll("Student's fieldOfStudy properties",
                () -> assertThat(actual.getFieldOfStudy().getId())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                () -> assertThat(actual.getFieldOfStudy().getName())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                () -> assertThat(actual.getFieldOfStudy().getMode())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                () -> assertThat(actual.getFieldOfStudy().getTitle())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                () -> assertThat(actual.getFieldOfStudy().getStudents())
                        .as("Check %s's %s properties", "fieldOfStudy", "students")
                        .extracting(
                                Student::getId,
                                Student::getFirstName,
                                Student::getLastName,
                                Student::getEmail,
                                Student::getAge,
                                Student::getFieldOfStudy
                        ).containsExactlyInAnyOrder(
                                Tuple.tuple(expected.getId(), expected.getFirstName(), expected.getLastName(),
                                        expected.getEmail(), expected.getAge(), fieldOfStudy))
        );
        assertThat(actual.getTeachers()).as("Check %s's %s properties", "Student", "teachers")
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
        assertThat(teacher1.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher1", "students")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(teacher2.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher2", "students")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
    }

    @Test
    void shouldUpdateStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Student entity = initData.createStudentOne(null, List.of());
        StudentDto expected = createStudentDto(fieldOfStudy, List.of(teacher1, teacher2));
        expected.setId(entity.getId());
        Student expectedEntity = mapper.map(expected, Student.class);
        //when
        URI url = createURL("/api/students/");
        final HttpEntity<StudentDto> request = new HttpEntity<>(expected);
        ResponseEntity<StudentDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, StudentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Student").isNotNull();
        assertThat(actual.getId()).as("Check %s's %s", "Student", "ID").isNotNull();
        studentRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Student with ID= " + actual.getId() + " should not be missing"));
        assertAll("Student's properties",
                () -> assertThat(actual.getFirstName())
                        .as("Check %s's %s", "Student", "First Name").isEqualTo(expected.getFirstName()),
                () -> assertThat(actual.getLastName())
                        .as("Check %s's %s", "Student", "Last Name").isEqualTo(expected.getLastName()),
                () -> assertThat(actual.getEmail())
                        .as("Check %s's %s", "Student", "Email").isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getAge())
                        .as("Check %s's %s", "Student", "Age").isEqualTo(expected.getAge())
        );
        assertThat(actual.getFieldOfStudy()).as("Check if %s is not null", "Student's fieldOfStudy").isNotNull();
        assertAll("Student's fieldOfStudy properties",
                () -> assertThat(actual.getFieldOfStudy().getId())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                () -> assertThat(actual.getFieldOfStudy().getName())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                () -> assertThat(actual.getFieldOfStudy().getMode())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                () -> assertThat(actual.getFieldOfStudy().getTitle())
                        .as("Check %s's %s %s", "Student", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                () -> assertThat(actual.getFieldOfStudy().getStudents())
                        .as("Check %s's %s properties", "fieldOfStudy", "students")
                        .extracting(
                                Student::getId,
                                Student::getFirstName,
                                Student::getLastName,
                                Student::getEmail,
                                Student::getAge,
                                Student::getFieldOfStudy
                        ).containsExactlyInAnyOrder(
                                Tuple.tuple(expected.getId(), expected.getFirstName(), expected.getLastName(),
                                        expected.getEmail(), expected.getAge(), fieldOfStudy))
        );
        assertThat(actual.getTeachers()).as("Check %s's %s properties", "Student", "teachers")
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
        assertThat(teacher1.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher1", "students")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(teacher2.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher2", "students")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
    }

    @Test
    void shouldDeleteStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/students/" + expected.getId());
        restTemplate.delete(url);
        //then
        Optional<Student> byId = studentRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy.getId() + " and name " +
                        fieldOfStudy.getName() + " should not be removed."));
        expected.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
    }

    @Test
    void shouldDeleteAllStudents() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

        Student expected1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Student expected2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher1, teacher3));

        //when
        URI url = createURL("/api/students");
        restTemplate.delete(url);
        //then
        Optional<Student> byId1 = studentRepository.findById(expected1.getId());
        assertThat(byId1).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy1.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy1.getId() + " and name " +
                        fieldOfStudy1.getName() + " should not be removed."));
        expected1.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
        Optional<Student> byId2 = studentRepository.findById(expected1.getId());
        assertThat(byId2).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy2.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy2.getId() + " and name " +
                        fieldOfStudy2.getName() + " should not be removed."));
        expected2.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " should not be removed.")));
    }

    private StudentDto createStudentDto(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        StudentDto dto = new StudentDto();
        dto.setFirstName("Pamela");
        dto.setLastName("Gonzales");
        dto.setEmail("p.gonzales@gmail.com");
        dto.setAge(20);
        dto.setFieldOfStudy(fieldOfStudy);
        dto.setTeachers(new HashSet<>(teachers));
        return dto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}