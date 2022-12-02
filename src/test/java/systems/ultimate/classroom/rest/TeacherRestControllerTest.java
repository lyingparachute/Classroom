package systems.ultimate.classroom.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

import java.net.URI;
import java.net.URISyntaxException;
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
    void shouldGetTeacher() {
    }

    @Test
    void shouldCreateTeacher() {
    }

    @Test
    void shouldUpdateTeacher() {
    }

    @Test
    void shouldDeleteTeacher() {
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }

}