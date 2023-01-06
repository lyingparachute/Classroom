package com.example.classroom.rest;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.StudentRepository;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeacherRestControllerTest {
    @Autowired
    private InitData initData;

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
        initData.cleanUp();
    }

    @Test
    void shouldGetTeacher() throws URISyntaxException {
        //given
        Student studentOne = initData.createStudentOne(List.of());
        Student studentTwo = initData.createStudentTwo(List.of());
        Teacher teacher = initData.createTeacherOne(List.of(studentOne, studentTwo));

        //when
        URI url = createURL("/api/teachers/" + teacher.getId());
        ResponseEntity<TeacherDto> response = restTemplate.getForEntity(url, TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(teacher.getId());
        assertThat(actual.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacher.getAge());
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(studentOne.getId(), studentOne.getFirstName(), studentOne.getLastName(),
                                studentOne.getEmail(), studentOne.getAge(), studentOne.getFieldOfStudy()),
                        Tuple.tuple(studentTwo.getId(), studentTwo.getFirstName(), studentTwo.getLastName(),
                                studentTwo.getEmail(), studentTwo.getAge(), studentTwo.getFieldOfStudy())
                );
    }

    @Test
    void shouldGetAllTeachers() throws URISyntaxException {
        //given
        initData.createTeacherOne(List.of(initData.createStudentOne(List.of())));
        initData.createTeacherTwo(List.of(initData.createStudentTwo(List.of())));
        //when
        URI url = createURL("/api/teachers/");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual).isNotEmpty();
        assertThat(actual).size().isEqualTo(2);
    }

    @Test
    void shouldCreateTeacher() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());

        TeacherDto teacherDto = createTeacherDto(student1, student2);
        //when
        URI url = createURL("/api/teachers/create");
        ResponseEntity<TeacherDto> response = restTemplate.postForEntity(url, teacherDto, TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Teacher with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
        assertThat(actual.getStudentsList()).size().isEqualTo(2);
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(), student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getEmail(), student2.getAge(), student2.getFieldOfStudy())
                );
    }

    @Test
    void shouldUpdateTeacher() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());

        Teacher teacherEntity = initData.createTeacherOne(new ArrayList<>());
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(teacherEntity.getId());
        teacherDto.setFirstName("Lionel");
        teacherDto.setLastName("Messi");
        teacherDto.setEmail("l.messi@gmail.com");
        teacherDto.setAge(35);
        teacherDto.setStudentsList(new HashSet<>(List.of(student1, student2)));

        //when
        URI url = createURL("/api/teachers/");
        final HttpEntity<TeacherDto> requestUpdate = new HttpEntity<>(teacherDto);
        ResponseEntity<TeacherDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestUpdate, TeacherDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Teacher with ID= " + actual.getId() + " should not be removed"));
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy,
                        s -> s.getTeachersList().size()
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy(), student1.getTeachersList().size()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy(), student2.getTeachersList().size())
                );
    }

    @Test
    void shouldDeleteTeacher() throws URISyntaxException {
        //given
        Teacher teacher = initData.createTeacherOne(List.of(
                initData.createStudentOne(List.of()),
                initData.createStudentTwo(List.of())));
        //when
        URI url = createURL("/api/teachers/" + teacher.getId());
        restTemplate.delete(url);
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isNotPresent();
        teacher.getStudentsList().forEach(i -> {
            studentRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Student with ID= " + i.getId() + " should not be removed."));
        });
    }

    private TeacherDto createTeacherDto(Student studentOne, Student studentTwo) {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setFirstName("Jagoda");
        teacherDto.setLastName("Kowalska");
        teacherDto.setEmail("j.kowalska@gmail.com");
        teacherDto.setAge(33);
        HashSet<Student> students = new HashSet<>();
        students.add(studentOne);
        students.add(studentTwo);
        teacherDto.setStudentsList(students);
        return teacherDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }

}