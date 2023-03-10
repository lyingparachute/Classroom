package com.example.classroom.service;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.model.*;
import com.example.classroom.repository.TeacherRepository;
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
class TeacherServiceTest {

    @Mock
    TeacherRepository repository;

    @InjectMocks
    TeacherService service;

    @Spy
    ModelMapper mapper;

    @Spy
    UnitTestsInitData initData;

    @Captor
    private ArgumentCaptor<Teacher> argumentCaptor;

    @Nested
    class SaveTeacherTest {
        @Test
        void create_shouldSaveTeacher_givenTeacherDto() {
            //given
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(department, List.of(), List.of(student1, student2));
            Subject subject1 = initData.createSubjectOne(fieldOfStudy, List.of());
            Subject subject2 = initData.createSubjectTwo(fieldOfStudy, List.of());

            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of());
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            //when
            when(repository.save(any(Teacher.class))).thenReturn(expected);
            service.create(dto);
            //then
            verify(repository).save(argumentCaptor.capture());
            Teacher actual = argumentCaptor.getValue();
            assertThat(actual).as("Check if %s is not null", "Teacher").isNotNull();
            assertAll("Teacher's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Teacher", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s's %s", "Teacher", "First Name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s's %s", "Teacher", "Last Name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s's %s", "Teacher", "Email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s's %s", "Teacher", "Age").isEqualTo(expected.getAge())
            );
            assertThat(actual.getDepartment()).as("Check if %s is not null", "Teacher's department").isNotNull();
            assertAll("Teacher's fieldOfStudy properties",
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s %s", "Teacher", "department", "Id").isEqualTo(department.getId()),
                    () -> assertThat(actual.getDepartment().getName())
                            .as("Check %s's %s %s", "Teacher", "department", "Name").isEqualTo(department.getName()),
                    () -> assertThat(actual.getDepartment().getAddress())
                            .as("Check %s's %s %s", "Teacher", "department", "Address").isEqualTo(department.getAddress()),
                    () -> assertThat(actual.getDepartment().getTelNumber())
                            .as("Check %s's %s %s", "Teacher", "department", "Telephone Number").isEqualTo(department.getTelNumber()),
                    () -> assertThat(actual.getDepartment().getDean())
                            .as("Check %s's %s", "Department", "dean")
                            .isNotNull().isEqualTo(expected)
            );
            assertThat(actual.getSubjects()).as("Check %s's %s properties", "Teacher", "subjects")
                    .extracting(
                            Subject::getId,
                            Subject::getName,
                            Subject::getDescription,
                            Subject::getSemester,
                            Subject::getHoursInSemester
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                    subject1.getSemester(), subject1.getHoursInSemester()),
                            Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                    subject2.getSemester(), subject2.getHoursInSemester()));
            assertThat(actual.getStudents()).as("Check %s's %s properties", "Teacher", "students")
                    .extracting(
                            Student::getId,
                            Student::getFirstName,
                            Student::getLastName,
                            Student::getEmail,
                            Student::getAge
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                    student1.getEmail(), student1.getAge()),
                            Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                    student2.getEmail(), student2.getAge()));
            assertThat(subject1.getTeachers()).as("Check if %s' %s list contains teacher", "subject1", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(subject2.getTeachers()).as("Check if %s' %s list contains teacher", "subject2", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(student1.getTeachers()).as("Check if %s' %s list contains teacher", "student1", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(student2.getTeachers()).as("Check if %s' %s list contains teacher", "student2", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
        }
    }

    @Nested
    class UpdateTeacherTest {
        @Test
        void update_shouldUpdateTeacher_givenTeacherDto() {
            //given
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(department, List.of(), List.of(student1, student2));
            Subject subject1 = initData.createSubjectOne(fieldOfStudy, List.of());
            Subject subject2 = initData.createSubjectTwo(fieldOfStudy, List.of());
            Teacher entityBeforeUpdate = initData.createTeacherOne(null, List.of(), List.of());
            Teacher expected = new Teacher();
            expected.setId(entityBeforeUpdate.getId());
            expected.setFirstName("Fabian");
            expected.setLastName("Graczyk");
            expected.setEmail("f.graczyk@gmail.com");
            expected.setAge(55);
            expected.setDepartment(department);
            expected.addSubject(subject1);
            expected.addSubject(subject2);
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(entityBeforeUpdate));
            TeacherDto updated = service.update(dto);
            //then
            verify(repository).findById(anyLong());

//            Teacher actual = argumentCaptor.getValue();
            Teacher actual = mapper.map(updated, Teacher.class);
            assertThat(actual).as("Check if %s is not null", "Teacher").isNotNull();
            assertAll("Teacher's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Teacher", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s's %s", "Teacher", "First Name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s's %s", "Teacher", "Last Name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s's %s", "Teacher", "Email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s's %s", "Teacher", "Age").isEqualTo(expected.getAge())
            );
            assertThat(actual.getDepartment()).as("Check if %s is not null", "Teacher's department").isNotNull();
            assertAll("Teacher's fieldOfStudy properties",
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s %s", "Teacher", "department", "Id").isEqualTo(department.getId()),
                    () -> assertThat(actual.getDepartment().getName())
                            .as("Check %s's %s %s", "Teacher", "department", "Name").isEqualTo(department.getName()),
                    () -> assertThat(actual.getDepartment().getAddress())
                            .as("Check %s's %s %s", "Teacher", "department", "Address").isEqualTo(department.getAddress()),
                    () -> assertThat(actual.getDepartment().getTelNumber())
                            .as("Check %s's %s %s", "Teacher", "department", "Telephone Number").isEqualTo(department.getTelNumber()),
                    () -> assertThat(actual.getDepartment().getDean())
                            .as("Check %s's %s", "Department", "dean")
                            .isNotNull().isEqualTo(expected)
            );
            assertThat(actual.getSubjects()).as("Check %s's %s properties", "Teacher", "subjects")
                    .extracting(
                            Subject::getId,
                            Subject::getName,
                            Subject::getDescription,
                            Subject::getSemester,
                            Subject::getHoursInSemester
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                    subject1.getSemester(), subject1.getHoursInSemester()),
                            Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                    subject2.getSemester(), subject2.getHoursInSemester()));
            assertThat(actual.getStudents()).as("Check %s's %s properties", "Teacher", "students")
                    .extracting(
                            Student::getId,
                            Student::getFirstName,
                            Student::getLastName,
                            Student::getEmail,
                            Student::getAge
                    ).containsExactlyInAnyOrder(
                            Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                    student1.getEmail(), student1.getAge()),
                            Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                    student2.getEmail(), student2.getAge()));
            assertThat(subject1.getTeachers()).as("Check if %s' %s list contains teacher", "subject1", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(subject2.getTeachers()).as("Check if %s' %s list contains teacher", "subject2", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(student1.getTeachers()).as("Check if %s' %s list contains teacher", "student1", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(student2.getTeachers()).as("Check if %s' %s list contains teacher", "student2", "teachers")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
        }

        @Test
        void update_throwsIllegalArgumentException_givenWrongTeacherDto() {
            //given
            TeacherDto dto = new TeacherDto();
            dto.setId(9L);
            dto.setFirstName("Alison");
            dto.setLastName("Becker");
            dto.setEmail("a.becker@gmail.com");
            dto.setAge(55);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid teacher '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId());
        }
    }

    @Nested
    class FindAllTeachersTest {
        @Test
        void fetchAll_shouldReturnAllTeachers() {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Department department3 = initData.createDepartmentThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());

            Teacher expectedTeacher1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
            Teacher expectedTeacher2 = initData.createTeacherTwo(department2, List.of(subject2), List.of(student2));
            Teacher expectedTeacher3 = initData.createTeacherThree(department3, List.of(subject3), List.of(student3));
            List<Teacher> teachers = List.of(expectedTeacher1, expectedTeacher2, expectedTeacher3);
            //when
            when(repository.findAll()).thenReturn(teachers);
            List<TeacherDto> actual = service.fetchAll();
            //then
            verify(repository).findAll();
            assertThat(actual).as("Check %s's list size", "teachers").hasSize(3);
            TeacherDto actualTeacher1 = actual.get(0);
            TeacherDto actualTeacher2 = actual.get(1);
            TeacherDto actualTeacher3 = actual.get(2);
            assertAll("Teacher1 properties",
                    () -> assertThat(actualTeacher1.getId())
                            .as("Check %s's %s", "Teacher1", "ID").isEqualTo(expectedTeacher1.getId()),
                    () -> assertThat(actualTeacher1.getFirstName())
                            .as("Check %s's %s", "Teacher1", "First Name").isEqualTo(expectedTeacher1.getFirstName()),
                    () -> assertThat(actualTeacher1.getLastName())
                            .as("Check %s's %s", "Teacher1", "Last Name").isEqualTo(expectedTeacher1.getLastName()),
                    () -> assertThat(actualTeacher1.getEmail())
                            .as("Check %s's %s", "Teacher1", "Email").isEqualTo(expectedTeacher1.getEmail()),
                    () -> assertThat(actualTeacher1.getAge())
                            .as("Check %s's %s", "Teacher1", "Age").isEqualTo(expectedTeacher1.getAge()),
                    () -> assertThat(actualTeacher1.getDepartment())
                            .as("Check %s's %s", "Teacher1", "Department").isEqualTo(department1),
                    () -> assertThat(actualTeacher1.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher1", "subjects")
                            .contains(subject1, subject2)
                            .doesNotContain(subject3),
                    () -> assertThat(actualTeacher1.getStudents())
                            .as("Check if %s contains %s", "actualTeacher1", "students")
                            .contains(student1, student2)
                            .doesNotContain(student3)
            );
            assertAll("Teacher2 properties",
                    () -> assertThat(actualTeacher2.getId())
                            .as("Check %s's %s", "Teacher2", "ID").isEqualTo(expectedTeacher2.getId()),
                    () -> assertThat(actualTeacher2.getFirstName())
                            .as("Check %s's %s", "Teacher2", "First Name").isEqualTo(expectedTeacher2.getFirstName()),
                    () -> assertThat(actualTeacher2.getLastName())
                            .as("Check %s's %s", "Teacher2", "Last Name").isEqualTo(expectedTeacher2.getLastName()),
                    () -> assertThat(actualTeacher2.getEmail())
                            .as("Check %s's %s", "Teacher2", "Email").isEqualTo(expectedTeacher2.getEmail()),
                    () -> assertThat(actualTeacher2.getAge())
                            .as("Check %s's %s", "Teacher2", "Age").isEqualTo(expectedTeacher2.getAge()),
                    () -> assertThat(actualTeacher2.getDepartment())
                            .as("Check %s's %s", "Teacher2", "Department").isEqualTo(department2),
                    () -> assertThat(actualTeacher2.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher2", "subjects")
                            .contains(subject2)
                            .doesNotContain(subject1, subject3),
                    () -> assertThat(actualTeacher2.getStudents())
                            .as("Check if %s contains %s", "actualTeacher2", "students")
                            .contains(student2)
                            .doesNotContain(student1, student3)
            );
            assertAll("Teacher3 properties",
                    () -> assertThat(actualTeacher3.getId())
                            .as("Check %s's %s", "Teacher3", "ID").isEqualTo(expectedTeacher3.getId()),
                    () -> assertThat(actualTeacher3.getFirstName())
                            .as("Check %s's %s", "Teacher3", "First Name").isEqualTo(expectedTeacher3.getFirstName()),
                    () -> assertThat(actualTeacher3.getLastName())
                            .as("Check %s's %s", "Teacher3", "Last Name").isEqualTo(expectedTeacher3.getLastName()),
                    () -> assertThat(actualTeacher3.getEmail())
                            .as("Check %s's %s", "Teacher3", "Email").isEqualTo(expectedTeacher3.getEmail()),
                    () -> assertThat(actualTeacher3.getAge())
                            .as("Check %s's %s", "Teacher3", "Age").isEqualTo(expectedTeacher3.getAge()),
                    () -> assertThat(actualTeacher3.getDepartment())
                            .as("Check %s's %s", "Teacher3", "Department").isEqualTo(department3),
                    () -> assertThat(actualTeacher3.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher3", "subjects")
                            .contains(subject3)
                            .doesNotContain(subject1, subject2),
                    () -> assertThat(actualTeacher3.getStudents())
                            .as("Check if %s contains %s", "actualTeacher3", "students")
                            .contains(student3)
                            .doesNotContain(student1, student2)
            );
        }

        @Test
        void fetchAllPaginated_shouldReturnAllTeachersPaginated_givenPageNo_pageSize_sortDir() {
            //given
            int pageNo = 2;
            int pageSize = 2;
            String sortField = "firstName";
            String sortDirection = Sort.Direction.DESC.name();

            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Department department3 = initData.createDepartmentThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());

            Teacher expectedTeacher1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
            Teacher expectedTeacher2 = initData.createTeacherTwo(department2, List.of(subject2), List.of(student2));
            Teacher expectedTeacher3 = initData.createTeacherThree(department3, List.of(subject3), List.of(student3));
            Page<Teacher> teachers = new PageImpl<>(List.of(expectedTeacher3));
            //when
            when(repository.findAll(any(Pageable.class))).thenReturn(teachers);
            Page<TeacherDto> actual = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
            //then
            verify(repository).findAll(any(Pageable.class));
            List<TeacherDto> actualContent = actual.getContent();
            assertThat(actualContent).as("Check %s's list size", "teachers").hasSize(1);
            TeacherDto actualTeacher = actualContent.get(0);
            assertAll("Teacher3 properties",
                    () -> assertThat(actualTeacher.getId())
                            .as("Check %s's %s", "Teacher3", "ID").isEqualTo(expectedTeacher3.getId()),
                    () -> assertThat(actualTeacher.getFirstName())
                            .as("Check %s's %s", "Teacher3", "First Name").isEqualTo(expectedTeacher3.getFirstName()),
                    () -> assertThat(actualTeacher.getLastName())
                            .as("Check %s's %s", "Teacher3", "Last Name").isEqualTo(expectedTeacher3.getLastName()),
                    () -> assertThat(actualTeacher.getEmail())
                            .as("Check %s's %s", "Teacher3", "Email").isEqualTo(expectedTeacher3.getEmail()),
                    () -> assertThat(actualTeacher.getAge())
                            .as("Check %s's %s", "Teacher3", "Age").isEqualTo(expectedTeacher3.getAge()),
                    () -> assertThat(actualTeacher.getDepartment())
                            .as("Check %s's %s", "Teacher3", "Department").isEqualTo(department3),
                    () -> assertThat(actualTeacher.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher", "subjects")
                            .contains(subject3)
                            .doesNotContain(subject1, subject2),
                    () -> assertThat(actualTeacher.getStudents())
                            .as("Check if %s contains %s", "actualTeacher", "students")
                            .contains(student3)
                            .doesNotContain(student1, student2)
            );
        }
    }

    @Nested
    class FindTeacherTest {
        @Test
        void fetchById_shouldFindStudent_givenId() {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());

            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            TeacherDto actual = service.fetchById(expected.getId());
            //then
            ArgumentCaptor<Long> idArgCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).findById(idArgCaptor.capture());
            Long actualId = idArgCaptor.getValue();

            assertThat(actual).as("Check if %s is not null", "Teacher").isNotNull();
            assertAll("Teacher properties",
                    () -> assertThat(actualId)
                            .as("Check %s's %s", "Teacher", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s's %s", "Teacher", "First Name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s's %s", "Teacher", "Last Name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s's %s", "Teacher", "Email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s's %s", "Teacher", "Age").isEqualTo(expected.getAge()),
                    () -> assertThat(actual.getDepartment())
                            .as("Check %s's %s", "Teacher", "Department").isEqualTo(department),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher", "subjects")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "actualTeacher", "students")
                            .contains(student1, student2)
            );
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
                    .hasMessage("Invalid teacher ID: " + id);
        }
    }

    @Nested
    class DeleteTeacherTest {
        @Test
        void remove_shouldRemoveStudent_givenId() {
            Teacher expected = initData.createTeacherOne(null, List.of(), List.of());
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            service.remove(expected.getId());
            //then
            verify(repository).findById(anyLong());
            verify(repository).delete(any(Teacher.class));
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
                    .hasMessage("Invalid teacher ID: " + id);
        }

        @Test
        void removeAll_shouldDeleteAllTeachers() {
            //given
            Teacher expectedTeacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher expectedTeacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            List<Teacher> teachers = List.of(expectedTeacher1, expectedTeacher2);
            //when
            when(repository.findAll()).thenReturn(teachers);
            service.removeAll();
            //then
            verify(repository).findAll();
            verify(repository).deleteAll();
        }
    }

    @Nested
    class FindTeachersByFirstOrLastNameTest {
        @Test
        void findByFirstOrLastName_returnsTeachersSearchedByFirstOrLastName_givenName() {
            //given
            String name = "ja";
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Department department3 = initData.createDepartmentThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());

            Teacher expectedTeacher1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
            Teacher expectedTeacher2 = initData.createTeacherTwo(department2, List.of(subject2), List.of(student2));
            Teacher expectedTeacher3 = initData.createTeacherThree(department3, List.of(subject3), List.of(student3));
            List<Teacher> teachers = List.of(expectedTeacher2, expectedTeacher3);
            //when
            when(repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString()))
                    .thenReturn(teachers);
            List<TeacherDto> actual = service.findByFirstOrLastName(name);
            //then
            verify(repository).findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString());
            assertThat(actual).as("Check %s's list size", "teachers").hasSize(2);
            TeacherDto actualTeacher1 = actual.get(0);
            TeacherDto actualTeacher2 = actual.get(1);
            assertAll("Teacher2 properties",
                    () -> assertThat(actualTeacher1.getId())
                            .as("Check %s's %s", "Teacher2", "ID").isEqualTo(expectedTeacher2.getId()),
                    () -> assertThat(actualTeacher1.getFirstName())
                            .as("Check %s's %s", "Teacher2", "First Name").isEqualTo(expectedTeacher2.getFirstName()),
                    () -> assertThat(actualTeacher1.getLastName())
                            .as("Check %s's %s", "Teacher2", "Last Name").isEqualTo(expectedTeacher2.getLastName()),
                    () -> assertThat(actualTeacher1.getEmail())
                            .as("Check %s's %s", "Teacher2", "Email").isEqualTo(expectedTeacher2.getEmail()),
                    () -> assertThat(actualTeacher1.getAge())
                            .as("Check %s's %s", "Teacher2", "Age").isEqualTo(expectedTeacher2.getAge()),
                    () -> assertThat(actualTeacher1.getDepartment())
                            .as("Check %s's %s", "Teacher2", "Department").isEqualTo(department2),
                    () -> assertThat(actualTeacher1.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher1", "subjects")
                            .contains(subject2)
                            .doesNotContain(subject1, subject3),
                    () -> assertThat(actualTeacher1.getStudents())
                            .as("Check if %s contains %s", "actualTeacher1", "students")
                            .contains(student2)
                            .doesNotContain(student1, student3)
            );
            assertAll("Teacher3 properties",
                    () -> assertThat(actualTeacher2.getId())
                            .as("Check %s's %s", "Teacher3", "ID").isEqualTo(expectedTeacher3.getId()),
                    () -> assertThat(actualTeacher2.getFirstName())
                            .as("Check %s's %s", "Teacher3", "First Name").isEqualTo(expectedTeacher3.getFirstName()),
                    () -> assertThat(actualTeacher2.getLastName())
                            .as("Check %s's %s", "Teacher3", "Last Name").isEqualTo(expectedTeacher3.getLastName()),
                    () -> assertThat(actualTeacher2.getEmail())
                            .as("Check %s's %s", "Teacher3", "Email").isEqualTo(expectedTeacher3.getEmail()),
                    () -> assertThat(actualTeacher2.getAge())
                            .as("Check %s's %s", "Teacher3", "Age").isEqualTo(expectedTeacher3.getAge()),
                    () -> assertThat(actualTeacher2.getDepartment())
                            .as("Check %s's %s", "Teacher3", "Department").isEqualTo(department3),
                    () -> assertThat(actualTeacher2.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher2", "subjects")
                            .contains(subject3)
                            .doesNotContain(subject1, subject2),
                    () -> assertThat(actualTeacher2.getStudents())
                            .as("Check if %s contains %s", "actualTeacher2", "students")
                            .contains(student3)
                            .doesNotContain(student1, student2)
            );
        }

        @Test
        void findByFirstOrLastNamePaginated_returnsTeachersSearchedByFirstOrLastNamePaginated_givenName_pageNo_pageSize_sortDir() {
            //given
            String name = "ja";
            int pageNo = 1;
            int pageSize = 1;
            String sortField = "firstName";
            String sortDirection = Sort.Direction.DESC.name();

            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Department department3 = initData.createDepartmentThree(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());

            Teacher expectedTeacher1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
            Teacher expectedTeacher2 = initData.createTeacherTwo(department2, List.of(subject2), List.of(student2));
            Teacher expectedTeacher3 = initData.createTeacherThree(department3, List.of(subject3), List.of(student3));
            Page<Teacher> teachers = new PageImpl<>(List.of(expectedTeacher3));
            //when
            when(repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                    .thenReturn(teachers);
            Page<TeacherDto> actual = service.findByFirstOrLastNamePaginated(pageNo, pageSize, sortField, sortDirection, name);
            //then
            verify(repository).findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), any(Pageable.class));
            List<TeacherDto> content = actual.getContent();
            assertThat(content).as("Check %s's list size", "students").hasSize(1);
            TeacherDto actualTeacher = content.get(0);
            assertAll("Teacher3 properties",
                    () -> assertThat(actualTeacher.getId())
                            .as("Check %s's %s", "Teacher3", "ID").isEqualTo(expectedTeacher3.getId()),
                    () -> assertThat(actualTeacher.getFirstName())
                            .as("Check %s's %s", "Teacher3", "First Name").isEqualTo(expectedTeacher3.getFirstName()),
                    () -> assertThat(actualTeacher.getLastName())
                            .as("Check %s's %s", "Teacher3", "Last Name").isEqualTo(expectedTeacher3.getLastName()),
                    () -> assertThat(actualTeacher.getEmail())
                            .as("Check %s's %s", "Teacher3", "Email").isEqualTo(expectedTeacher3.getEmail()),
                    () -> assertThat(actualTeacher.getAge())
                            .as("Check %s's %s", "Teacher3", "Age").isEqualTo(expectedTeacher3.getAge()),
                    () -> assertThat(actualTeacher.getDepartment())
                            .as("Check %s's %s", "Teacher3", "Department").isEqualTo(department3),
                    () -> assertThat(actualTeacher.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher", "subjects")
                            .contains(subject3)
                            .doesNotContain(subject1, subject2),
                    () -> assertThat(actualTeacher.getStudents())
                            .as("Check if %s contains %s", "actualTeacher", "students")
                            .contains(student3)
                            .doesNotContain(student1, student2)
            );

        }
    }
}