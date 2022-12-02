package systems.ultimate.classroom.rest;

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
import systems.ultimate.classroom.enums.FieldOfStudy;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.util.InitData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

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

    @BeforeEach
    public void cleanUp() {
        initData.cleanUp();
    }

    @Test
    void shouldGetStudent() throws URISyntaxException {
        //given
        Student student = initData.createFirstStudent();
        //when
        URI url = createURL("/api/students/" + student.getId());
        ResponseEntity<StudentDto> response = restTemplate.getForEntity(url, StudentDto.class);
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
        assertThat(actual.getTeachersList()).isEmpty();
    }

    @Test
    void shouldCreateStudent() throws URISyntaxException {
        //given
        StudentDto studentDto = createStudentDto();
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
        assertThat(actual.getTeachersList()).isNull();
    }

    @Test
    void shouldUpdateStudent() throws URISyntaxException {
        //given
        Student student = initData.createFirstStudent();
        StudentDto studentDto = createStudentDto();
        studentDto.setId(student.getId());

        //when
        URI url = createURL("/api/students/");
        final HttpEntity<StudentDto> request = new HttpEntity<>(studentDto);
        ResponseEntity<StudentDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, StudentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
        assertThat(actual.getTeachersList()).isEqualTo(studentDto.getTeachersList());
    }

    private static StudentDto createStudentDto() {
        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("Weronika");
        studentDto.setLastName("Romanski");
        studentDto.setEmail("w.romanski@gmail.com");
        studentDto.setAge(21);
        studentDto.setFieldOfStudy(FieldOfStudy.ELECTRICAL);
        return studentDto;
    }

    @Test
    void shouldDeleteStudent() throws URISyntaxException {
        //given
        Student student = initData.createFirstStudent();
        //when
        URI url = createURL("/api/students/" + student.getId());
        restTemplate.delete(url);
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isNotPresent();
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}