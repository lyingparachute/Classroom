package com.example.classroom.service;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.InitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@Transactional
class SubjectServiceTest {

    @Autowired
    private InitData initData;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void create_shouldSaveStudent_givenStudentDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        ArrayList<Teacher> teachers = new ArrayList<>(List.of(teacher1, teacher2));
        SubjectDto expected = createSubjectDto(teachers);
        //when
        subjectService.create(expected);
        //then
        Optional<Subject> byId = subjectRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        Subject actual = byId.get();
        assertThat(actual.getShortName()).isEqualTo(expected.getShortName());
        assertThat(actual.getLongName()).isEqualTo(expected.getLongName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getTeachers()).size().isEqualTo(2);
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void update_shouldUpdateStudent_givenStudentDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Subject subject = initData.createSubjectArt(List.of());
        SubjectDto expected = createSubjectDto(List.of(teacher1, teacher2));
        expected.setId(subject.getId());
        //when
        subjectService.update(expected);
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        Subject actual = byId.get();
        assertThat(actual.getShortName()).isEqualTo(expected.getShortName());
        assertThat(actual.getLongName()).isEqualTo(expected.getLongName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void update_throwsIllegalArgumentException_givenWrongStudentDto() {
        //given
        SubjectDto dto = createSubjectDto(List.of());
        dto.setId(1L);
        //when
        Throwable thrown = catchThrowable(() -> subjectService.update(dto));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid subject '" + dto + "' with ID: " + dto.getId());
    }

    @Test
    void fetchAll_shouldReturnAllStudents() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Teacher teacher3 = initData.createTeacherThree(List.of());
        Subject expected1 = initData.createSubjectIT(List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectMaths(List.of(teacher3));
        Subject expected3 = initData.createSubjectScience(List.of(teacher1, teacher2, teacher3));
        //when
        List<SubjectDto> actual = subjectService.fetchAll();
        //then
        assertThat(actual).size().isEqualTo(3);
        SubjectDto actual1 = actual.get(0);
        SubjectDto actual2 = actual.get(1);
        SubjectDto actual3 = actual.get(2);
        assertThat(actual1.getShortName()).isEqualTo(expected1.getShortName());
        assertThat(actual1.getLongName()).isEqualTo(expected1.getLongName());
        assertThat(actual1.getDescription()).isEqualTo(expected1.getDescription());
        assertThat(actual1.getHoursInSemester()).isEqualTo(expected1.getHoursInSemester());
        assertThat(actual1.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        assertThat(actual2.getShortName()).isEqualTo(expected2.getShortName());
        assertThat(actual2.getLongName()).isEqualTo(expected2.getLongName());
        assertThat(actual2.getDescription()).isEqualTo(expected2.getDescription());
        assertThat(actual2.getHoursInSemester()).isEqualTo(expected2.getHoursInSemester());
        assertThat(actual2.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
        assertThat(actual3.getShortName()).isEqualTo(expected3.getShortName());
        assertThat(actual3.getLongName()).isEqualTo(expected3.getLongName());
        assertThat(actual3.getDescription()).isEqualTo(expected3.getDescription());
        assertThat(actual3.getHoursInSemester()).isEqualTo(expected3.getHoursInSemester());
        assertThat(actual3.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void fetchAllPaginated_shouldReturnAllStudentsPaginated_givenPageNo_PageSize_SortDir() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Teacher teacher3 = initData.createTeacherThree(List.of());
        Subject expected1 = initData.createSubjectIT(List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectMaths(List.of(teacher3));
        Subject expected3 = initData.createSubjectScience(List.of(teacher1, teacher2, teacher3));
        int pageNo = 2;
        int pageSize = 2;
        String sortField = "shortName";
        String sortDirection = Sort.Direction.DESC.name();
        //when
        Page<SubjectDto> actualPage = subjectService.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
        //then
        List<SubjectDto> actualContent = actualPage.getContent();
        assertThat(actualContent).size().isEqualTo(1);
        SubjectDto actual = actualContent.get(0);
        assertThat(actual.getShortName()).isEqualTo(expected1.getShortName());
        assertThat(actual.getLongName()).isEqualTo(expected1.getLongName());
        assertThat(actual.getDescription()).isEqualTo(expected1.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected1.getHoursInSemester());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void fetchById_shouldFindStudent_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Teacher teacher3 = initData.createTeacherThree(List.of());
        Subject expected = initData.createSubjectArt(List.of(teacher1, teacher2, teacher3));
        //when
        SubjectDto actual = subjectService.fetchById(expected.getId());
        //then
        assertThat(actual.getShortName()).isEqualTo(expected.getShortName());
        assertThat(actual.getLongName()).isEqualTo(expected.getLongName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void fetchById_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> subjectService.fetchById(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid subject id: " + id);
    }

    @Test
    void remove_shouldRemoveStudent_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Teacher teacher3 = initData.createTeacherThree(List.of());
        Subject expected = initData.createSubjectArt(List.of(teacher1, teacher2, teacher3));
        //when
        subjectService.remove(expected.getId());
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        expected.getTeachers().forEach(teacher -> {
            teacherRepository.findById(teacher.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + teacher.getId() + " should not be removed."));
        });
    }

    @Test
    void remove_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> subjectService.remove(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid subject id: " + id);
    }

    @Test
    void findByFirstOrLastName_returnsStudentsSearchedByFirstOrLastName_givenName() {
        //given
        String shortName = "i";
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Teacher teacher3 = initData.createTeacherThree(List.of());
        Subject expected1 = initData.createSubjectIT(List.of(teacher1, teacher2));
        Subject expected2 = initData.createSubjectMaths(List.of(teacher3));
        Subject expected3 = initData.createSubjectScience(List.of(teacher1, teacher2, teacher3));
        //when
        List<SubjectDto> actual = subjectService.findByShortName(shortName);
        //then
        assertThat(actual.size()).isEqualTo(2);
        SubjectDto actualSubject1 = actual.get(0);
        SubjectDto actualSubject2 = actual.get(1);

        assertThat(actualSubject1.getShortName()).isEqualTo(expected1.getShortName());
        assertThat(actualSubject1.getLongName()).isEqualTo(expected1.getLongName());
        assertThat(actualSubject1.getDescription()).isEqualTo(expected1.getDescription());
        assertThat(actualSubject1.getHoursInSemester()).isEqualTo(expected1.getHoursInSemester());
        assertThat(actualSubject1.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        assertThat(actualSubject2.getShortName()).isEqualTo(expected3.getShortName());
        assertThat(actualSubject2.getLongName()).isEqualTo(expected3.getLongName());
        assertThat(actualSubject2.getDescription()).isEqualTo(expected3.getDescription());
        assertThat(actualSubject2.getHoursInSemester()).isEqualTo(expected3.getHoursInSemester());
        assertThat(actualSubject2.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void assignTeachers_shouldAssignTeachersToStudent_givenTeachersSet() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        HashSet<Teacher> teachers = new HashSet<>(List.of(teacher1, teacher2));
        Subject expected = initData.createSubjectScience(List.of());
        //when
        subjectService.addTeachers(expected, teachers);
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        Subject actual = byId.get();
        assertThat(actual.getShortName()).isEqualTo(expected.getShortName());
        assertThat(actual.getLongName()).isEqualTo(expected.getLongName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void removeTeachers_shouldRemoveTeachersFromStudent_givenTeachersSet() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Subject expected = initData.createSubjectScience(List.of(teacher1, teacher2));
        Optional<Subject> byId2 = subjectRepository.findById(expected.getId());
        assertThat(byId2).isPresent();
        Subject actual2 = byId2.get();
        assertThat(actual2.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).contains(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        //when
        subjectService.removeTeachers(expected, new HashSet<>(expected.getTeachers()));
        //then
        Optional<Subject> byId = subjectRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        Subject actual = byId.get();
        assertThat(actual.getShortName()).isEqualTo(expected.getShortName());
        assertThat(actual.getLongName()).isEqualTo(expected.getLongName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getHoursInSemester()).isEqualTo(expected.getHoursInSemester());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).doesNotContain(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    private SubjectDto createSubjectDto(List<Teacher> teachers) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setShortName("SPEECH");
        subjectDto.setLongName("Speech therapy");
        subjectDto.setDescription("Classes with speech therapy specialist.");
        subjectDto.setHoursInSemester(80);
        subjectDto.setTeachers(new HashSet<>(teachers));
        return subjectDto;
    }
}