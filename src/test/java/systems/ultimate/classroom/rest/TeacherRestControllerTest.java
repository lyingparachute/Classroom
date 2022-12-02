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
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.Subject;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

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
    TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetTeacher() throws URISyntaxException {
        //given
        Teacher teacher = initData.createFirstTeacher();
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
        assertThat(actual.getSubject()).isEqualTo(teacher.getSubject());
        assertThat(actual.getStudentsList()).isEmpty();
    }

    @Test
    void shouldGetAllTeachers() throws URISyntaxException {
        //given
        initData.createFirstTeacher();
        initData.createSecondTeacher();
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
        TeacherDto teacherDto = createTeacherDto();
        //when
        URI url = createURL("/api/teachers/create");
        ResponseEntity<TeacherDto> response = restTemplate.postForEntity(url, teacherDto ,TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Student with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
        assertThat(actual.getSubject()).isEqualTo(teacherDto.getSubject());
        assertThat(actual.getStudentsList()).isNull();
    }

    @Test
    void shouldUpdateTeacher() throws URISyntaxException {
        //given
        Teacher teacher = initData.createFirstTeacher();
        TeacherDto teacherDto = createTeacherDto();
        teacherDto.setId(teacher.getId());
        //when
        URI url = createURL("/api/teachers/");
        HttpEntity<TeacherDto> requestUpdate = new HttpEntity<>(teacherDto);
        ResponseEntity<TeacherDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestUpdate ,TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Student with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
        assertThat(actual.getSubject()).isEqualTo(teacherDto.getSubject());
        assertThat(actual.getStudentsList()).isEqualTo(teacherDto.getStudentsList());
    }

    @Test
    void shouldDeleteTeacher() throws URISyntaxException {
        //given
        Teacher teacher = initData.createFirstTeacher();
        //when
        URI url = createURL("/api/teachers/" + teacher.getId());
        restTemplate.delete(url);
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isNotPresent();
    }

    private TeacherDto createTeacherDto(){
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setFirstName("Jagoda");
        teacherDto.setLastName("Kowalska");
        teacherDto.setEmail("j.kowalska@gmail.com");
        teacherDto.setAge(33);
        teacherDto.setSubject(Subject.SCIENCE);
        return teacherDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }

}