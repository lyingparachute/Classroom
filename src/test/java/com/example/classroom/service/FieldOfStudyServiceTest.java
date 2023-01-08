package com.example.classroom.service;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.util.InitData;
import com.example.classroom.repository.util.InitDepartment;
import com.example.classroom.repository.util.InitSubject;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FieldOfStudyServiceTest {

    @Autowired
    private InitData initData;

    @Autowired
    private InitDepartment initDepartment;

    @Autowired
    private InitSubject initSubject;

    @Autowired
    private FieldOfStudyService service;

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void create_shouldSaveObject_givenObjectDto() {
        //given
        Subject subject1 = initSubject.createSubjectMaths(List.of());
        Department department = initDepartment.createDepartmentOne(null, List.of());
        FieldOfStudyDto expected = createFieldOfStudyDto(department, List.of(), List.of());
        //when
        service.create(expected);
        //then
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        FieldOfStudy actual = byId.get();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLevelOfEducation()).isEqualTo(expected.getLevelOfEducation());
        assertThat(actual.getMode()).isEqualTo(expected.getMode());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDepartment()).isEqualTo(expected.getDepartment());
        assertThat(actual.getSubjects())
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                subject1.getSemester(), subject1.getHoursInSemester()));
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

    @Test
    void fetchById() {
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

    private FieldOfStudyDto createFieldOfStudyDto(Department department, List<Subject> subjects, List<Student> students){
        FieldOfStudyDto dto = new FieldOfStudyDto();
        dto.setName("name");
        dto.setLevelOfEducation(LevelOfEducation.FIRST);
        dto.setMode(ModeOfStudy.PT);
        dto.setTitle(AcademicTitle.ENG);
        dto.setDepartment(department);
        dto.setSubjects(new HashSet<>(subjects));
        dto.setStudents(new HashSet<>(students));
        return dto;
    }
}