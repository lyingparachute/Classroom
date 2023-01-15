package com.example.classroom.service;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.Semester;
import com.example.classroom.repository.*;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SubjectServiceTest {

    @Autowired
    private InitData initData;

    @Autowired
    private SubjectService service;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void create_shouldSaveSubject_givenSubjectDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        SubjectDto expected = createSubjectDto(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        service.create(expected);
        //then
        Optional<Subject> byId = subjectRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        Subject actual = byId.get();
        assertAll("Properties Test",
                () -> assertAll("Subject properties",
                        () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                        () -> assertThat(actual.getDescription()).isEqualTo(expected.getDescription()),
                        () -> assertThat(actual.getSemester()).isEqualTo(expected.getSemester()),
                        () -> assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester()),
                        () -> assertThat(actual.getFieldOfStudy()).isEqualTo(expected.getFieldOfStudy()),
                        () -> assertThat(actual.getTeachers())
                                .extracting(
                                        Teacher::getId,
                                        Teacher::getFirstName,
                                        Teacher::getLastName,
                                        Teacher::getEmail,
                                        Teacher::getAge,
                                        Teacher::getDepartmentDean
                                ).containsExactlyInAnyOrder(
                                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()))
                ),
                () -> assertAll("Field Of Study's Subjects properties",
                        () -> assertThat(fieldOfStudy.getSubjects())
                                .extracting(
                                        Subject::getName,
                                        Subject::getDescription,
                                        Subject::getSemester,
                                        Subject::getHoursInSemester,
                                        Subject::getFieldOfStudy,
                                        Subject::getTeachers
                                ).containsExactlyInAnyOrder(
                                        Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                                expected.getHoursInSemester(), expected.getFieldOfStudy(), expected.getTeachers()))
                ),
                () -> assertAll("Teacher1's Subjects properties",
                        () -> assertThat(teacher1.getSubjects())
                                .extracting(
                                        Subject::getName,
                                        Subject::getDescription,
                                        Subject::getSemester,
                                        Subject::getHoursInSemester,
                                        Subject::getFieldOfStudy,
                                        Subject::getTeachers
                                ).containsExactlyInAnyOrder(
                                        Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                                expected.getHoursInSemester(), expected.getFieldOfStudy(), expected.getTeachers()))
                ),
                () -> assertAll("Teacher2's Subjects properties",
                        () -> assertThat(teacher2.getSubjects())
                                .extracting(
                                        Subject::getName,
                                        Subject::getDescription,
                                        Subject::getSemester,
                                        Subject::getHoursInSemester,
                                        Subject::getFieldOfStudy,
                                        Subject::getTeachers
                                ).containsExactlyInAnyOrder(
                                        Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                                expected.getHoursInSemester(), expected.getFieldOfStudy(), expected.getTeachers()))
                )
        );
    }

    @Test
    void update_shouldUpdateSubject_givenSubjectDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject subject = initData.createSubjectTwo(null, List.of());
        SubjectDto expected = createSubjectDto(fieldOfStudy, List.of(teacher1, teacher2));
        expected.setId(subject.getId());
        //when
        service.update(expected);
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        Subject actual = byId.get();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getSemester()).isEqualTo(expected.getSemester());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getFieldOfStudy()).isEqualTo(expected.getFieldOfStudy());
        assertThat(fieldOfStudy.getSubjects())
                .extracting(
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                expected.getHoursInSemester(), expected.getFieldOfStudy(), expected.getTeachers()));
        assertThat(teacher1.getSubjects())
                .extracting(
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                expected.getHoursInSemester(), expected.getFieldOfStudy(), expected.getTeachers()));
        assertThat(teacher2.getSubjects())
                .extracting(
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                expected.getHoursInSemester(), expected.getFieldOfStudy(), expected.getTeachers()));
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
    }

    @Test
    void update_throwsIllegalArgumentException_givenWrongSubjectDto() {
        //given
        SubjectDto dto = createSubjectDto(null, List.of());
        dto.setId(1L);
        //when
        Throwable thrown = catchThrowable(() -> service.update(dto));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid subject '" + dto + "' with ID: " + dto.getId());
    }

    @Test
    void fetchAll_shouldReturnAllSubjects() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
        Subject expected3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
        //when
        List<SubjectDto> actual = service.fetchAll();
        //then
        assertThat(actual).size().isEqualTo(3);
        SubjectDto actual1 = actual.get(0);
        SubjectDto actual2 = actual.get(1);
        SubjectDto actual3 = actual.get(2);
        assertThat(actual1.getName()).isEqualTo(expected1.getName());
        assertThat(actual1.getDescription()).isEqualTo(expected1.getDescription());
        assertThat(actual1.getHoursInSemester()).isEqualTo(expected1.getHoursInSemester());
        assertThat(actual1.getFieldOfStudy()).isEqualTo(expected1.getFieldOfStudy());
        assertThat(actual1.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
        assertThat(actual2.getName()).isEqualTo(expected2.getName());
        assertThat(actual2.getDescription()).isEqualTo(expected2.getDescription());
        assertThat(actual2.getHoursInSemester()).isEqualTo(expected2.getHoursInSemester());
        assertThat(actual2.getFieldOfStudy()).isEqualTo(expected2.getFieldOfStudy());
        assertThat(actual2.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge(), teacher3.getDepartmentDean()));
        assertThat(actual3.getName()).isEqualTo(expected3.getName());
        assertThat(actual3.getDescription()).isEqualTo(expected3.getDescription());
        assertThat(actual3.getHoursInSemester()).isEqualTo(expected3.getHoursInSemester());
        assertThat(actual3.getFieldOfStudy()).isEqualTo(expected3.getFieldOfStudy());
        assertThat(actual3.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge(), teacher3.getDepartmentDean()));
    }

    @Test
    void fetchAllPaginated_shouldReturnAllSubjectsPaginated_givenPageNo_PageSize_SortDir() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
        Subject expected3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));

        int pageNo = 2;
        int pageSize = 2;
        String sortField = "name";
        String sortDirection = Sort.Direction.DESC.name();
        //when
        Page<SubjectDto> actualPage = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
        //then
        List<SubjectDto> actualContent = actualPage.getContent();
        assertThat(actualContent).size().isEqualTo(1);
        SubjectDto actual = actualContent.get(0);
        assertThat(actual.getName()).isEqualTo(expected2.getName());
        assertThat(actual.getDescription()).isEqualTo(expected2.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected2.getHoursInSemester());
        assertThat(actual.getFieldOfStudy()).isEqualTo(expected2.getFieldOfStudy());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge(), teacher3.getDepartmentDean()));
    }

    @Test
    void fetchById_shouldFindSubject_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject expected = initData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        SubjectDto actual = service.fetchById(expected.getId());
        //then
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getFieldOfStudy()).isEqualTo(expected.getFieldOfStudy());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
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
                .hasMessage("Invalid subject id: " + id);
    }

    @Test
    void remove_shouldRemoveSubject_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

        Subject expected = initData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
        //when
        service.remove(expected.getId());
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy.getId() + " and name " + fieldOfStudy.getName() + " should not be removed."));
        expected.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));
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
                .hasMessage("Invalid subject id: " + id);
    }

    @Test
    void removeAll_shouldDeleteAllObjects() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectTwo(fieldOfStudy2, List.of(teacher3));
        //when
        service.removeAll();
        //then
        Optional<Subject> byId1 = subjectRepository.findById(fieldOfStudy1.getId());
        assertThat(byId1).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy1.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy1.getId() + " and name " + fieldOfStudy1.getName() + " should not be removed."));
        expected1.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));

        Optional<Subject> byId2 = subjectRepository.findById(fieldOfStudy2.getId());
        assertThat(byId2).isNotPresent();
        fieldOfStudyRepository.findById(fieldOfStudy2.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + fieldOfStudy2.getId() + " and name " + fieldOfStudy2.getName() + " should not be removed."));
        expected2.getTeachers().forEach(teacher ->
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                        "Teacher with ID = " + teacher.getId() + " and name "
                                + teacher.getFirstName() + " " + teacher.getLastName() + " should not be removed.")));
    }

    @Test
    void findByName_returnsSubjectsSearchedByName_givenName() {
        //given
        String name = "i";

        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
        Subject expected3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
        //when
        List<SubjectDto> actual = service.findByName(name);
        //then
        assertThat(actual).hasSize(2);
        SubjectDto actualSubject1 = actual.get(0);
        SubjectDto actualSubject2 = actual.get(1);

        assertThat(actualSubject1.getName()).isEqualTo(expected1.getName());
        assertThat(actualSubject1.getDescription()).isEqualTo(expected1.getDescription());
        assertThat(actualSubject1.getHoursInSemester()).isEqualTo(expected1.getHoursInSemester());
        assertThat(actualSubject1.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
        assertThat(actualSubject2.getName()).isEqualTo(expected3.getName());
        assertThat(actualSubject2.getDescription()).isEqualTo(expected3.getDescription());
        assertThat(actualSubject2.getHoursInSemester()).isEqualTo(expected3.getHoursInSemester());
        assertThat(actualSubject2.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge(), teacher2.getDepartmentDean()));
    }

    @Test
    void findByNamePaginated_returnsSubjectsSearchedByNamePaginated_givenName() {
        //given
        int pageNo = 1;
        int pageSize = 1;
        String sortField = "name";
        String sortDirection = Sort.Direction.ASC.name();
        String name = "i";

        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

        Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
        Subject expected3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
        //when
        Page<SubjectDto> actual = service.findByNamePaginated(pageNo, pageSize, sortField, sortDirection, name);
        List<SubjectDto> content = actual.getContent();
        //then
        assertThat(content).hasSize(1);
        SubjectDto actualSubject1 = content.get(0);

        assertThat(actualSubject1.getName()).isEqualTo(expected1.getName());
        assertThat(actualSubject1.getDescription()).isEqualTo(expected1.getDescription());
        assertThat(actualSubject1.getHoursInSemester()).isEqualTo(expected1.getHoursInSemester());
        assertThat(actualSubject1.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getDepartmentDean
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getDepartmentDean()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getDepartmentDean()));
    }

    private SubjectDto createSubjectDto(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setName("Speech therapy");
        subjectDto.setDescription("Classes with speech therapy specialist.");
        subjectDto.setSemester(Semester.FIRST);
        subjectDto.setHoursInSemester(80);
        subjectDto.setFieldOfStudy(fieldOfStudy);
        subjectDto.setTeachers(new HashSet<>(teachers));
        return subjectDto;
    }
}