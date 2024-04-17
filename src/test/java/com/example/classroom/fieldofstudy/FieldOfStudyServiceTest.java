package com.example.classroom.fieldofstudy;

import com.example.classroom.department.Department;
import com.example.classroom.department.DepartmentDto;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.enums.Semester;
import com.example.classroom.student.Student;
import com.example.classroom.subject.Subject;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.test.util.UnitTestsInitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldOfStudyServiceTest {
    @Mock
    FieldOfStudyRepository repository;

    @InjectMocks
    FieldOfStudyService service;

    @Spy
    ModelMapper mapper;

    @Spy
    UnitTestsInitData initData;

    @Captor
    private ArgumentCaptor<FieldOfStudy> argumentCaptor;

    @Nested
    class CreateTest {
        @Test
        void returnsSavedFieldOfStudy_givenFieldOfStudyDto() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Student student1 = initData.createStudentOne(null, List.of(teacher1));
            Student student2 = initData.createStudentTwo(null, List.of(teacher2));
            Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
            Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));
            Department department = initData.createDepartmentOne(teacher1, List.of());

            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            when(repository.save(any(FieldOfStudy.class))).thenReturn(expected);
            service.create(dto);
            //then
            verify(repository).save(argumentCaptor.capture());
            FieldOfStudy actual = argumentCaptor.getValue();
            assertThat(actual).as("Check if %s is not null", "FieldOfStudy").isNotNull();
            assertAll("FieldOfStudy's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "FieldOfStudy", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "FieldOfStudy", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s's %s", "FieldOfStudy", "Level of education").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s's %s", "FieldOfStudy", "Study mode").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getTitle())
                            .as("Check %s's %s", "FieldOfStudy", "Obtained title").isEqualTo(expected.getTitle())
            );
            assertAll("FieldOfStudy's department properties",
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Id").isEqualTo(department.getId()),
                    () -> assertThat(actual.getDepartment().getName())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Name").isEqualTo(department.getName()),
                    () -> assertThat(actual.getDepartment().getAddress())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Address").isEqualTo(department.getAddress()),
                    () -> assertThat(actual.getDepartment().getTelNumber())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Telephone number").isEqualTo(department.getTelNumber()),
                    () -> assertThat(actual.getDepartment().getFieldsOfStudy())
                            .as("Check %s's %s properties", "department", "fieldsOfStudy")
                            .extracting(
                                    FieldOfStudy::getId,
                                    FieldOfStudy::getName,
                                    FieldOfStudy::getMode,
                                    FieldOfStudy::getTitle,
                                    FieldOfStudy::getLevelOfEducation
                            ).containsExactlyInAnyOrder(
                                    tuple(expected.getId(), expected.getName(), expected.getMode(),
                                            expected.getTitle(), expected.getLevelOfEducation()))
            );
            assertThat(actual.getSubjects()).as("Check %s's %s properties", "FieldOfStudy", "subjects")
                    .extracting(
                            Subject::getId,
                            Subject::getName,
                            Subject::getDescription,
                            Subject::getSemester,
                            Subject::getHoursInSemester,
                            Subject::getFieldOfStudy
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                    subject1.getSemester(), subject1.getHoursInSemester(), expected),
                            Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                    subject2.getSemester(), subject2.getHoursInSemester(), expected));
            assertThat(actual.getStudents()).as("Check %s's %s properties", "FieldOfStudy", "students")
                    .extracting(
                            Student::getId,
                            Student::getFirstName,
                            Student::getLastName,
                            Student::getAge,
                            Student::getEmail,
                            Student::getFieldOfStudy
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                    student1.getAge(), student1.getEmail(), expected),
                            Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                    student2.getAge(), student2.getEmail(), expected));
        }
    }

    @Nested
    class UpdateTest {
        @Test
        void returnsUpdatedFieldOfStudy_givenFieldOfStudyDto() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Student student1 = initData.createStudentOne(null, List.of(teacher1));
            Student student2 = initData.createStudentTwo(null, List.of(teacher2));
            Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
            Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));
            Department department = initData.createDepartmentOne(teacher1, List.of());

            FieldOfStudy entityBeforeUpdate = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy expected = new FieldOfStudy();
            expected.setId(10L);
            expected.setName("Inżynieria materiałowa");
            expected.setLevelOfEducation(LevelOfEducation.FIRST);
            expected.setMode(ModeOfStudy.PT);
            expected.setTitle(AcademicTitle.ENG);
            expected.setDepartment(department);
            expected.addSubject(subject1);
            expected.addSubject(subject2);
            expected.addStudent(student1);
            expected.addStudent(student2);
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(entityBeforeUpdate));
            FieldOfStudyDto updated = service.update(dto);
            //then
            verify(repository).findById(anyLong());
            FieldOfStudy actual = mapper.map(updated, FieldOfStudy.class);

            assertThat(actual).as("Check if %s is not null", "FieldOfStudy").isNotNull();
            assertAll("FieldOfStudy's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "FieldOfStudy", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "FieldOfStudy", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s's %s", "FieldOfStudy", "Level of education").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s's %s", "FieldOfStudy", "Study mode").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getTitle())
                            .as("Check %s's %s", "FieldOfStudy", "Obtained title").isEqualTo(expected.getTitle())
            );
            assertAll("FieldOfStudy's department properties",
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Id").isEqualTo(department.getId()),
                    () -> assertThat(actual.getDepartment().getName())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Name").isEqualTo(department.getName()),
                    () -> assertThat(actual.getDepartment().getAddress())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Address").isEqualTo(department.getAddress()),
                    () -> assertThat(actual.getDepartment().getTelNumber())
                            .as("Check %s's %s %s", "FieldOfStudy", "department", "Telephone number").isEqualTo(department.getTelNumber()),
                    () -> assertThat(actual.getDepartment().getFieldsOfStudy())
                            .as("Check %s's %s properties", "department", "fieldsOfStudy")
                            .extracting(
                                    FieldOfStudy::getId,
                                    FieldOfStudy::getName,
                                    FieldOfStudy::getMode,
                                    FieldOfStudy::getTitle,
                                    FieldOfStudy::getLevelOfEducation
                            ).containsExactlyInAnyOrder(
                                    tuple(expected.getId(), expected.getName(), expected.getMode(),
                                            expected.getTitle(), expected.getLevelOfEducation()))
            );
            assertThat(actual.getSubjects()).as("Check %s's %s properties", "FieldOfStudy", "subjects")
                    .extracting(
                            Subject::getId,
                            Subject::getName,
                            Subject::getDescription,
                            Subject::getSemester,
                            Subject::getHoursInSemester,
                            Subject::getFieldOfStudy
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                    subject1.getSemester(), subject1.getHoursInSemester(), expected),
                            Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                    subject2.getSemester(), subject2.getHoursInSemester(), expected));
            assertThat(actual.getStudents()).as("Check %s's %s properties", "FieldOfStudy", "students")
                    .extracting(
                            Student::getId,
                            Student::getFirstName,
                            Student::getLastName,
                            Student::getAge,
                            Student::getEmail,
                            Student::getFieldOfStudy
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                    student1.getAge(), student1.getEmail(), expected),
                            Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                    student2.getAge(), student2.getEmail(), expected));

        }

        @Test
        void throwsIllegalArgumentException_givenWrongFieldOfStudyDto() {
            //given
            FieldOfStudy expected = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId());
        }
    }

    @Nested
    class UpdateSubjectsTest {
        @Test
        void returnsUpdatedFieldOfStudy_withAddedSubjects_givenFieldOfStudyDto() {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());

            FieldOfStudy entityBeforeUpdate = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy expected = new FieldOfStudy();
            expected.setId(10L);
            expected.addSubject(subject1);
            expected.addSubject(subject2);
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(entityBeforeUpdate));
            when(repository.save(any(FieldOfStudy.class))).thenReturn(expected);
            service.updateSubjects(dto);
            //then
            verify(repository).findById(anyLong());
            verify(repository).save(argumentCaptor.capture());
            FieldOfStudy actual = argumentCaptor.getValue();

            assertThat(actual).as("Check if %s is not null", "FieldOfStudy").isNotNull();
            assertAll("FieldOfStudy's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "FieldOfStudy", "ID").isEqualTo(expected.getId())
            );
            assertThat(actual.getSubjects()).as("Check %s's %s properties", "FieldOfStudy", "subjects")
                    .extracting(
                            Subject::getId,
                            Subject::getName,
                            Subject::getDescription,
                            Subject::getSemester,
                            Subject::getHoursInSemester,
                            Subject::getFieldOfStudy
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                    subject1.getSemester(), subject1.getHoursInSemester(), expected),
                            Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                    subject2.getSemester(), subject2.getHoursInSemester(), expected));
        }

        @Test
        void throwsIllegalArgumentException_givenWrongFieldOfStudyDto() {
            //given
            FieldOfStudy expected = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId());
        }
    }

    @Nested
    class FetchAllTest {
        @Test
        void returnsAllFieldsOfStudy() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

            Student student1 = initData.createStudentOne(null, List.of(teacher1));
            Student student2 = initData.createStudentTwo(null, List.of(teacher2));

            Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
            Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

            Department department1 = initData.createDepartmentOne(teacher1, List.of());
            Department department2 = initData.createDepartmentTwo(teacher2, List.of());

            FieldOfStudy expectedFieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
            FieldOfStudy expectedFieldOfStudy2 = initData.createFieldOfStudyTwo(department2, List.of(subject2), List.of(student2));
            List<FieldOfStudy> fieldsOfStudy = List.of(expectedFieldOfStudy1, expectedFieldOfStudy2);
            //when
            when(repository.findAll()).thenReturn(fieldsOfStudy);
            List<FieldOfStudyDto> actual = service.fetchAll();
            //then
            verify(repository).findAll();
            assertThat(actual).as("Check %s's list size", "fieldsOfStudy").hasSize(2);
            FieldOfStudyDto actualFieldOfStudy1 = actual.get(0);
            FieldOfStudyDto actualFieldOfStudy2 = actual.get(1);

            assertAll("FieldOfStudy1 properties",
                    () -> assertThat(actualFieldOfStudy1.getId())
                            .as("Check %s's %s", "FieldOfStudy1", "ID").isEqualTo(expectedFieldOfStudy1.getId()),
                    () -> assertThat(actualFieldOfStudy1.getName())
                            .as("Check %s's %s", "FieldOfStudy1", "Name").isEqualTo(expectedFieldOfStudy1.getName()),
                    () -> assertThat(actualFieldOfStudy1.getLevelOfEducation())
                            .as("Check %s's %s", "FieldOfStudy1", "Level of education").isEqualTo(expectedFieldOfStudy1.getLevelOfEducation()),
                    () -> assertThat(actualFieldOfStudy1.getMode())
                            .as("Check %s's %s", "FieldOfStudy1", "Study mode").isEqualTo(expectedFieldOfStudy1.getMode()),
                    () -> assertThat(actualFieldOfStudy1.getTitle())
                            .as("Check %s's %s", "FieldOfStudy1", "Obtained title").isEqualTo(expectedFieldOfStudy1.getTitle()),
                    () -> assertThat(actualFieldOfStudy1.getDepartment())
                            .as("Check %s's %s", "FieldOfStudy1", "Obtained title").isEqualTo(department1),
                    () -> assertThat(actualFieldOfStudy1.getSubjects())
                            .as("Check if %s' %s list contains subject", "actualFieldOfStudy1", "subjects")
                            .isNotNull().isNotEmpty().hasSize(1).contains(subject1).doesNotContain(subject2),
                    () -> assertThat(actualFieldOfStudy1.getStudents())
                            .as("Check if %s' %s list contains student", "actualFieldOfStudy1", "students")
                            .isNotNull().isNotEmpty().hasSize(1).contains(student1).doesNotContain(student2)
            );
            assertAll("FieldOfStudy2 properties",
                    () -> assertThat(actualFieldOfStudy2.getId())
                            .as("Check %s's %s", "FieldOfStudy2", "ID").isEqualTo(expectedFieldOfStudy2.getId()),
                    () -> assertThat(actualFieldOfStudy2.getName())
                            .as("Check %s's %s", "FieldOfStudy2", "Name").isEqualTo(expectedFieldOfStudy2.getName()),
                    () -> assertThat(actualFieldOfStudy2.getLevelOfEducation())
                            .as("Check %s's %s", "FieldOfStudy2", "Level of education").isEqualTo(expectedFieldOfStudy2.getLevelOfEducation()),
                    () -> assertThat(actualFieldOfStudy2.getMode())
                            .as("Check %s's %s", "FieldOfStudy2", "Study mode").isEqualTo(expectedFieldOfStudy2.getMode()),
                    () -> assertThat(actualFieldOfStudy2.getTitle())
                            .as("Check %s's %s", "FieldOfStudy2", "Obtained title").isEqualTo(expectedFieldOfStudy2.getTitle()),
                    () -> assertThat(actualFieldOfStudy2.getDepartment())
                            .as("Check %s's %s", "FieldOfStudy2", "Obtained title").isEqualTo(department2),
                    () -> assertThat(actualFieldOfStudy2.getSubjects())
                            .as("Check if %s' %s list contains subject", "actualFieldOfStudy2", "subjects")
                            .isNotNull().isNotEmpty().hasSize(1).contains(subject2).doesNotContain(subject1),
                    () -> assertThat(actualFieldOfStudy2.getStudents())
                            .as("Check if %s' %s list contains student", "actualFieldOfStudy2", "students")
                            .isNotNull().isNotEmpty().hasSize(1).contains(student2).doesNotContain(student1)
            );
        }
    }

    @Nested
    class FetchByIdTest {
        @Test
        void returnsFieldOfStudyDto_givenId() {
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
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            FieldOfStudyDto actual = service.fetchById(expected.getId());
            //then
            ArgumentCaptor<Long> idArgCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).findById(idArgCaptor.capture());
            Long actualId = idArgCaptor.getValue();
            assertAll("FieldOfStudy properties",
                    () -> assertThat(actualId)
                            .as("Check %s's %s", "FieldOfStudy", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "FieldOfStudy", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s's %s", "FieldOfStudy", "Level of education").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s's %s", "FieldOfStudy", "Study mode").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getTitle())
                            .as("Check %s's %s", "FieldOfStudy", "Obtained title").isEqualTo(expected.getTitle()),
                    () -> assertThat(actual.getDepartment())
                            .as("Check %s's %s", "FieldOfStudy", "Obtained title").isEqualTo(department),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s' %s list contains subject", "actual", "subjects")
                            .isNotNull().isNotEmpty().hasSize(2).contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s' %s list contains student", "actual", "students")
                            .isNotNull().isNotEmpty().hasSize(2).contains(student1, student2)
            );
        }

        @Test
        void throwsIllegalArgumentException_givenWrongId() {
            //given
            Long id = 10L;
            //when
            Throwable thrown = catchThrowable(() -> service.fetchById(id));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Field Of Study ID: " + id);
        }
    }

    @Nested
    class RemoveTest {
        @Test
        void removesFieldOfStudy_givenId() {
            //given
            FieldOfStudy expected = initData.createFieldOfStudyOne(null, List.of(), List.of());
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            service.remove(expected.getId());
            //then
            verify(repository).findById(anyLong());
            verify(repository).delete(any(FieldOfStudy.class));
        }

        @Test
        void throwsIllegalArgumentException_givenWrongId() {
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
        void removeAll_shouldDeleteAllFieldsOfStudy() {
            //given
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            List<FieldOfStudy> fieldsOfStudy = List.of(fieldOfStudy1, fieldOfStudy2);
            //when
            when(repository.findAll()).thenReturn(fieldsOfStudy);
            service.removeAll();
            //then
            verify(repository).findAll();
            verify(repository).deleteAll();
        }
    }

    @Nested
    class FetchAllSubjectsFromFieldOfStudyGroupedBySemestersTest {
        @Test
        void returnsMapOfSemestersAndSubjectsList_givenId() {
            //given
            Long id = 1L;
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectFour(null, List.of());
            List<Subject> subjects = List.of(subject1, subject2, subject3);
            //when
            when(repository.findAllSubjectsFromFieldOfStudy(anyLong())).thenReturn(subjects);
            Map<Semester, List<Subject>> actual = service.fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(id);
            //then
            verify(repository).findAllSubjectsFromFieldOfStudy(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check actual Map content")
                    .containsValues(List.of(subject1, subject3), List.of(subject2));
        }
    }

    @Nested
    class CalculateHoursInEachSemesterFromFieldOfStudyTest {
        @Test
        void returnsMapOfSemestersAndIntegers_givenId() {
            //given
            Long id = 1L;
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectFour(null, List.of());
            List<Subject> subjects = List.of(subject1, subject2, subject3);
            //when
            when(repository.findAllSubjectsFromFieldOfStudy(anyLong())).thenReturn(subjects);
            Map<Semester, Integer> actual = service.calculateHoursInEachSemesterFromFieldOfStudy(id);
            //then
            verify(repository).findAllSubjectsFromFieldOfStudy(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check actual Map content")
                    .containsValues(subject1.getHoursInSemester() + subject3.getHoursInSemester(), subject2.getHoursInSemester());
        }
    }

    @Nested
    class SplitDescriptionTest {
        @Test
        void shouldReturnListOfString_givenStringWithSpecialCharacter() {
            //given
            Long id = 1L;
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            String description = "some;description;test";
            String descItem1 = "some";
            String descItem2 = "description";
            String descItem3 = "test";
            fieldOfStudy.setDescription(description);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(fieldOfStudy));
            List<String> actual = (List<String>) service.splitDescription(id);
            //then
            verify(repository).findById(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).hasSize(3);
            assertThat(actual.get(0)).isEqualTo(descItem1);
            assertThat(actual.get(1)).isEqualTo(descItem2);
            assertThat(actual.get(2)).isEqualTo(descItem3);
        }

        @Test
        void shouldReturnEmptyList_givenNull() {
            //given
            Long id = 1L;
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            fieldOfStudy.setDescription(null);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(fieldOfStudy));
            List<String> actual = (List<String>) service.splitDescription(id);
            //then
            verify(repository).findById(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class CalculateEctsPointsForEachSemesterTest {
        @Test
        void returnsMapOfSemesterAndNumberOfEctsPoints_givenId() {
            //given
            Long id = 1L;
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectFour(null, List.of());
            List<Subject> subjects = List.of(subject1, subject2, subject3);
            //when
            when(repository.findAllSubjectsFromFieldOfStudy(anyLong())).thenReturn(subjects);
            Map<Semester, Integer> actual = service.calculateEctsPointsForEachSemester(id);
            //then
            verify(repository).findAllSubjectsFromFieldOfStudy(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check actual Map content")
                    .containsValues(subject1.getEctsPoints() + subject3.getEctsPoints(), subject2.getEctsPoints());
        }
    }

    @Nested
    class GetSumOfEctsPointsFromAllSemestersTest {
        @Test
        void returnsIntegerValueOfEctsPoints_givenId() {
            //given
            Long id = 1L;
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectFour(null, List.of());
            List<Subject> subjects = List.of(subject1, subject2, subject3);
            int expected = subject1.getEctsPoints() + subject3.getEctsPoints() + subject2.getEctsPoints();
            //when
            when(repository.findAllSubjectsFromFieldOfStudy(anyLong())).thenReturn(subjects);
            Integer actual = service.getSumOfEctsPointsFromAllSemesters(id);
            //then
            verify(repository).findAllSubjectsFromFieldOfStudy(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check return value").isEqualTo(expected);
        }
    }

    @Nested
    class getImagePathTest {
        @Test
        void returnsImagePathAsString_givenFieldOfStudyIdWithImgAssigned() {
            //given
            String imgName = "someImage.jpg";
            String expectedPath = "/img/fields-of-study/" + imgName;
            Long id = 1L;
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyThree(null, List.of(), List.of());
            fieldOfStudy.setImage("someImage.jpg");
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(fieldOfStudy));
            String actual = service.getImagePath(id);
            //then
            verify(repository).findById(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check image path value").isEqualTo(expectedPath);
        }

        @Test
        void returnsDefaultImagePathAsString_givenFieldOfStudyIdWithImgAssigned() {
            //given
            String defaultImgName = "default.jpg";
            String expectedPath = "/img/fields-of-study/" + defaultImgName;
            Long id = 1L;
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyThree(null, List.of(), List.of());
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(fieldOfStudy));
            String actual = service.getImagePath(id);
            //then
            verify(repository).findById(anyLong());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check image path value").isEqualTo(expectedPath);
        }
    }

    @Nested
    class FetchAllByLevelOfEducationSortedByNameTest {
        @Test
        void returnsListOfFieldsOfStudies_filteredByLevelOfEducation_sortedByNameAscending_givenFirstLevelOfEducation() {
            //given
            LevelOfEducation levelOfEducation = LevelOfEducation.FIRST;
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyThree(null, List.of(), List.of());
            FieldOfStudyDto fieldOfStudyDto1 = mapper.map(fieldOfStudy1, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto2 = mapper.map(fieldOfStudy2, FieldOfStudyDto.class);
            List<FieldOfStudy> resultOfRepositorySearch = List.of(fieldOfStudy2, fieldOfStudy1);
            //when
            when(repository.findAllByLevelOfEducation(any(LevelOfEducation.class), any(Sort.class)))
                    .thenReturn(resultOfRepositorySearch);
            List<FieldOfStudyDto> actual = (List<FieldOfStudyDto>) service.fetchAllByLevelOfEducationSortedByName(levelOfEducation);
            //then
            verify(repository).findAllByLevelOfEducation(any(LevelOfEducation.class), any(Sort.class));
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check if result list contains items in exact order")
                    .contains(fieldOfStudyDto1, atIndex(1))
                    .contains(fieldOfStudyDto2, atIndex(0));
        }

        @Test
        void returnsListOfFieldsOfStudies_filteredByLevelOfEducation_sortedByNameAscending_givenSecondLevelOfEducation() {
            //given
            LevelOfEducation levelOfEducation = LevelOfEducation.SECOND;
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyFour(null, List.of(), List.of());
            FieldOfStudyDto fieldOfStudyDto1 = mapper.map(fieldOfStudy1, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto2 = mapper.map(fieldOfStudy2, FieldOfStudyDto.class);
            List<FieldOfStudy> resultOfRepositorySearch = List.of(fieldOfStudy2, fieldOfStudy1);
            //when
            when(repository.findAllByLevelOfEducation(any(LevelOfEducation.class), any(Sort.class)))
                    .thenReturn(resultOfRepositorySearch);
            List<FieldOfStudyDto> actual = (List<FieldOfStudyDto>) service.fetchAllByLevelOfEducationSortedByName(levelOfEducation);
            //then
            verify(repository).findAllByLevelOfEducation(any(LevelOfEducation.class), any(Sort.class));
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check if result list contains items in exact order")
                    .contains(fieldOfStudyDto1, atIndex(1))
                    .contains(fieldOfStudyDto2, atIndex(0));
        }
    }

    @Nested
    class FetchAllGroupedByNameAndSortedByNameTest {
        @Test
        void returnsMap_nameAsKey_listOfFieldsOfStudiesAsValue_sortedByNameAscending() {
            //given
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy4 = initData.createFieldOfStudyFour(null, List.of(), List.of());
            fieldOfStudy2.setName(fieldOfStudy1.getName());
            fieldOfStudy4.setName(fieldOfStudy3.getName());
            FieldOfStudyDto fieldOfStudyDto1 = mapper.map(fieldOfStudy1, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto2 = mapper.map(fieldOfStudy2, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto3 = mapper.map(fieldOfStudy3, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto4 = mapper.map(fieldOfStudy4, FieldOfStudyDto.class);
            List<FieldOfStudy> resultOfRepositoryFindAll =
                    List.of(fieldOfStudy4, fieldOfStudy3, fieldOfStudy1, fieldOfStudy2);
            //when
            when(repository.findAll(any(Sort.class))).thenReturn(resultOfRepositoryFindAll);
            when(repository.findAllByNameContainingIgnoreCase(fieldOfStudy1.getName()))
                    .thenReturn(List.of(fieldOfStudy1, fieldOfStudy2));
            when(repository.findAllByNameContainingIgnoreCase(fieldOfStudy3.getName()))
                    .thenReturn(List.of(fieldOfStudy3, fieldOfStudy4));
            Map<String, List<FieldOfStudyDto>> actual = service.fetchAllGroupedByNameAndSortedByName();
            //then
            verify(repository).findAll(any(Sort.class));
            verify(repository, times(2)).findAllByNameContainingIgnoreCase(anyString());
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check if result map contains correct map entries")
                    .containsEntry(fieldOfStudyDto1.getName(), List.of(fieldOfStudyDto1, fieldOfStudyDto2))
                    .containsEntry(fieldOfStudyDto3.getName(), List.of(fieldOfStudyDto3, fieldOfStudyDto4));
        }
    }

    @Nested
    class FetchAllWithNoDepartmentTest {
        @Test
        void returnsListOfFieldsOfStudies_withNoDepartmentAssigned() {
            //given
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentOne(null, List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy4 = initData.createFieldOfStudyFour(department2, List.of(), List.of());
            FieldOfStudyDto fieldOfStudyDto2 = mapper.map(fieldOfStudy2, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto3 = mapper.map(fieldOfStudy3, FieldOfStudyDto.class);
            List<FieldOfStudy> resultOfRepositoryFindAll =
                    List.of(fieldOfStudy1, fieldOfStudy2, fieldOfStudy3, fieldOfStudy4);
            //when
            when(repository.findAll()).thenReturn(resultOfRepositoryFindAll);
            List<FieldOfStudyDto> actual = service.fetchAllWithNoDepartment();
            //then
            verify(repository).findAll();
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check if result list contains correct values")
                    .contains(fieldOfStudyDto2, fieldOfStudyDto3);
        }
    }

    @Nested
    class FetchAllWithGivenDepartmentDtoOrNoDepartmentTest {
        @Test
        void returnsListOfFieldsOfStudies_withNoDepartmentAssigned_orWithGivenDepartmentAssigned_givenDepartmentDto() {
            //given
            Department department1 = initData.createDepartmentOne(null, List.of());
            DepartmentDto departmentDto1 = mapper.map(department1, DepartmentDto.class);
            Department department2 = initData.createDepartmentOne(null, List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(department1, List.of(), List.of());
            FieldOfStudy fieldOfStudy4 = initData.createFieldOfStudyFour(department2, List.of(), List.of());
            FieldOfStudyDto fieldOfStudyDto1 = mapper.map(fieldOfStudy1, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto2 = mapper.map(fieldOfStudy2, FieldOfStudyDto.class);
            FieldOfStudyDto fieldOfStudyDto3 = mapper.map(fieldOfStudy3, FieldOfStudyDto.class);
            List<FieldOfStudy> resultOfRepositoryFindAll =
                    List.of(fieldOfStudy1, fieldOfStudy2, fieldOfStudy3, fieldOfStudy4);
            //when
            when(repository.findAll()).thenReturn(resultOfRepositoryFindAll);
            List<FieldOfStudyDto> actual = (List<FieldOfStudyDto>) service.fetchAllWithGivenDepartmentDtoOrNoDepartment(departmentDto1);
            //then
            verify(repository, times(2)).findAll();
            verifyNoMoreInteractions(repository);
            assertThat(actual).as("Check if result list contains correct values")
                    .contains(fieldOfStudyDto1, fieldOfStudyDto2, fieldOfStudyDto3);
        }
    }
}