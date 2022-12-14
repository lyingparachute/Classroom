package systems.ultimate.classroom.rest;

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
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentRestControllerTest {

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
    void shouldGetStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student student = initData.createStudentOne(List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/students/" + student.getId());
        ResponseEntity<StudentDto> response = restTemplate.getForEntity(url, StudentDto.class);
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isPresent();
        List<Teacher> all = teacherRepository.findAll();
        Set<Teacher> student1 = byId.get().getTeachersList();
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(student.getId());
        assertThat(actual.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(student.getLastName());
        assertThat(actual.getEmail()).isEqualTo(student.getEmail());
        assertThat(actual.getAge()).isEqualTo(student.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(student.getFieldOfStudy());
        assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
    }

    @Test
    void shouldGetAllStudents() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());

        Student student1 = initData.createStudentOne(List.of(teacher1));
        Student student2 = initData.createStudentTwo(List.of(teacher2));
        //when
        URI url = createURL("/api/students/");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual).isNotEmpty();
        assertThat(actual).size().isEqualTo(2);
       }

    @Test
    void shouldCreateStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        StudentDto studentDto = createStudentDto(List.of(teacher1, teacher2));
        //when
        URI url = createURL("/api/students/create");
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
        assertThat(actual.getFieldOfStudy()).isEqualTo(studentDto.getFieldOfStudy());
        assertThat(actual.getTeachersList()).size().isEqualTo(2);
        assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
    }

    @Test
    void shouldUpdateStudent() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student studentEntity = initData.createStudentOne(List.of());
        StudentDto studentDto = new StudentDto();
        studentDto.setId(studentEntity.getId());
        studentDto.setFirstName("Pamela");
        studentDto.setLastName("Gonzales");
        studentDto.setEmail("p.gonzales@gmail.com");
        studentDto.setAge(20);
        studentDto.setFieldOfStudy(FieldOfStudy.ROBOTICS);
        studentDto.setTeachersList(new HashSet<>(List.of(teacher1, teacher2)));

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
        assertThat(actual.getFieldOfStudy()).isEqualTo(studentDto.getFieldOfStudy());
        assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
    }

    @Test
    void shouldDeleteStudent() throws URISyntaxException {
        //given
        Student student = initData.createStudentOne(List.of(
                initData.createTeacherOne(List.of()),
                initData.createTeacherTwo(List.of())));
        //when
        URI url = createURL("/api/students/" + student.getId());
        restTemplate.delete(url);
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isNotPresent();
        student.getTeachersList().forEach(i -> {
            teacherRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + i.getId() + " should not be removed."));
        });
    }

    private StudentDto createStudentDto(List<Teacher> teachers) {
        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("Weronika");
        studentDto.setLastName("Romanski");
        studentDto.setEmail("w.romanski@gmail.com");
        studentDto.setAge(21);
        studentDto.setFieldOfStudy(FieldOfStudy.ELECTRICAL);
        studentDto.setTeachersList(new HashSet<Teacher>(teachers));
        return studentDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}