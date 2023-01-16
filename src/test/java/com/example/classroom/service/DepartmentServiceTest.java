package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.util.UnitTestsInitData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    DepartmentService departmentService;

    @Spy
    ModelMapper mapper;

    @Spy
    UnitTestsInitData initData;

    @Captor
    private ArgumentCaptor<List<Department>> argCaptor;

    @Test
    void create_shouldSaveDepartment_givenDto() {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        DepartmentDto dto = mapper.map(expected, DepartmentDto.class);
        //when
        when(departmentRepository.save(expected)).thenReturn(expected);
        departmentService.create(dto);
        //then
        ArgumentCaptor<Department> departmentArgumentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).save(departmentArgumentCaptor.capture());
        Department actual = departmentArgumentCaptor.getValue();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department's properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Department", "ID").isEqualTo(expected.getId()),
                () -> assertThat(actual.getName())
                        .as("Check %s's %s", "Department", "Name").isEqualTo(dto.getName()),
                () -> assertThat(actual.getAddress())
                        .as("Check %s's %s", "Department", "Address").isEqualTo(dto.getAddress()),
                () -> assertThat(actual.getTelNumber())
                        .as("Check %s's %s", "Department", "Telephone Number").isEqualTo(dto.getTelNumber())
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
        assertAll("Dean's Department properties",
                () -> assertThat(dean.getDepartmentDean().getId())
                        .as("Check %s's %s %s", "Dean", "Department", "Id").isEqualTo(expected.getId()),
                () -> assertThat(dean.getDepartmentDean().getName())
                        .as("Check %s's %s %s", "Dean", "Department", "Name").isEqualTo(expected.getName()),
                () -> assertThat(dean.getDepartmentDean().getAddress())
                        .as("Check %s's %s %s", "Dean", "Department", "Address").isEqualTo(expected.getAddress()),
                () -> assertThat(dean.getDepartmentDean().getTelNumber())
                        .as("Check %s's %s %s", "Dean", "Department", "Telephone Number").isEqualTo(expected.getTelNumber())
        );
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
    void update() {
    }

    @Test
    void fetchAll() {
    }

    @Test
    void fetchAllPaginated() {
    }

    @Nested
    class FindCoffeeUserByIdTest {
        @Test
        void fetchById_shouldReturnDepartment_givenCorrectId() {
        }

        @Test
        void fetchById_shouldThrowIllegalStateException_givenIncorrectId() {
        }
    }

    @Test
    void remove() {
    }

    @Test
    void findByName() {
    }

    @Test
    void findByNamePaginated() {
    }

    @Test
    void removeAll() {
    }
}