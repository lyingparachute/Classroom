package com.example.classroom.rest;

import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepartmentRestControllerIntegrationTest {
    @Autowired
    private IntegrationTestsInitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    DepartmentRepository departmentRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetAllDepartments() throws URISyntaxException {
        //given
        Teacher teacher = initData.createTeacherTwo(null, List.of(), List.of());
        initData.createDepartmentOne(teacher, List.of());
        //when
        URI url = createURL("/api/departments");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void createDepartment() throws URISyntaxException {
    }

    @Test
    void getDepartment() throws URISyntaxException {
    }

    @Test
    void putDepartment() throws URISyntaxException {
    }

    @Test
    void deleteDepartment() throws URISyntaxException {
    }

    @Test
    void deleteAllDepartments() throws URISyntaxException {
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}