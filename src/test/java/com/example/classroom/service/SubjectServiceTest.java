package com.example.classroom.service;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.enums.Semester;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Subject;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.util.UnitTestsInitData;
import org.assertj.core.groups.Tuple;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    SubjectRepository repository;

    @InjectMocks
    SubjectService service;

    @Spy
    ModelMapper mapper;

    @Spy
    UnitTestsInitData initData;

    @Captor
    private ArgumentCaptor<Subject> argumentCaptor;

    @Nested
    class SaveSubjectTest {
        @Test
        void create_shouldSaveSubject_givenSubjectDto() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            //when
            when(repository.save(any(Subject.class))).thenReturn(expected);
            service.create(dto);
            //then
            verify(repository).save(argumentCaptor.capture());
            Subject actual = argumentCaptor.getValue();
            assertThat(actual).as("Check if %s is not null", "Subject").isNotNull();
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester())
            );
            assertAll("Subject's fieldOfStudy properties",
                    () -> assertThat(actual.getFieldOfStudy().getId())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                    () -> assertThat(actual.getFieldOfStudy().getName())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                    () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                    () -> assertThat(actual.getFieldOfStudy().getMode())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                    () -> assertThat(actual.getFieldOfStudy().getTitle())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                    () -> assertThat(actual.getFieldOfStudy().getSubjects())
                            .as("Check %s's %s properties", "fieldOfStudy", "subjects")
                            .extracting(
                                    Subject::getName,
                                    Subject::getDescription,
                                    Subject::getSemester,
                                    Subject::getHoursInSemester,
                                    Subject::getFieldOfStudy
                            ).containsExactlyInAnyOrder(
                                    Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                            expected.getHoursInSemester(), fieldOfStudy))
            );
            assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
            assertThat(teacher1.getSubjects()).as("Check if %s' %s list contains subject", "teacher1", "subjects")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(teacher2.getSubjects()).as("Check if %s' %s list contains subject", "teacher2", "subjects")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
        }
    }

    @Nested
    class UpdateSubjectTest {
        @Test
        void update_updatesSubject_givenSubjectDto() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Subject entityBeforeUpdate = initData.createSubjectTwo(null, List.of());
            Subject expected = new Subject();
            expected.setId(10L);
            expected.setName("Speech therapy");
            expected.setDescription("Classes with speech therapy specialist.");
            expected.setSemester(Semester.FIRST);
            expected.setHoursInSemester(80);
            expected.setFieldOfStudy(fieldOfStudy);
            expected.addTeacher(teacher1);
            expected.addTeacher(teacher2);
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(entityBeforeUpdate));
            SubjectDto updated = service.update(dto);
            //then
            verify(repository).findById(anyLong());
            Subject actual = mapper.map(updated, Subject.class);

            assertThat(actual).as("Check if %s is not null", "Subject").isNotNull();
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester())
            );
            assertAll("Subject's fieldOfStudy properties",
                    () -> assertThat(actual.getFieldOfStudy().getId())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                    () -> assertThat(actual.getFieldOfStudy().getName())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                    () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                    () -> assertThat(actual.getFieldOfStudy().getMode())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                    () -> assertThat(actual.getFieldOfStudy().getTitle())
                            .as("Check %s's %s %s", "Subject", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                    () -> assertThat(actual.getFieldOfStudy().getSubjects())
                            .as("Check %s's %s properties", "fieldOfStudy", "subjects")
                            .extracting(
                                    Subject::getName,
                                    Subject::getDescription,
                                    Subject::getSemester,
                                    Subject::getHoursInSemester,
                                    Subject::getFieldOfStudy
                            ).containsExactlyInAnyOrder(
                                    Tuple.tuple(expected.getName(), expected.getDescription(), expected.getSemester(),
                                            expected.getHoursInSemester(), fieldOfStudy))
            );
            assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
            assertThat(teacher1.getSubjects())
                    .as("Check if %s' %s list contains subject", "teacher1", "subjects")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(teacher2.getSubjects())
                    .as("Check if %s' %s list contains subject", "teacher2", "subjects")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
        }

        @Test
        void update_throwsIllegalArgumentException_givenWrongSubjectDto() {
            //given
            Subject entity = initData.createSubjectTwo(null, List.of());
            SubjectDto dto = mapper.map(entity, SubjectDto.class);
            dto.setId(10L);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid subject '" + dto + "' with ID: " + dto.getId());
        }
    }

    @Nested
    class FindAllSubjectsTest {
        @Test
        void fetchAll_shouldReturnAllSubjects() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Subject expectedSubject1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Subject expectedSubject2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
            Subject expectedSubject3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
            List<Subject> subjects = List.of(expectedSubject1, expectedSubject2, expectedSubject3);
            //when
            when(repository.findAll()).thenReturn(subjects);
            List<SubjectDto> actual = service.fetchAll();
            //then
            verify(repository).findAll();
            assertThat(actual).as("Check %s's list size", "subjects").isNotNull().isNotEmpty().hasSize(3);
            SubjectDto actualSubject1 = actual.get(0);
            SubjectDto actualSubject2 = actual.get(1);
            SubjectDto actualSubject3 = actual.get(2);
            assertAll("Subject1 properties",
                    () -> assertThat(actualSubject1.getId())
                            .as("Check %s's %s", "Subject1", "ID").isEqualTo(expectedSubject1.getId()),
                    () -> assertThat(actualSubject1.getName())
                            .as("Check %s's %s", "Subject1", "Name").isEqualTo(expectedSubject1.getName()),
                    () -> assertThat(actualSubject1.getDescription())
                            .as("Check %s's %s", "Subject1", "Description").isEqualTo(expectedSubject1.getDescription()),
                    () -> assertThat(actualSubject1.getSemester())
                            .as("Check %s's %s", "Subject1", "Semester").isEqualTo(expectedSubject1.getSemester()),
                    () -> assertThat(actualSubject1.getHoursInSemester())
                            .as("Check %s's %s", "Subject1", "Hours in semester").isEqualTo(expectedSubject1.getHoursInSemester()),
                    () -> assertThat(actualSubject1.getFieldOfStudy())
                            .as("Check %s's %s", "Subject1", "fieldOfStudy").isEqualTo(fieldOfStudy1),
                    () -> assertThat(actualSubject1.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
                                            teacher2.getEmail(), teacher2.getAge()))
            );
            assertAll("Subject2 properties",
                    () -> assertThat(actualSubject2.getId())
                            .as("Check %s's %s", "Subject2", "ID").isEqualTo(expectedSubject2.getId()),
                    () -> assertThat(actualSubject2.getName())
                            .as("Check %s's %s", "Subject2", "Name").isEqualTo(expectedSubject2.getName()),
                    () -> assertThat(actualSubject2.getDescription())
                            .as("Check %s's %s", "Subject2", "Description").isEqualTo(expectedSubject2.getDescription()),
                    () -> assertThat(actualSubject2.getSemester())
                            .as("Check %s's %s", "Subject2", "Semester").isEqualTo(expectedSubject2.getSemester()),
                    () -> assertThat(actualSubject2.getHoursInSemester())
                            .as("Check %s's %s", "Subject2", "Hours in semester").isEqualTo(expectedSubject2.getHoursInSemester()),
                    () -> assertThat(actualSubject2.getFieldOfStudy())
                            .as("Check %s's %s", "Subject2", "fieldOfStudy").isEqualTo(fieldOfStudy1),
                    () -> assertThat(actualSubject2.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
                            .extracting(
                                    Teacher::getId,
                                    Teacher::getFirstName,
                                    Teacher::getLastName,
                                    Teacher::getEmail,
                                    Teacher::getAge
                            ).containsExactlyInAnyOrder(
                                    Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                            teacher3.getEmail(), teacher3.getAge()))
            );
            assertAll("Subject3 properties",
                    () -> assertThat(actualSubject3.getId())
                            .as("Check %s's %s", "Subject3", "ID").isEqualTo(expectedSubject3.getId()),
                    () -> assertThat(actualSubject3.getName())
                            .as("Check %s's %s", "Subject3", "Name").isEqualTo(expectedSubject3.getName()),
                    () -> assertThat(actualSubject3.getDescription())
                            .as("Check %s's %s", "Subject3", "Description").isEqualTo(expectedSubject3.getDescription()),
                    () -> assertThat(actualSubject3.getSemester())
                            .as("Check %s's %s", "Subject3", "Semester").isEqualTo(expectedSubject3.getSemester()),
                    () -> assertThat(actualSubject3.getHoursInSemester())
                            .as("Check %s's %s", "Subject3", "Hours in semester").isEqualTo(expectedSubject3.getHoursInSemester()),
                    () -> assertThat(actualSubject3.getFieldOfStudy())
                            .as("Check %s's %s", "Subject3", "fieldOfStudy").isEqualTo(fieldOfStudy2),
                    () -> assertThat(actualSubject3.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
                                            teacher3.getEmail(), teacher3.getAge()))
            );
        }

        @Test
        void fetchAllPaginated_shouldReturnAllSubjectsPaginated_givenPageNo_pageSize_sortDir() {
            //given
            int pageNo = 2;
            int pageSize = 2;
            String sortField = "name";
            String sortDirection = Sort.Direction.DESC.name();

            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Subject expectedSubject1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Subject expectedSubject2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
            Subject expectedSubject3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
            Page<Subject> subjects = new PageImpl<>(List.of(expectedSubject2));
            //when
            when(repository.findAll(any(Pageable.class))).thenReturn(subjects);
            Page<SubjectDto> actualPage = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
            //then
            verify(repository).findAll(any(Pageable.class));
            List<SubjectDto> actualContent = actualPage.getContent();
            assertThat(actualContent).as("Check %s's list size", "subjects").hasSize(1);
            SubjectDto actualSubject = actualContent.get(0);
            assertAll("Subject2 properties",
                    () -> assertThat(actualSubject.getId())
                            .as("Check %s's %s", "Subject2", "ID").isEqualTo(expectedSubject2.getId()),
                    () -> assertThat(actualSubject.getName())
                            .as("Check %s's %s", "Subject2", "Name").isEqualTo(expectedSubject2.getName()),
                    () -> assertThat(actualSubject.getDescription())
                            .as("Check %s's %s", "Subject2", "Description").isEqualTo(expectedSubject2.getDescription()),
                    () -> assertThat(actualSubject.getSemester())
                            .as("Check %s's %s", "Subject2", "Semester").isEqualTo(expectedSubject2.getSemester()),
                    () -> assertThat(actualSubject.getHoursInSemester())
                            .as("Check %s's %s", "Subject2", "Hours in semester").isEqualTo(expectedSubject2.getHoursInSemester()),
                    () -> assertThat(actualSubject.getFieldOfStudy())
                            .as("Check %s's %s", "Subject2", "fieldOfStudy").isEqualTo(fieldOfStudy1),
                    () -> assertThat(actualSubject.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
                            .extracting(
                                    Teacher::getId,
                                    Teacher::getFirstName,
                                    Teacher::getLastName,
                                    Teacher::getEmail,
                                    Teacher::getAge
                            ).containsExactlyInAnyOrder(
                                    Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                            teacher3.getEmail(), teacher3.getAge()))
            );
        }
    }

    @Nested
    class FindSubjectTest {
        @Test
        void fetchById_shouldFindSubject_givenId() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Subject expected = initData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            SubjectDto actual = service.fetchById(expected.getId());
            //then
            ArgumentCaptor<Long> idArgCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).findById(idArgCaptor.capture());
            Long actualId = idArgCaptor.getValue();
            assertThat(actual).as("Check if %s is not null", "Subject").isNotNull();
            assertAll("Subject's properties",
                    () -> assertThat(actualId)
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
                                            teacher2.getEmail(), teacher2.getAge()))
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
                    .hasMessage("Invalid subject id: " + id);
        }
    }

    @Nested
    class DeleteSubjectTest {
        @Test
        void remove_shouldDeleteSubject_givenId() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Subject expected = initData.createSubjectTwo(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            service.remove(expected.getId());
            //then
            verify(repository).findById(anyLong());
            verify(repository).delete(any(Subject.class));
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
            List<Subject> subjects = List.of(expected1, expected2);
            //when
            when(repository.findAll()).thenReturn(subjects);
            service.removeAll();
            //then
            verify(repository).findAll();
            verify(repository).deleteAll();
        }
    }

    @Nested
    class FindSubjectsByNameTest {
        @Test
        void findByName_shouldReturnSubjectsSearchedByName_givenName() {
            //given
            String name = "i";

            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());

            Subject expectedSubject1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Subject expectedSubject2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
            Subject expectedSubject3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
            List<Subject> subjects = List.of(expectedSubject1, expectedSubject3);
            //when
            when(repository.findAllByNameContainingIgnoreCase(anyString())).thenReturn(subjects);
            List<SubjectDto> actual = service.findByName(name);
            //then
            verify(repository).findAllByNameContainingIgnoreCase(anyString());
            assertThat(actual).as("Check %s list size", "subjects").hasSize(2);
            SubjectDto actualSubject1 = actual.get(0);
            SubjectDto actualSubject2 = actual.get(1);
            assertAll("Subject1 properties",
                    () -> assertThat(actualSubject1.getId())
                            .as("Check %s's %s", "Subject1", "ID").isEqualTo(expectedSubject1.getId()),
                    () -> assertThat(actualSubject1.getName())
                            .as("Check %s's %s", "Subject1", "Name").isEqualTo(expectedSubject1.getName()),
                    () -> assertThat(actualSubject1.getDescription())
                            .as("Check %s's %s", "Subject1", "Description").isEqualTo(expectedSubject1.getDescription()),
                    () -> assertThat(actualSubject1.getSemester())
                            .as("Check %s's %s", "Subject1", "Semester").isEqualTo(expectedSubject1.getSemester()),
                    () -> assertThat(actualSubject1.getHoursInSemester())
                            .as("Check %s's %s", "Subject1", "Hours in semester").isEqualTo(expectedSubject1.getHoursInSemester()),
                    () -> assertThat(actualSubject1.getFieldOfStudy())
                            .as("Check %s's %s", "Subject1", "fieldOfStudy").isEqualTo(expectedSubject1.getFieldOfStudy()),
                    () -> assertThat(actualSubject1.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
                                            teacher2.getEmail(), teacher2.getAge()))
            );
            assertAll("Subject2 properties",
                    () -> assertThat(actualSubject2.getId())
                            .as("Check %s's %s", "Subject2", "ID").isEqualTo(expectedSubject3.getId()),
                    () -> assertThat(actualSubject2.getName())
                            .as("Check %s's %s", "Subject2", "Name").isEqualTo(expectedSubject3.getName()),
                    () -> assertThat(actualSubject2.getDescription())
                            .as("Check %s's %s", "Subject2", "Description").isEqualTo(expectedSubject3.getDescription()),
                    () -> assertThat(actualSubject2.getSemester())
                            .as("Check %s's %s", "Subject2", "Semester").isEqualTo(expectedSubject3.getSemester()),
                    () -> assertThat(actualSubject2.getHoursInSemester())
                            .as("Check %s's %s", "Subject2", "Hours in semester").isEqualTo(expectedSubject3.getHoursInSemester()),
                    () -> assertThat(actualSubject2.getFieldOfStudy())
                            .as("Check %s's %s", "Subject2", "fieldOfStudy").isEqualTo(expectedSubject3.getFieldOfStudy()),
                    () -> assertThat(actualSubject2.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
                                            teacher3.getEmail(), teacher3.getAge()))
            );
        }

        @Test
        void findByNamePaginated_shouldReturnSubjectsSearchedByNamePaginated_givenName_pageNo_pageSize_sortDir() {
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

            Subject expectedSubject1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Subject expectedSubject2 = initData.createSubjectTwo(fieldOfStudy1, List.of(teacher3));
            Subject expectedSubject3 = initData.createSubjectThree(fieldOfStudy2, List.of(teacher1, teacher2, teacher3));
            Page<Subject> subjects = new PageImpl<>(List.of(expectedSubject1));
            //when
            when(repository.findAllByNameContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(subjects);
            Page<SubjectDto> actual = service.findByNamePaginated(pageNo, pageSize, sortField, sortDirection, name);
            //then
            verify(repository).findAllByNameContainingIgnoreCase(anyString(), any(Pageable.class));
            List<SubjectDto> content = actual.getContent();
            assertThat(content).as("Check %s's list size", "subjects").hasSize(1);
            SubjectDto actualSubject = content.get(0);
            assertAll("Subject1 properties",
                    () -> assertThat(actualSubject.getId())
                            .as("Check %s's %s", "Subject1", "ID").isEqualTo(expectedSubject1.getId()),
                    () -> assertThat(actualSubject.getName())
                            .as("Check %s's %s", "Subject1", "Name").isEqualTo(expectedSubject1.getName()),
                    () -> assertThat(actualSubject.getDescription())
                            .as("Check %s's %s", "Subject1", "Description").isEqualTo(expectedSubject1.getDescription()),
                    () -> assertThat(actualSubject.getSemester())
                            .as("Check %s's %s", "Subject1", "Semester").isEqualTo(expectedSubject1.getSemester()),
                    () -> assertThat(actualSubject.getHoursInSemester())
                            .as("Check %s's %s", "Subject1", "Hours in semester").isEqualTo(expectedSubject1.getHoursInSemester()),
                    () -> assertThat(actualSubject.getFieldOfStudy())
                            .as("Check %s's %s", "Subject1", "fieldOfStudy").isEqualTo(expectedSubject1.getFieldOfStudy()),
                    () -> assertThat(actualSubject.getTeachers()).as("Check %s's %s properties", "Subject", "teachers")
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
                                            teacher2.getEmail(), teacher2.getAge()))
            );
        }
    }
}