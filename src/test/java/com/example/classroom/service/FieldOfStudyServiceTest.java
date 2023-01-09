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
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@Transactional
class FieldOfStudyServiceTest {

    @Autowired
    private InitData initData;



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
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentOne(null, List.of());
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department = initData.createDepartmentOne(null, List.of());
        FieldOfStudyDto expected = createFieldOfStudyDto(department, List.of(subject1), List.of(student1));
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
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(), subject1.getSemester(),
                                subject1.getHoursInSemester(), subject1.getFieldOfStudy(), subject1.getTeachers()));
        assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getAge,
                        Student::getEmail,
                        Student::getFieldOfStudy,
                        Student::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(), student1.getAge(),
                                student1.getEmail(), student1.getFieldOfStudy(), student1.getTeachers()));
    }

    @Test
    void update_shouldUpdateObject_givenObjectDto() {
    }

    @Test
    void update_throwsIllegalArgumentException_givenWrongObjectDto() {
        //given
        FieldOfStudyDto dto = createFieldOfStudyDto(null, List.of(), List.of());
        dto.setId(1L);
        //when
        Throwable thrown = catchThrowable(() -> service.update(dto));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId());
    }

    @Test
    void fetchAll_shouldReturnAllObjects() {
    }

    @Test
    void fetchAllPaginated_shouldReturnAllObjectsPaginated_givenPageNo_PageSize_SortDir() {
    }

    @Test
    void fetchById_shouldFindObject_givenId() {
    }

    @Test
    void fetchById_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> service.fetchById(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Field Of Study ID: " + id);
    }

    @Test
    void remove_shouldRemoveObject_givenId() {

    }

    @Test
    void remove_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> service.remove(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Field Of Study ID: " + id);
    }

    @Test
    void findByName_returnsObjectsSearchedByName_givenName() {
    }

    @Test
    void findByNamePaginated() {
    }

    @Test
    void removeAll_shouldRemoveAllObjects() {
    }

    private FieldOfStudyDto createFieldOfStudyDto(Department department, List<Subject> subjects, List<Student> students){
        FieldOfStudyDto dto = new FieldOfStudyDto();
        dto.setName("Inżynieria materiałowa");
        dto.setLevelOfEducation(LevelOfEducation.FIRST);
        dto.setMode(ModeOfStudy.PT);
        dto.setTitle(AcademicTitle.ENG);
        dto.setDepartment(department);
        dto.setSubjects(new HashSet<>(subjects));
        dto.setStudents(new HashSet<>(students));
        return dto;
    }
}