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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    void create_savesDepartment_givenDepartmentDto() {
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
        void update_updatesDepartment_givenDepartmentDto() {
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
        void update_throwsIllegalArgumentException_givenWrongDepartmentDto() {
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
    void fetchAll_returnsAllDepartments() {
        //given
        Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher dean2 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Department department1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
        Department department2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
        List<Department> departments = List.of(department1, department2);
        //when
        when(repository.findAll()).thenReturn(departments);
        List<DepartmentDto> actual = service.fetchAll();
        //then
        verify(repository).findAll();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
        DepartmentDto actualItem1 = actual.get(0);
        DepartmentDto actualItem2 = actual.get(1);
        assertAll("department1 properties",
                () -> assertThat(actualItem1.getId())
                        .as("Check %s %s", "department1", "ID").isEqualTo(department1.getId()),
                () -> assertThat(actualItem1.getName())
                        .as("Check %s %s", "department1", "Name").isEqualTo(department1.getName()),
                () -> assertThat(actualItem1.getAddress())
                        .as("Check %s %s", "department1", "Address").isEqualTo(department1.getAddress()),
                () -> assertThat(actualItem1.getTelNumber())
                        .as("Check %s %s", "department1", "Telephone Number").isEqualTo(department1.getTelNumber()),
                () -> assertThat(actualItem1.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean1),
                () -> assertThat(actualItem1.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department1", "fieldOfStudy1").contains(fieldOfStudy1),
                () -> assertThat(actualItem1.getFieldsOfStudy())
                        .as("Check if %s does not contain %s", "department1", "fieldOfStudy2").doesNotContain(fieldOfStudy2)
        );
        assertAll("department2 properties",
                () -> assertThat(actualItem2.getId())
                        .as("Check %s %s", "department2", "ID").isEqualTo(department2.getId()),
                () -> assertThat(actualItem2.getName())
                        .as("Check %s %s", "department2", "Name").isEqualTo(department2.getName()),
                () -> assertThat(actualItem2.getAddress())
                        .as("Check %s %s", "department2", "Address").isEqualTo(department2.getAddress()),
                () -> assertThat(actualItem2.getTelNumber())
                        .as("Check %s %s", "department2", "Telephone Number").isEqualTo(department2.getTelNumber()),
                () -> assertThat(actualItem2.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean2),
                () -> assertThat(actualItem2.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department2", "fieldOfStudy1").contains(fieldOfStudy2),
                () -> assertThat(actualItem2.getFieldsOfStudy())
                        .as("Check if %s does not contain %s", "department2", "fieldOfStudy2").doesNotContain(fieldOfStudy1)
        );
    }

    @Test
    void fetchAllPaginated() {
        //given
        int pageNo = 2;
        int pageSize = 2;
        String sortField = "name";
        String sortDirection = Sort.Direction.DESC.name();

        Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher dean2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher dean3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

        Department department1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
        Department department2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
        Department department3 = initData.createDepartmentThree(dean3, List.of(fieldOfStudy3));
        Page<Department> departments = new PageImpl<>(List.of(department3));
        //when
        when(repository.findAll(any(PageRequest.class))).thenReturn(departments);
        Page<DepartmentDto> actualPage = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
        //then
        verify(repository).findAll(any(PageRequest.class));
        List<DepartmentDto> actualContent = actualPage.getContent();
        assertThat(actualContent).size().isEqualTo(1);
        DepartmentDto actualItem = actualContent.get(0);
        assertAll("Resulting department properties",
                () -> assertThat(actualItem.getId())
                        .as("Check %s %s", "department3", "ID").isEqualTo(department3.getId()),
                () -> assertThat(actualItem.getName())
                        .as("Check %s %s", "department3", "Name").isEqualTo(department3.getName()),
                () -> assertThat(actualItem.getAddress())
                        .as("Check %s %s", "department3", "Address").isEqualTo(department3.getAddress()),
                () -> assertThat(actualItem.getTelNumber())
                        .as("Check %s %s", "department3", "Telephone Number").isEqualTo(department3.getTelNumber()),
                () -> assertThat(actualItem.getDean())
                        .as("Check %s's %s", "Department", "Dean").isEqualTo(dean3),
                () -> assertThat(actualItem.getFieldsOfStudy())
                        .as("Check if %s contains %s", "department3", "fieldOfStudy1")
                        .contains(fieldOfStudy3).doesNotContain(fieldOfStudy1, fieldOfStudy2)
        );
    }

    @Nested
    class FindDepartmentByIdTest {
        @Test
        void fetchById_shouldReturnDepartment_givenId() {
            //given
            Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            DepartmentDto actual = service.fetchById(expected.getId());
            //then
            ArgumentCaptor<Long> idArgCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).findById(idArgCaptor.capture());
            Long actualId = idArgCaptor.getValue();

            assertThat(actual).as("Check if %s is not null", "Department").isNotNull();

            assertAll("Resulting department properties",
                    () -> assertThat(actualId)
                            .as("Check %s %s", "department3", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s %s", "department3", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getAddress())
                            .as("Check %s %s", "department3", "Address").isEqualTo(expected.getAddress()),
                    () -> assertThat(actual.getTelNumber())
                            .as("Check %s %s", "department3", "Telephone Number").isEqualTo(expected.getTelNumber()),
                    () -> assertThat(actual.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean),
                    () -> assertThat(actual.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department3", "fieldOfStudy1")
                            .contains(fieldOfStudy1, fieldOfStudy2)
            );
        }

        @Test
        void fetchById_throwsIllegalArgumentException_givenWrongId() {
            //given
            Long id = 10L;
            //when
            Throwable thrown = catchThrowable(() -> service.fetchById(id));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Department id: " + id);
        }
    }

    @Nested
    class removeDepartmentTest {
        @Test
        void remove_deletesDepartment_givenId() {
            //given
            Department expected = initData.createDepartmentOne(null, List.of());
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            service.remove(expected.getId());
            //then
            verify(repository).findById(anyLong());
            verify(repository).delete(any(Department.class));
        }

        @Test
        void remove_throwsIllegalArgumentException_givenWrongId() {
            //given
            Long id = 10L;
            //when
            Throwable thrown = catchThrowable(() -> service.remove(id));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Department id: " + id);
        }

        @Test
        void removeAll_deletesAllDepartments() {
            //given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Department department3 = initData.createDepartmentThree(null, List.of());
            List<Department> departments = List.of(department1, department2, department3);
            //when
            when(repository.findAll()).thenReturn(departments);
            service.removeAll();
            //then
            verify(repository).findAll();
            verify(repository).deleteAll();
        }
    }

    @Test
    void removeAll_deletesAllDepartments() {
        //given
        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentTwo(null, List.of());
        Department department3 = initData.createDepartmentThree(null, List.of());
        List<Department> departments = List.of(department1, department2, department3);
        //when
        when(repository.findAll()).thenReturn(departments);
        service.removeAll();
        //then
        verify(repository).findAll();
        verify(repository).deleteAll();
    }

    @Test
    void findByName() {

    }

    @Test
    void findByNamePaginated() {
    }
}