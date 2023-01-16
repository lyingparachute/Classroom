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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    DepartmentRepository repository;

    @InjectMocks
    DepartmentService service;

    @Spy
    ModelMapper mapper;

    @Spy
    UnitTestsInitData initData;

    @Captor
    private ArgumentCaptor<List<Department>> departmentListArgumentCaptor;

    @Captor
    private ArgumentCaptor<Department> departmentArgumentCaptor;

    @Test
    void create_shouldSaveDepartment_givenDto() {
        //given
        Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        DepartmentDto dto = mapper.map(expected, DepartmentDto.class);
        //when
        when(repository.save(expected)).thenReturn(expected);
        service.create(dto);
        //then
        verify(repository).save(departmentArgumentCaptor.capture());
        Department actual = departmentArgumentCaptor.getValue();
        assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
        assertAll("Department's properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Department", "ID").isEqualTo(expected.getId()),
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
        assertThat(dean.getDepartmentDean()).as("Check if %s is not null", "Dean's Department").isNotNull();
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

    @Nested
    class updateDepartmentTest {
        @Test
        void update_shouldUpdateDepartment_givenDto() {
            //given
            Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher dean = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Department entityBeforeUpdate = initData.createDepartmentOne(dean1, List.of());
            Department expected = new Department();
            expected.setId(entityBeforeUpdate.getId());
            expected.setName("Wydział Chemiczny");
            expected.setAddress("ul. Broniewicza 115, 00-245 Kęty");
            expected.setTelNumber(987654321);
            expected.setDean(dean);
            expected.addFieldOfStudy(fieldOfStudy1);
            expected.addFieldOfStudy(fieldOfStudy2);
            DepartmentDto dto = mapper.map(expected, DepartmentDto.class);

            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(entityBeforeUpdate));
            when(repository.save(any(Department.class))).thenReturn(expected);
            service.update(dto);
            //then
            verify(repository).findById(anyLong());
            verify(repository).save(departmentArgumentCaptor.capture());
            Department actual = departmentArgumentCaptor.getValue();

            assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
            assertAll("Department's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Department", "ID").isEqualTo(expected.getId()),
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
            assertThat(dean.getDepartmentDean()).as("Check if %s is not null", "Dean's Department").isNotNull();
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
        void update_throwsIllegalArgumentException_givenWrongDto() {
            //given
            Department department = initData.createDepartmentOne(null, List.of());
            DepartmentDto dto = mapper.map(department, DepartmentDto.class);
            dto.setId(5L);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Department '" + dto.getName() + "' with ID: " + dto.getId());
        }
    }

    @Test
    void fetchAll() {
    }

    @Test
    void fetchAllPaginated() {
    }

    @Nested
    class FindDepartmentByIdTest {
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