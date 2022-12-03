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
        Long idOne = initData.createStudentOne().getId();
        StudentDto dto1 = createStudentDtoOne();
        dto1.setId(idOne);
        Long idTwo = initData.createStudentTwo().getId();
        StudentDto dto2 = createStudentDtoTwo();
        dto2.setId(idTwo);

        TeacherDto teacherDto = createTeacherDto(dto1, dto2);
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
        assertThat(actual.getStudentsList()).size().isEqualTo(2);
        assertThat(actual.getStudentsList())
                .extracting(
                        StudentDto::getId,
                        StudentDto::getFirstName,
                        StudentDto::getLastName,
                        StudentDto::getEmail,
                        StudentDto::getAge,
                        StudentDto::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(dto1.getId(), dto1.getFirstName(), dto1.getLastName(), dto1.getEmail(), dto1.getAge(), dto1.getFieldOfStudy()),
                        Tuple.tuple(dto2.getId(), dto2.getFirstName(), dto2.getLastName(), dto2.getEmail(), dto2.getAge(), dto2.getFieldOfStudy())
                );
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

    private StudentDto createStudentDtoOne() {
        StudentDto dto = new StudentDto();
        dto.setFirstName("Maciej");
        dto.setLastName("Komaranczuk");
        dto.setEmail("m.komaranczuk@gmail.com");
        dto.setAge(25);
        dto.setFieldOfStudy(FieldOfStudy.INFORMATICS);
        return dto;
    }

    private StudentDto createStudentDtoTwo() {
        StudentDto dto = new StudentDto();
        dto.setFirstName("Weronika");
        dto.setLastName("Romanski");
        dto.setEmail("w.romanski@gmail.com");
        dto.setAge(21);
        dto.setFieldOfStudy(FieldOfStudy.ELECTRICAL);
        return dto;
    }


    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }

}