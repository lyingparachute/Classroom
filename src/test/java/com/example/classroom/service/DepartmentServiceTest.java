package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.util.UnitTestsInitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

        Department department = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
        DepartmentDto dto = mapper.map(department, DepartmentDto.class);
        //when
        when(departmentRepository.save(department)).thenReturn(department);
        DepartmentDto created = departmentService.create(dto);
        //then
        verify(departmentRepository).save(department);
        ArgumentCaptor<Department> departmentArgumentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).save(departmentArgumentCaptor.capture());
        Department actual = departmentArgumentCaptor.getValue();
        assertAll("Department Properties Test",
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(dto.getId()),
                () -> assertThat(actual.getName()).isEqualTo(dto.getName()),
                () -> assertThat(actual.getAddress()).isEqualTo(dto.getAddress()),
                () -> assertThat(actual.getTelNumber()).isEqualTo(dto.getTelNumber()),
                () -> assertThat(actual.getDean()).isEqualTo(dean),
                () -> assertThat(actual.getDean().getDepartmentDean()).isEqualTo(department),
                () -> assertThat(actual.getFieldsOfStudy())
                        .extracting(
                                FieldOfStudy::getId,
                                FieldOfStudy::getName,
                                FieldOfStudy::getMode,
                                FieldOfStudy::getTitle,
                                FieldOfStudy::getLevelOfEducation,
                                FieldOfStudy::getDepartment
                        ).containsExactlyInAnyOrder(
                                Tuple.tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getMode(),
                                        fieldOfStudy1.getTitle(), fieldOfStudy1.getLevelOfEducation(), department),
                                Tuple.tuple(fieldOfStudy2.getId(), fieldOfStudy2.getName(), fieldOfStudy2.getMode(),
                                        fieldOfStudy2.getTitle(), fieldOfStudy2.getLevelOfEducation(), department))
        );
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

    private DepartmentDto createDepartmentDto() {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(1L);
        dto.setName("Really nice department");
        dto.setAddress("Holy street 89");
        dto.setTelNumber(123456789);
        dto.setDean(createDean());
        dto.setFieldsOfStudy(new HashSet<>());
        return dto;
    }

    private Department createDepartment(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber(321321321);
        department.setDean(dean);
        fieldsOfStudy.forEach(department::addFieldOfStudy);
        return department;
    }


    private Teacher createDean() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Fabian");
        teacher.setLastName("Graczyk");
        teacher.setEmail("f.graczyk@gmail.com");
        teacher.setAge(55);
        return teacher;
    }

    private List<FieldOfStudy> createFieldsOfStudy() {
        FieldOfStudy fieldOfStudy1 = new FieldOfStudy();
        fieldOfStudy1.setName("Inżynieria mechaniczno-medyczna");
        fieldOfStudy1.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy1.setMode(ModeOfStudy.FT);
        fieldOfStudy1.setTitle(AcademicTitle.MGR);

        FieldOfStudy fieldOfStudy2 = new FieldOfStudy();
        fieldOfStudy2.setName("Mechatronika");
        fieldOfStudy2.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy2.setMode(ModeOfStudy.PT);
        fieldOfStudy2.setTitle(AcademicTitle.BACH);

        return List.of(fieldOfStudy1, fieldOfStudy2);
    }
}