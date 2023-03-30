package com.example.classroom.api;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.model.Department;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepartmentRestControllerIntegrationTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private IntegrationTestsInitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetDepartment() throws URISyntaxException {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        //when
        URI url = createURL("/api/departments/" + expected.getId());
        ResponseEntity<DepartmentDto> response = restTemplate.getForEntity(url, DepartmentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department properties",
                () -> assertThat(actual.getId())
                        .as("Check %s %s", "department", "ID").isEqualTo(expected.getId()),
                () -> assertThat(actual.getName())
                        .as("Check %s %s", "department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s %s", "department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getTelNumber()),
                () -> assertThat(actual.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean),
                () -> assertThat(actual.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department", "fieldsOfStudy")
                        .contains(fieldOfStudy1, fieldOfStudy2)
        );
    }

    @Test
    void shouldGetAllDepartments() throws URISyntaxException {
        //given
        Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher dean2 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
        initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
        //when
        URI url = createURL("/api/departments");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    void shouldCreateDepartment() throws URISyntaxException {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        DepartmentDto expected = createDepartmentDto(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        //when
        URI url = createURL("/api/departments");
        ResponseEntity<DepartmentDto> response = restTemplate.postForEntity(url, expected, DepartmentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        DepartmentDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department properties",
                () -> assertThat(actual.getName())
                        .as("Check %s %s", "department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s %s", "department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getTelNumber()),
                () -> assertThat(actual.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean),
                () -> assertThat(actual.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department", "fieldsOfStudy")
                        .contains(fieldOfStudy1, fieldOfStudy2)
        );
        assertAll("Department's properties",
                () -> assertThat(actual.getName())
                        .as("Check %s's %s", "Department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s's %s", "Department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s's %s", "Department", "Telephone Number").isEqualTo(expected.getTelNumber())
        );
        assertThat(actual.getDean()).as("Check if %s is not null", "Department's Dean").isNotNull();
        assertAll("Department's Dean properties",
                () -> assertThat(actual.getDean().getId())
                        .as("Check %s's %s %s", "Department", "Dean", "Id").isEqualTo(dean.getId()),
                () -> assertThat(actual.getDean().getFirstName())
                        .as("Check %s's %s %s", "Department", "Dean", "First Name").isEqualTo(dean.getFirstName()),
                () -> assertThat(actual.getDean().getLastName())
                        .as("Check %s's %s %s", "Department", "Dean", "Last Name").isEqualTo(dean.getLastName()),
                () -> assertThat(actual.getDean().getAge())
                        .as("Check %s's %s %s", "Department", "Dean", "Age").isEqualTo(dean.getAge()),
                () -> assertThat(actual.getDean().getEmail())
                        .as("Check %s's %s %s", "Department", "Dean", "Email").isEqualTo(dean.getEmail())
        );
        assertThat(dean.getDepartment()).as("Check if %s is not null", "Dean's Department").isNotNull();
        assertAll("Dean's Department properties",
                () -> assertThat(dean.getDepartment().getId())
                        .as("Check %s's %s %s", "Dean", "Department", "Id").isEqualTo(expected.getId()),
                () -> assertThat(dean.getDepartment().getName())
                        .as("Check %s's %s %s", "Dean", "Department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(dean.getDepartment().getAddress())
                        .as("Check %s's %s %s", "Dean", "Department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(dean.getDepartment().getTelNumber())
                        .as("Check %s's %s %s", "Dean", "Department", "Telephone Number").isEqualTo(expected.getTelNumber())
        );
        assertThat(actual.getFieldsOfStudy()).isNotNull().isNotEmpty().hasSize(2);
        assertThat(fieldOfStudy1.getDepartment()).as("Check if %s is not null", "fieldOfStudy1's department").isNotNull();
        assertThat(fieldOfStudy1.getDepartment()).as("Check if %s is not null", "fieldOfStudy1's department").isNotNull();
        assertThat(actual.getFieldsOfStudy()).as("Check %s's %s properties", "Department", "Fields Of Study")
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle,
                        FieldOfStudy::getLevelOfEducation,
                        fieldOfStudy -> fieldOfStudy.getDepartment().getId(),
                        fieldOfStudy -> fieldOfStudy.getDepartment().getName(),
                        fieldOfStudy -> fieldOfStudy.getDepartment().getAddress(),
                        fieldOfStudy -> fieldOfStudy.getDepartment().getTelNumber()
                ).containsExactlyInAnyOrder(
                        tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getMode(),
                                fieldOfStudy1.getTitle(), fieldOfStudy1.getLevelOfEducation(),
                                expected.getId(), expected.getName(), expected.getAddress(), expected.getTelNumber()),
                        tuple(fieldOfStudy2.getId(), fieldOfStudy2.getName(), fieldOfStudy2.getMode(),
                                fieldOfStudy2.getTitle(), fieldOfStudy2.getLevelOfEducation(),
                                expected.getId(), expected.getName(), expected.getAddress(), expected.getTelNumber()));
    }

    @Test
    void shouldUpdateDepartment() throws URISyntaxException {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department entity = initData.createDepartmentOne(null, List.of());
        DepartmentDto expected = createDepartmentDto(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        expected.setId(entity.getId());
        //when
        URI url = createURL("/api/departments");
        HttpEntity<DepartmentDto> requestUpdate = new HttpEntity<>(expected);
        ResponseEntity<DepartmentDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestUpdate, DepartmentDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Department", "ID").isNotNull(),
                () -> assertThat(actual.getName())
                        .as("Check %s %s", "department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s %s", "department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getTelNumber()),
                () -> assertThat(actual.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean),
                () -> assertThat(actual.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department", "fieldsOfStudy")
                        .contains(fieldOfStudy1, fieldOfStudy2)
        );
        assertAll("Department's properties",
                () -> assertThat(actual.getName())
                        .as("Check %s's %s", "Department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s's %s", "Department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s's %s", "Department", "Telephone Number").isEqualTo(expected.getTelNumber())
        );
        assertThat(actual.getDean()).as("Check if %s is not null", "Department's Dean").isNotNull();
        assertAll("Department's Dean properties",
                () -> assertThat(actual.getDean().getId())
                        .as("Check %s's %s %s", "Department", "Dean", "Id").isEqualTo(dean.getId()),
                () -> assertThat(actual.getDean().getFirstName())
                        .as("Check %s's %s %s", "Department", "Dean", "First Name").isEqualTo(dean.getFirstName()),
                () -> assertThat(actual.getDean().getLastName())
                        .as("Check %s's %s %s", "Department", "Dean", "Last Name").isEqualTo(dean.getLastName()),
                () -> assertThat(actual.getDean().getAge())
                        .as("Check %s's %s %s", "Department", "Dean", "Age").isEqualTo(dean.getAge()),
                () -> assertThat(actual.getDean().getEmail())
                        .as("Check %s's %s %s", "Department", "Dean", "Email").isEqualTo(dean.getEmail())
        );
        assertThat(dean.getDepartment()).as("Check if %s is not null", "Dean's Department").isNotNull();
        assertAll("Dean's Department properties",
                () -> assertThat(dean.getDepartment().getId())
                        .as("Check %s's %s %s", "Dean", "Department", "Id").isEqualTo(expected.getId()),
                () -> assertThat(dean.getDepartment().getName())
                        .as("Check %s's %s %s", "Dean", "Department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(dean.getDepartment().getAddress())
                        .as("Check %s's %s %s", "Dean", "Department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(dean.getDepartment().getTelNumber())
                        .as("Check %s's %s %s", "Dean", "Department", "Telephone Number").isEqualTo(expected.getTelNumber())
        );
        assertThat(actual.getFieldsOfStudy()).isNotNull().isNotEmpty().hasSize(2);
        assertThat(fieldOfStudy1.getDepartment()).as("Check if %s is not null", "fieldOfStudy1's department").isNotNull();
        assertThat(fieldOfStudy1.getDepartment()).as("Check if %s is not null", "fieldOfStudy1's department").isNotNull();
        assertThat(actual.getFieldsOfStudy()).as("Check %s's %s properties", "Department", "Fields Of Study")
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle,
                        FieldOfStudy::getLevelOfEducation,
                        fieldOfStudy -> fieldOfStudy.getDepartment().getId(),
                        fieldOfStudy -> fieldOfStudy.getDepartment().getName(),
                        fieldOfStudy -> fieldOfStudy.getDepartment().getAddress(),
                        fieldOfStudy -> fieldOfStudy.getDepartment().getTelNumber()
                ).containsExactlyInAnyOrder(
                        tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getMode(),
                                fieldOfStudy1.getTitle(), fieldOfStudy1.getLevelOfEducation(),
                                expected.getId(), expected.getName(), expected.getAddress(), expected.getTelNumber()),
                        tuple(fieldOfStudy2.getId(), fieldOfStudy2.getName(), fieldOfStudy2.getMode(),
                                fieldOfStudy2.getTitle(), fieldOfStudy2.getLevelOfEducation(),
                                expected.getId(), expected.getName(), expected.getAddress(), expected.getTelNumber()));


    }

    @Test
    void shouldDeleteDepartment() throws URISyntaxException {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        //when
        URI url = createURL("/api/departments/" + expected.getId());
        restTemplate.delete(url);
        //then
        Optional<Department> byId = departmentRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        teacherRepository.findById(dean.getId()).orElseThrow(() -> new IllegalStateException(
                "Teacher with ID = " + dean.getId() + " and name " +
                        dean.getFirstName() + " " + dean.getLastName() + " should not be removed."));
        expected.getFieldsOfStudy().forEach(fieldOfStudy ->
                fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                        "FieldOfStudy with ID= " + fieldOfStudy.getId() + " and name " +
                                fieldOfStudy.getName() + " should not be removed.")));
    }

    @Test
    void shouldDeleteAllDepartments() throws URISyntaxException {
        //given
        Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher dean2 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
        Department expected2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
        //when
        URI url = createURL("/api/departments");
        restTemplate.delete(url);
        //then
        Optional<Department> byId1 = departmentRepository.findById(expected1.getId());
        assertThat(byId1).isNotPresent();
        teacherRepository.findById(dean1.getId()).orElseThrow(() -> new IllegalStateException(
                "Teacher with ID = " + dean1.getId() + " and name " +
                        dean1.getFirstName() + " " + dean1.getLastName() + " should not be removed."));
        expected1.getFieldsOfStudy().forEach(fieldOfStudy ->
                fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                        "FieldOfStudy with ID= " + fieldOfStudy.getId() + " and name " +
                                fieldOfStudy.getName() + " should not be removed.")));
        Optional<Department> byId2 = departmentRepository.findById(expected2.getId());
        assertThat(byId2).isNotPresent();
        teacherRepository.findById(dean2.getId()).orElseThrow(() -> new IllegalStateException(
                "Teacher with ID = " + dean2.getId() + " and name " +
                        dean2.getFirstName() + " " + dean2.getLastName() + " should not be removed."));
        expected2.getFieldsOfStudy().forEach(fieldOfStudy ->
                fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                        "FieldOfStudy with ID= " + fieldOfStudy.getId() + " and name " +
                                fieldOfStudy.getName() + " should not be removed.")));
    }

    private DepartmentDto createDepartmentDto(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        DepartmentDto dto = new DepartmentDto();
        dto.setName("Wydział Najlepszy");
        dto.setAddress("ul. Kasztanowa 68, 22-098 Kasztanowo");
        dto.setTelNumber("321321321");
        dto.setDean(dean);
        dto.setFieldsOfStudy(new HashSet<>(fieldsOfStudy));
        return dto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}