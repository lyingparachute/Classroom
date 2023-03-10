package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.exception.DepartmentNotFoundException;
import com.example.classroom.model.Department;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Teacher;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
    private ArgumentCaptor<Department> argumentCaptor;

    @Nested
    class SaveDepartmentTest {
        @Test
        void create_shouldSaveDepartment_givenDepartmentDto() {
            //given
            Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
            DepartmentDto dto = mapper.map(expected, DepartmentDto.class);
            given(repository.save(any(Department.class))).willReturn(expected);
            //when
            service.create(dto);
            //then
            then(repository).should().save(argumentCaptor.capture());
            then(repository).shouldHaveNoMoreInteractions();
            Department actual = argumentCaptor.getValue();
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
    }

    @Nested
    class UpdateDepartmentTest {
        @Test
        void update_shouldUpdateDepartment_givenDepartmentDto() {
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
            expected.setTelNumber("987654321");
            expected.setDean(dean);
            expected.addFieldOfStudy(fieldOfStudy1);
            expected.addFieldOfStudy(fieldOfStudy2);
            DepartmentDto dto = mapper.map(expected, DepartmentDto.class);
            given(repository.findById(anyLong())).willReturn(Optional.of(entityBeforeUpdate));
            //when
            DepartmentDto updated = service.update(dto);
            //then
            then(repository).should().findById(anyLong());
            then(repository).shouldHaveNoMoreInteractions();
            Department actual = mapper.map(updated, Department.class);

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
        void update_throwsDepartmentNotFoundException_givenWrongDepartmentDto() {
            //given
            Department department = initData.createDepartmentOne(null, List.of());
            DepartmentDto dto = mapper.map(department, DepartmentDto.class);
            dto.setId(5L);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(DepartmentNotFoundException.class)
                    .hasMessage("Invalid Department '" + dto.getName() + "' with ID: " + dto.getId());
        }
    }

    @Nested
    class FindAllDepartmentsTest {
        @Test
        void fetchAll_shouldReturnAllDepartments() {
            //given
            Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher dean2 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Department expectedDepartment1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
            Department expectedDepartment2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
            List<Department> departments = List.of(expectedDepartment1, expectedDepartment2);
            given(repository.findAll()).willReturn(departments);
            //when
            List<DepartmentDto> actual = service.fetchAll();
            //then
            then(repository).should().findAll();
            then(repository).shouldHaveNoMoreInteractions();
            assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
            DepartmentDto actualDepartment1 = actual.get(0);
            DepartmentDto actualDepartment2 = actual.get(1);
            assertAll("expectedDepartment1 properties",
                    () -> assertThat(actualDepartment1.getId())
                            .as("Check %s %s", "expectedDepartment1", "ID").isEqualTo(expectedDepartment1.getId()),
                    () -> assertThat(actualDepartment1.getName())
                            .as("Check %s %s", "expectedDepartment1", "Name").isEqualTo(expectedDepartment1.getName()),
                    () -> assertThat(actualDepartment1.getAddress())
                            .as("Check %s %s", "expectedDepartment1", "Address").isEqualTo(expectedDepartment1.getAddress()),
                    () -> assertThat(actualDepartment1.getTelNumber())
                            .as("Check %s %s", "expectedDepartment1", "Telephone Number").isEqualTo(expectedDepartment1.getTelNumber()),
                    () -> assertThat(actualDepartment1.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean1),
                    () -> assertThat(actualDepartment1.getFieldsOfStudy())
                            .as("Check if %s contains %s", "expectedDepartment1", "fieldOfStudy1").contains(fieldOfStudy1),
                    () -> assertThat(actualDepartment1.getFieldsOfStudy())
                            .as("Check if %s does not contain %s", "expectedDepartment1", "fieldOfStudy2").doesNotContain(fieldOfStudy2)
            );
            assertAll("expectedDepartment2 properties",
                    () -> assertThat(actualDepartment2.getId())
                            .as("Check %s %s", "expectedDepartment2", "ID").isEqualTo(expectedDepartment2.getId()),
                    () -> assertThat(actualDepartment2.getName())
                            .as("Check %s %s", "expectedDepartment2", "Name").isEqualTo(expectedDepartment2.getName()),
                    () -> assertThat(actualDepartment2.getAddress())
                            .as("Check %s %s", "expectedDepartment2", "Address").isEqualTo(expectedDepartment2.getAddress()),
                    () -> assertThat(actualDepartment2.getTelNumber())
                            .as("Check %s %s", "expectedDepartment2", "Telephone Number").isEqualTo(expectedDepartment2.getTelNumber()),
                    () -> assertThat(actualDepartment2.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean2),
                    () -> assertThat(actualDepartment2.getFieldsOfStudy())
                            .as("Check if %s contains %s", "expectedDepartment2", "fieldOfStudy1").contains(fieldOfStudy2),
                    () -> assertThat(actualDepartment2.getFieldsOfStudy())
                            .as("Check if %s does not contain %s", "expectedDepartment2", "fieldOfStudy2").doesNotContain(fieldOfStudy1)
            );
        }

        @Test
        void fetchAllPaginated_shouldReturnAllDepartmentsPaginated_givenPageNo_pageSize_sortDir() {
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

            Department expectedDepartment1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
            Department expectedDepartment2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
            Department expectedDepartment3 = initData.createDepartmentThree(dean3, List.of(fieldOfStudy3));
            Page<Department> departments = new PageImpl<>(List.of(expectedDepartment3));
            given(repository.findAll(any(Pageable.class))).willReturn(departments);
            //when
            Page<DepartmentDto> actualPage = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
            //then
            then(repository).should().findAll(any(Pageable.class));
            then(repository).shouldHaveNoMoreInteractions();
            List<DepartmentDto> actualContent = actualPage.getContent();
            assertThat(actualContent).as("Check %s's list size", "departments").hasSize(1);
            DepartmentDto actualDepartment = actualContent.get(0);
            assertAll("Resulting department properties",
                    () -> assertThat(actualDepartment.getId())
                            .as("Check %s %s", "department3", "ID").isEqualTo(expectedDepartment3.getId()),
                    () -> assertThat(actualDepartment.getName())
                            .as("Check %s %s", "department3", "Name").isEqualTo(expectedDepartment3.getName()),
                    () -> assertThat(actualDepartment.getAddress())
                            .as("Check %s %s", "department3", "Address").isEqualTo(expectedDepartment3.getAddress()),
                    () -> assertThat(actualDepartment.getTelNumber())
                            .as("Check %s %s", "department3", "Telephone Number").isEqualTo(expectedDepartment3.getTelNumber()),
                    () -> assertThat(actualDepartment.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean3),
                    () -> assertThat(actualDepartment.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department3", "fieldOfStudy1")
                            .contains(fieldOfStudy3).doesNotContain(fieldOfStudy1, fieldOfStudy2)
            );
        }
    }

    @Nested
    class FindDepartmentTest {
        @Test
        void fetchById_shouldFindDepartment_givenId() {
            //given
            Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
            given(repository.findById(anyLong())).willReturn(Optional.of(expected));
            //when
            DepartmentDto actual = service.fetchById(expected.getId());
            //then
            ArgumentCaptor<Long> idArgCaptor = ArgumentCaptor.forClass(Long.class);
            then(repository).should().findById(idArgCaptor.capture());
            then(repository).shouldHaveNoMoreInteractions();
            Long actualId = idArgCaptor.getValue();

            assertThat(actual).as("Check if %s is not null", "Department").isNotNull();
            assertAll("Department properties",
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
        void fetchById_throwsDepartmentNotFoundException_givenWrongId() {
            //given
            Long id = 10L;
            //when
            Throwable thrown = catchThrowable(() -> service.fetchById(id));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(DepartmentNotFoundException.class)
                    .hasMessage("Invalid Department id: " + id);
        }
    }

    @Nested
    class DeleteDepartmentTest {
        @Test
        void remove_shouldDeleteDepartment_givenId() {
            //given
            Department expected = initData.createDepartmentOne(null, List.of());
            given(repository.findById(anyLong())).willReturn(Optional.of(expected));
            //when
            service.remove(expected.getId());
            //then
            then(repository).should().findById(anyLong());
            then(repository).should().delete(any(Department.class));
            then(repository).shouldHaveNoMoreInteractions();
        }

        @Test
        void remove_throwsDepartmentNotFoundException_givenWrongId() {
            //given
            Long id = 10L;
            //when
            Throwable thrown = catchThrowable(() -> service.remove(id));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(DepartmentNotFoundException.class)
                    .hasMessage("Invalid Department id: " + id);
        }

        @Test
        void removeAll_shouldDeleteAllDepartments() {
            //given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Department department3 = initData.createDepartmentThree(null, List.of());
            List<Department> departments = List.of(department1, department2, department3);
            given(repository.findAll()).willReturn(departments);
            //when
            service.removeAll();
            //then
            then(repository).should().findAll();
            then(repository).should().deleteAll();
            then(repository).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    class FindDepartmentsNameTest {
        @Test
        void findByName_returnsDepartmentsSearcherByName_givenName() {
            //given
            String name = "ch";

            Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher dean2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher dean3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

            Department expectedDepartment1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
            Department expectedDepartment2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
            Department expectedDepartment3 = initData.createDepartmentThree(dean3, List.of(fieldOfStudy3));
            List<Department> departments = List.of(expectedDepartment2, expectedDepartment3);
            given(repository.findAllByNameContainingIgnoreCase(anyString())).willReturn(departments);
            //when
            List<DepartmentDto> actualContent = service.findByName(name);
            //then
            then(repository).should().findAllByNameContainingIgnoreCase(anyString());
            then(repository).shouldHaveNoMoreInteractions();
            then(repository).shouldHaveNoMoreInteractions();
            assertThat(actualContent).hasSize(2);
            DepartmentDto actualDepartment1 = actualContent.get(0);
            DepartmentDto actualDepartment2 = actualContent.get(1);

            assertAll("department1 properties",
                    () -> assertThat(actualDepartment1.getId())
                            .as("Check %s %s", "department1", "ID").isEqualTo(expectedDepartment2.getId()),
                    () -> assertThat(actualDepartment1.getName())
                            .as("Check %s %s", "department1", "Name").isEqualTo(expectedDepartment2.getName()),
                    () -> assertThat(actualDepartment1.getAddress())
                            .as("Check %s %s", "department1", "Address").isEqualTo(expectedDepartment2.getAddress()),
                    () -> assertThat(actualDepartment1.getTelNumber())
                            .as("Check %s %s", "department1", "Telephone Number").isEqualTo(expectedDepartment2.getTelNumber()),
                    () -> assertThat(actualDepartment1.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean2),
                    () -> assertThat(actualDepartment1.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department1", "fieldOfStudy1")
                            .contains(fieldOfStudy2).doesNotContain(fieldOfStudy1, fieldOfStudy3)
            );
            assertAll("department2 properties",
                    () -> assertThat(actualDepartment2.getId())
                            .as("Check %s %s", "department2", "ID").isEqualTo(expectedDepartment3.getId()),
                    () -> assertThat(actualDepartment2.getName())
                            .as("Check %s %s", "department2", "Name").isEqualTo(expectedDepartment3.getName()),
                    () -> assertThat(actualDepartment2.getAddress())
                            .as("Check %s %s", "department2", "Address").isEqualTo(expectedDepartment3.getAddress()),
                    () -> assertThat(actualDepartment2.getTelNumber())
                            .as("Check %s %s", "department2", "Telephone Number").isEqualTo(expectedDepartment3.getTelNumber()),
                    () -> assertThat(actualDepartment2.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean3),
                    () -> assertThat(actualDepartment2.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department2", "fieldOfStudy1")
                            .contains(fieldOfStudy3).doesNotContain(fieldOfStudy1, fieldOfStudy2)
            );
        }

        @Test
        void findByNamePaginated_shouldReturnDepartmentsSearcherByNamePaginated_givenName_pageNo_pageSize_sortDir() {
            //given
            int pageNo = 2;
            int pageSize = 2;
            String sortField = "name";
            String sortDirection = Sort.Direction.DESC.name();
            String name = "ch";

            Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher dean2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher dean3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

            Department expectedDepartment1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
            Department expectedDepartment2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
            Department expectedDepartment3 = initData.createDepartmentThree(dean3, List.of(fieldOfStudy3));
            Page<Department> departments = new PageImpl<>(List.of(expectedDepartment2, expectedDepartment3));
            given(repository.findAllByNameContainingIgnoreCase(anyString(), any(Pageable.class))).willReturn(departments);
            //when
            Page<DepartmentDto> actualPage = service.findByNamePaginated(pageNo, pageSize, sortField, sortDirection, name);
            //then
            then(repository).should().findAllByNameContainingIgnoreCase(anyString(), any(Pageable.class));
            then(repository).shouldHaveNoMoreInteractions();
            List<DepartmentDto> actualContent = actualPage.getContent();
            assertThat(actualContent).size().isEqualTo(2);
            DepartmentDto actualDepartment1 = actualContent.get(0);
            DepartmentDto actualDepartment2 = actualContent.get(1);

            assertAll("department1 properties",
                    () -> assertThat(actualDepartment1.getId())
                            .as("Check %s %s", "department1", "ID").isEqualTo(expectedDepartment2.getId()),
                    () -> assertThat(actualDepartment1.getName())
                            .as("Check %s %s", "department1", "Name").isEqualTo(expectedDepartment2.getName()),
                    () -> assertThat(actualDepartment1.getAddress())
                            .as("Check %s %s", "department1", "Address").isEqualTo(expectedDepartment2.getAddress()),
                    () -> assertThat(actualDepartment1.getTelNumber())
                            .as("Check %s %s", "department1", "Telephone Number").isEqualTo(expectedDepartment2.getTelNumber()),
                    () -> assertThat(actualDepartment1.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean2),
                    () -> assertThat(actualDepartment1.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department1", "fieldOfStudy1")
                            .contains(fieldOfStudy2).doesNotContain(fieldOfStudy1, fieldOfStudy3)
            );
            assertAll("department2 properties",
                    () -> assertThat(actualDepartment2.getId())
                            .as("Check %s %s", "department2", "ID").isEqualTo(expectedDepartment3.getId()),
                    () -> assertThat(actualDepartment2.getName())
                            .as("Check %s %s", "department2", "Name").isEqualTo(expectedDepartment3.getName()),
                    () -> assertThat(actualDepartment2.getAddress())
                            .as("Check %s %s", "department2", "Address").isEqualTo(expectedDepartment3.getAddress()),
                    () -> assertThat(actualDepartment2.getTelNumber())
                            .as("Check %s %s", "department2", "Telephone Number").isEqualTo(expectedDepartment3.getTelNumber()),
                    () -> assertThat(actualDepartment2.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean3),
                    () -> assertThat(actualDepartment2.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department2", "fieldOfStudy1")
                            .contains(fieldOfStudy3).doesNotContain(fieldOfStudy1, fieldOfStudy2)
            );
        }
    }
}