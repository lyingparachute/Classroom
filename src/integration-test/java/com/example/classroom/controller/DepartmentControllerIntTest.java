package com.example.classroom.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration")
@SpringBootTest
@Transactional
class DepartmentControllerIntTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private IntegrationTestsInitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldCreateDepartment() throws Exception {
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
        DepartmentDto expected = createDepartmentDto(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        //when
        this.mockMvc.perform(post("/dashboard/departments/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("name=" + expected.getName() +
                                "&address=" + expected.getAddress() +
                                "&telNumber=" + expected.getTelNumber() +
                                "&dean=" + dean.getId() +
                                "&fieldsOfStudy=" + fieldOfStudy1.getId() +
                                "&_fieldsOfStudy=on" +
                                "&fieldsOfStudy=" + fieldOfStudy2.getId() +
                                "&_fieldsOfStudy=on" +
                                "&add="))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        //then
        Optional<Department> byId = departmentRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        Department actual = byId.get();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department's properties",
                () -> assertThat(actual.getName())
                        .as("Check %s's %s", "department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s's %s", "department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s's %s", "department", "Phone Number").isEqualTo(expected.getTelNumber()),
                () -> assertThat(actual.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean),
                () -> assertThat(actual.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department", "fieldsOfStudy")
                        .contains(fieldOfStudy1, fieldOfStudy2)
        );
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
    void shouldEditDepartment() throws Exception {
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
        Department department = initData.createDepartmentOne(null, List.of());

        DepartmentDto expected = new DepartmentDto();
        expected.setId(department.getId());
        expected.setName("Wydział Najlepszy");
        expected.setAddress("ul. Kasztanowa 68, 22-098 Kasztanowo");
        expected.setTelNumber("321321321");
        //when
        this.mockMvc.perform(post("/dashboard/departments/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("id=" + department.getId() +
                                "&name=" + expected.getName() +
                                "&address=" + expected.getAddress() +
                                "&telNumber=" + expected.getTelNumber() +
                                "&dean=" + dean.getId() +
                                "&fieldsOfStudy=" + fieldOfStudy1.getId() +
                                "&_fieldsOfStudy=on" +
                                "&fieldsOfStudy=" + fieldOfStudy2.getId() +
                                "&_fieldsOfStudy=on" +
                                "&add="))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        //then
        Optional<Department> byId = departmentRepository.findById(department.getId());
        assertThat(byId).isPresent();
        Department actual = byId.get();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department's properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Department", "ID").isNotNull(),
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
    void shouldDeleteDepartment() throws Exception {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));

        //when
        this.mockMvc.perform(get("/dashboard/departments/delete/" + expected.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
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

    private DepartmentDto createDepartmentDto(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        DepartmentDto dto = new DepartmentDto();
        dto.setName("Wydział Najlepszy");
        dto.setAddress("ul. Kasztanowa 68, 22-098 Kasztanowo");
        dto.setTelNumber("321321321");
        dto.setDean(dean);
        dto.setFieldsOfStudy(new HashSet<>(fieldsOfStudy));
        return dto;
    }
}