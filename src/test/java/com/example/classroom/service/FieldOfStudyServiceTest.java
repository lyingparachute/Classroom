package com.example.classroom.service;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.*;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.util.InitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ActiveProfiles("test-it")
@SpringBootTest
@Transactional
class FieldOfStudyServiceTest {

    @Autowired
    private InitData initData;

    @Autowired
    private FieldOfStudyService service;

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void create_shouldSaveObject_givenObjectDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));
        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));
        Department department = initData.createDepartmentOne(teacher1, List.of());

        FieldOfStudyDto expected = createFieldOfStudyDto(department, List.of(subject1, subject2), List.of(student1, student2));
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
        assertThat(actual.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getName(), expected.getLevelOfEducation(), expected.getMode(), expected.getTitle()));
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
                                subject1.getHoursInSemester(), fieldOfStudyRepository.findAll().get(0), subject1.getTeachers()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), fieldOfStudyRepository.findAll().get(0), subject2.getTeachers()));
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
                                student1.getEmail(), fieldOfStudyRepository.findAll().get(0), student1.getTeachers()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), fieldOfStudyRepository.findAll().get(0), student2.getTeachers()));
    }

    @Test
    void update_shouldUpdateObject_givenObjectDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));
        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));
        Department department = initData.createDepartmentOne(teacher1, List.of());

        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudyDto expected = createFieldOfStudyDto(department, List.of(subject1, subject2), List.of(student1, student2));
        expected.setId(fieldOfStudy.getId());
        //when
        service.update(expected);
        //then
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        FieldOfStudy actual = byId.get();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLevelOfEducation()).isEqualTo(expected.getLevelOfEducation());
        assertThat(actual.getMode()).isEqualTo(expected.getMode());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDepartment()).isEqualTo(expected.getDepartment());
        assertThat(actual.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getId(), expected.getName(), expected.getLevelOfEducation(),
                                expected.getMode(), expected.getTitle()));
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
                                subject1.getHoursInSemester(), fieldOfStudyRepository.findAll().get(0), subject1.getTeachers()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), fieldOfStudyRepository.findAll().get(0), subject2.getTeachers()));
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
                                student1.getEmail(), fieldOfStudyRepository.findAll().get(0), student1.getTeachers()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), fieldOfStudyRepository.findAll().get(0), student2.getTeachers()));
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
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department1 = initData.createDepartmentOne(teacher1, List.of());
        Department department2 = initData.createDepartmentTwo(teacher2, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(department2, List.of(subject2), List.of(student2));
        //when
        List<FieldOfStudyDto> actual = service.fetchAll();
        assertThat(actual).size().isEqualTo(2);
        FieldOfStudyDto actualFieldOfStudy1 = actual.get(0);
        FieldOfStudyDto actualFieldOfStudy2 = actual.get(1);

        assertThat(actualFieldOfStudy1.getName()).isEqualTo(fieldOfStudy1.getName());
        assertThat(actualFieldOfStudy1.getLevelOfEducation()).isEqualTo(fieldOfStudy1.getLevelOfEducation());
        assertThat(actualFieldOfStudy1.getMode()).isEqualTo(fieldOfStudy1.getMode());
        assertThat(actualFieldOfStudy1.getTitle()).isEqualTo(fieldOfStudy1.getTitle());
        assertThat(actualFieldOfStudy1.getDepartment()).isEqualTo(fieldOfStudy1.getDepartment());
        assertThat(actualFieldOfStudy1.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getLevelOfEducation(),
                                fieldOfStudy1.getMode(), fieldOfStudy1.getTitle()));
        assertThat(actualFieldOfStudy1.getSubjects())
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
                                subject1.getHoursInSemester(), fieldOfStudy1, subject1.getTeachers()));
        assertThat(actualFieldOfStudy1.getStudents())
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
                                student1.getEmail(), fieldOfStudy1, student1.getTeachers()));

        assertThat(actualFieldOfStudy2.getName()).isEqualTo(fieldOfStudy2.getName());
        assertThat(actualFieldOfStudy2.getLevelOfEducation()).isEqualTo(fieldOfStudy2.getLevelOfEducation());
        assertThat(actualFieldOfStudy2.getMode()).isEqualTo(fieldOfStudy2.getMode());
        assertThat(actualFieldOfStudy2.getTitle()).isEqualTo(fieldOfStudy2.getTitle());
        assertThat(actualFieldOfStudy2.getDepartment()).isEqualTo(fieldOfStudy2.getDepartment());
        assertThat(actualFieldOfStudy2.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(fieldOfStudy2.getId(), fieldOfStudy2.getName(), fieldOfStudy2.getLevelOfEducation(),
                                fieldOfStudy2.getMode(), fieldOfStudy2.getTitle()));
        assertThat(actualFieldOfStudy2.getSubjects())
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), fieldOfStudy2, subject2.getTeachers()));
        assertThat(actualFieldOfStudy2.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getAge,
                        Student::getEmail,
                        Student::getFieldOfStudy,
                        Student::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), fieldOfStudy2, student2.getTeachers()));
    }

    @Test
    void fetchAllPaginated_shouldReturnAllObjectsPaginated_givenPageNo_PageSize_SortDir() {
        //given
        int pageNo = 2;
        int pageSize = 1;
        String sortField = "name";
        String sortDirection = Sort.Direction.DESC.name();

        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department1 = initData.createDepartmentOne(teacher1, List.of());
        Department department2 = initData.createDepartmentTwo(teacher2, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(department2, List.of(subject2), List.of(student2));
        //when
        Page<FieldOfStudyDto> actual = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
        //then
        List<FieldOfStudyDto> actualContent = actual.getContent();
        assertThat(actualContent.size()).isEqualTo(1);
        FieldOfStudyDto actualFieldOfStudy = actualContent.get(0);
        assertThat(actualFieldOfStudy.getName()).isEqualTo(fieldOfStudy1.getName());
        assertThat(actualFieldOfStudy.getLevelOfEducation()).isEqualTo(fieldOfStudy1.getLevelOfEducation());
        assertThat(actualFieldOfStudy.getMode()).isEqualTo(fieldOfStudy1.getMode());
        assertThat(actualFieldOfStudy.getTitle()).isEqualTo(fieldOfStudy1.getTitle());
        assertThat(actualFieldOfStudy.getDepartment()).isEqualTo(fieldOfStudy1.getDepartment());
        assertThat(actualFieldOfStudy.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getLevelOfEducation(),
                                fieldOfStudy1.getMode(), fieldOfStudy1.getTitle()));
        assertThat(actualFieldOfStudy.getSubjects())
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
                                subject1.getHoursInSemester(), fieldOfStudy1, subject1.getTeachers()));
        assertThat(actualFieldOfStudy.getStudents())
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
                                student1.getEmail(), fieldOfStudy1, student1.getTeachers()));
    }

    @Test
    void fetchById_shouldFindObject_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department = initData.createDepartmentOne(teacher1, List.of());

        FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        FieldOfStudyDto actual = service.fetchById(expected.getId());
        //then
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLevelOfEducation()).isEqualTo(expected.getLevelOfEducation());
        assertThat(actual.getMode()).isEqualTo(expected.getMode());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDepartment()).isEqualTo(expected.getDepartment());
        assertThat(actual.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getId(), expected.getName(), expected.getLevelOfEducation(),
                                expected.getMode(), expected.getTitle()));
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
                                subject1.getHoursInSemester(), expected, subject1.getTeachers()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), expected, subject2.getTeachers()));
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
                                student1.getEmail(), expected, student1.getTeachers()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), expected, student2.getTeachers()));
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
    void remove_shouldDeleteObject_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department = initData.createDepartmentOne(null, List.of());

        FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        service.remove(expected.getId());
        //then
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        departmentRepository.findById(department.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + department.getId() + " and name " + department.getName() + " should not be removed."));
        expected.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " + subject.getName() + " should not be removed.")));
        expected.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID = " + student.getId() + " and name " + student.getFirstName() + " " + student.getLastName() + " should not be removed.")));
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
    void removeAll_shouldDeleteAllObjects() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentThree(null, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyOne(department2, List.of(subject2), List.of(student2));
        //when
        service.removeAll();
        //then
        Optional<FieldOfStudy> byId1 = fieldOfStudyRepository.findById(fieldOfStudy1.getId());
        assertThat(byId1).isNotPresent();
        departmentRepository.findById(fieldOfStudy1.getDepartment().getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + fieldOfStudy1.getDepartment().getId() + " and name " + fieldOfStudy1.getDepartment().getName() + " should not be removed."));
        fieldOfStudy1.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " + subject.getName() + " should not be removed.")));
        fieldOfStudy1.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID = " + student.getId() + " and name " + student.getFirstName() + " " + student.getLastName() + " should not be removed.")));

        Optional<FieldOfStudy> byId2 = fieldOfStudyRepository.findById(fieldOfStudy2.getId());
        assertThat(byId2).isNotPresent();
        departmentRepository.findById(fieldOfStudy2.getDepartment().getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + fieldOfStudy2.getDepartment().getId() + " and name " +
                        fieldOfStudy2.getDepartment().getName() + " should not be removed."));
        fieldOfStudy2.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " + subject.getName() + " should not be removed.")));
        fieldOfStudy2.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID = " + student.getId() + " and name " + student.getFirstName() + " " + student.getLastName() + " should not be removed.")));
    }

    @Test
    void findByName_returnsObjectsSearchedByName_givenName() {
        //given
        String name = "in";
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department1 = initData.createDepartmentOne(teacher1, List.of());
        Department department2 = initData.createDepartmentTwo(teacher2, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(department2, List.of(subject2), List.of(student2));
        //when
        List<FieldOfStudyDto> actual = service.findByName(name);
        assertThat(actual).size().isEqualTo(1);
        FieldOfStudyDto actualFieldOfStudy = actual.get(0);

        assertThat(actualFieldOfStudy.getName()).isEqualTo(fieldOfStudy1.getName());
        assertThat(actualFieldOfStudy.getLevelOfEducation()).isEqualTo(fieldOfStudy1.getLevelOfEducation());
        assertThat(actualFieldOfStudy.getMode()).isEqualTo(fieldOfStudy1.getMode());
        assertThat(actualFieldOfStudy.getTitle()).isEqualTo(fieldOfStudy1.getTitle());
        assertThat(actualFieldOfStudy.getDepartment()).isEqualTo(fieldOfStudy1.getDepartment());
        assertThat(actualFieldOfStudy.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getLevelOfEducation(),
                                fieldOfStudy1.getMode(), fieldOfStudy1.getTitle()));
        assertThat(actualFieldOfStudy.getSubjects())
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
                                subject1.getHoursInSemester(), fieldOfStudy1, subject1.getTeachers()));
        assertThat(actualFieldOfStudy.getStudents())
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
                                student1.getEmail(), fieldOfStudy1, student1.getTeachers()));
    }

    @Test
    void findByNamePaginated_returnsObjectsSearchedByNamePaginated_givenName() {
        //given
        int pageNo = 1;
        int pageSize = 2;
        String sortField = "name";
        String sortDirection = Sort.Direction.ASC.name();
        String name = "in";
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department1 = initData.createDepartmentOne(teacher1, List.of());
        Department department2 = initData.createDepartmentTwo(teacher2, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(department2, List.of(), List.of());
        FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(department2, List.of(subject2), List.of(student2));
        //when
        Page<FieldOfStudyDto> actual = service.findByNamePaginated(pageNo, pageSize, sortField, sortDirection, name);
        List<FieldOfStudyDto> actualContent = actual.getContent();
        //then
        assertThat(actualContent.size()).isEqualTo(2);
        FieldOfStudyDto actualFieldOfStudy1 = actualContent.get(0);
        FieldOfStudyDto actualFieldOfStudy2 = actualContent.get(1);

        assertThat(actualFieldOfStudy1.getName()).isEqualTo(fieldOfStudy3.getName());
        assertThat(actualFieldOfStudy1.getLevelOfEducation()).isEqualTo(fieldOfStudy3.getLevelOfEducation());
        assertThat(actualFieldOfStudy1.getMode()).isEqualTo(fieldOfStudy3.getMode());
        assertThat(actualFieldOfStudy1.getTitle()).isEqualTo(fieldOfStudy3.getTitle());
        assertThat(actualFieldOfStudy1.getDepartment()).isEqualTo(fieldOfStudy3.getDepartment());
        assertThat(actualFieldOfStudy1.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(fieldOfStudy2.getId(), fieldOfStudy2.getName(), fieldOfStudy2.getLevelOfEducation(),
                                fieldOfStudy2.getMode(), fieldOfStudy2.getTitle()),
                        Tuple.tuple(fieldOfStudy3.getId(), fieldOfStudy3.getName(), fieldOfStudy3.getLevelOfEducation(),
                                fieldOfStudy3.getMode(), fieldOfStudy3.getTitle()));
        assertThat(actualFieldOfStudy1.getSubjects())
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), fieldOfStudy3, subject2.getTeachers()));
        assertThat(actualFieldOfStudy1.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getAge,
                        Student::getEmail,
                        Student::getFieldOfStudy,
                        Student::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), fieldOfStudy3, student2.getTeachers()));

        assertThat(actualFieldOfStudy2.getName()).isEqualTo(fieldOfStudy1.getName());
        assertThat(actualFieldOfStudy2.getLevelOfEducation()).isEqualTo(fieldOfStudy1.getLevelOfEducation());
        assertThat(actualFieldOfStudy2.getMode()).isEqualTo(fieldOfStudy1.getMode());
        assertThat(actualFieldOfStudy2.getTitle()).isEqualTo(fieldOfStudy1.getTitle());
        assertThat(actualFieldOfStudy2.getDepartment()).isEqualTo(fieldOfStudy1.getDepartment());
        assertThat(actualFieldOfStudy2.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(fieldOfStudy1.getId(), fieldOfStudy1.getName(), fieldOfStudy1.getLevelOfEducation(),
                                fieldOfStudy1.getMode(), fieldOfStudy1.getTitle()));
        assertThat(actualFieldOfStudy2.getSubjects())
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
                                subject1.getHoursInSemester(), fieldOfStudy1, subject1.getTeachers()));
        assertThat(actualFieldOfStudy2.getStudents())
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
                                student1.getEmail(), fieldOfStudy1, student1.getTeachers()));
    }


    private FieldOfStudyDto createFieldOfStudyDto(Department department, List<Subject> subjects, List<Student> students) {
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