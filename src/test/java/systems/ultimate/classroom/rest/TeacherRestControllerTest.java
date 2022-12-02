package systems.ultimate.classroom.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

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
    void shouldGetAllTeachers() {

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
}