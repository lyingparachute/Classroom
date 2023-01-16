package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.repository.DepartmentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    //TODO - tests for DepartmentService

    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    DepartmentService departmentService;

    @Spy
    ModelMapper mapper;

    @Captor
    private ArgumentCaptor<List<Department>> argCaptor;

    @Test
    void create_shouldSaveDepartment_givenDto() {
        //given
        DepartmentDto dto = createDepartmentDto();
        Department entity = mapper.map(dto, Department.class);
//        entity.setDean(createDean());
        //when
        when(departmentRepository.save(entity)).thenReturn(entity);
        DepartmentDto created = departmentService.create(dto);
        //then
        verify(departmentRepository).save(entity);
        ArgumentCaptor<Department> departmentArgumentCaptor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).save(departmentArgumentCaptor.capture());
        Department captured = departmentArgumentCaptor.getValue();
        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isEqualTo(dto.getId());
        assertThat(captured.getName()).isEqualTo(dto.getName());
        assertThat(captured.getAddress()).isEqualTo(dto.getAddress());
        assertThat(captured.getTelNumber()).isEqualTo(dto.getTelNumber());
        assertThat(captured.getDean()).isEqualTo(createDean());
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

    private DepartmentDto createDepartmentDto(){
        DepartmentDto dto = new DepartmentDto();
        dto.setId(1L);
        dto.setName("Really nice department");
        dto.setAddress("Holy street 89");
        dto.setTelNumber(123456789);
        dto.setDean(createDean());
        dto.setFieldsOfStudy(new HashSet<>());
        return dto;
    }

    private Department createDepartment(Teacher dean, List<FieldOfStudy> fieldsOfStudy){
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber(321321321);
        department.setDean(dean);
        fieldsOfStudy.forEach(department::addFieldOfStudy);
        return department;
    }


    private Teacher createDean(){
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