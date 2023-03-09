package com.example.classroom.service;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Student;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.StudentRepository;
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
class StudentServiceTest {

    @Mock
    StudentRepository repository;

    @InjectMocks
    StudentService service;

    @Spy
    ModelMapper mapper;

    @Spy
    UnitTestsInitData initData;

    @Captor
    private ArgumentCaptor<Student> argumentCaptor;

    @Nested
    class SaveStudentTest {
        @Test
        void create_shouldSaveStudent_givenStudentDto() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            StudentDto dto = mapper.map(expected, StudentDto.class);
            //when
            when(repository.save(any(Student.class))).thenReturn(expected);
            service.create(dto);
            //then
            verify(repository).save(argumentCaptor.capture());
            Student actual = argumentCaptor.getValue();
            assertThat(actual).as("Check if %s is not null", "Student").isNotNull();
            assertAll("Student's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Student", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s's %s", "Student", "First Name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s's %s", "Student", "Last Name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s's %s", "Student", "Email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s's %s", "Student", "Age").isEqualTo(expected.getAge())
            );
            assertThat(actual.getFieldOfStudy()).as("Check if %s is not null", "Student's fieldOfStudy").isNotNull();
            assertAll("Student's fieldOfStudy properties",
                    () -> assertThat(actual.getFieldOfStudy().getId())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                    () -> assertThat(actual.getFieldOfStudy().getName())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                    () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                    () -> assertThat(actual.getFieldOfStudy().getMode())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                    () -> assertThat(actual.getFieldOfStudy().getTitle())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                    () -> assertThat(actual.getFieldOfStudy().getStudents())
                            .as("Check %s's %s properties", "fieldOfStudy", "students")
                            .extracting(
                                    Student::getId,
                                    Student::getFirstName,
                                    Student::getLastName,
                                    Student::getEmail,
                                    Student::getAge,
                                    Student::getFieldOfStudy
                            ).containsExactlyInAnyOrder(
                                    Tuple.tuple(expected.getId(), expected.getFirstName(), expected.getLastName(),
                                            expected.getEmail(), expected.getAge(), fieldOfStudy))
            );
            assertThat(actual.getTeachers()).as("Check %s's %s properties", "Student", "teachers")
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
            assertThat(teacher1.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher1", "students")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(teacher2.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher2", "students")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
        }
    }

    @Nested
    class UpdateStudentTest {
        @Test
        void update_shouldUpdateStudent_givenStudentDto() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Student entityBeforeUpdate = initData.createStudentOne(null, List.of());
            Student expected = new Student();
            expected.setId(10L);
            expected.setFirstName("Pamela");
            expected.setLastName("Gonzales");
            expected.setEmail("p.gonzales@gmail.com");
            expected.setAge(20);
            expected.setFieldOfStudy(fieldOfStudy);
            expected.addTeacher(teacher1);
            expected.addTeacher(teacher2);
            StudentDto dto = mapper.map(expected, StudentDto.class);
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(entityBeforeUpdate));
            StudentDto updated = service.update(dto);
            //then
            verify(repository).findById(anyLong());
            Student actual = mapper.map(updated, Student.class);
            assertThat(actual).as("Check if %s is not null", "Student").isNotNull();
            assertAll("Student's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Student", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s's %s", "Student", "First Name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s's %s", "Student", "Last Name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s's %s", "Student", "Email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s's %s", "Student", "Age").isEqualTo(expected.getAge())
            );
            assertThat(actual.getFieldOfStudy()).as("Check if %s is not null", "Student's fieldOfStudy").isNotNull();
            assertAll("Student's fieldOfStudy properties",
                    () -> assertThat(actual.getFieldOfStudy().getId())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Id").isEqualTo(fieldOfStudy.getId()),
                    () -> assertThat(actual.getFieldOfStudy().getName())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Name").isEqualTo(fieldOfStudy.getName()),
                    () -> assertThat(actual.getFieldOfStudy().getLevelOfEducation())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Level of education").isEqualTo(fieldOfStudy.getLevelOfEducation()),
                    () -> assertThat(actual.getFieldOfStudy().getMode())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Study mode").isEqualTo(fieldOfStudy.getMode()),
                    () -> assertThat(actual.getFieldOfStudy().getTitle())
                            .as("Check %s's %s %s", "Student", "fieldOfStudy", "Obtained title").isEqualTo(fieldOfStudy.getTitle()),
                    () -> assertThat(actual.getFieldOfStudy().getStudents())
                            .as("Check %s's %s properties", "fieldOfStudy", "students")
                            .extracting(
                                    Student::getId,
                                    Student::getFirstName,
                                    Student::getLastName,
                                    Student::getEmail,
                                    Student::getAge,
                                    Student::getFieldOfStudy
                            ).containsExactlyInAnyOrder(
                                    Tuple.tuple(expected.getId(), expected.getFirstName(), expected.getLastName(),
                                            expected.getEmail(), expected.getAge(), fieldOfStudy))
            );
            assertThat(actual.getTeachers()).as("Check %s's %s properties", "Student", "teachers")
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
            assertThat(teacher1.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher1", "students")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
            assertThat(teacher2.getStudents()).as("Check if %s' %s list has exact size and contains student", "teacher2", "students")
                    .isNotNull().isNotEmpty().hasSize(1).contains(actual);
        }

        @Test
        void update_throwsIllegalArgumentException_givenWrongStudentDto() {
            //given
            Student entity = initData.createStudentOne(null, List.of());
            StudentDto dto = mapper.map(entity, StudentDto.class);
            dto.setId(10L);
            //when
            Throwable thrown = catchThrowable(() -> service.update(dto));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Student '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId());
        }
    }

    @Nested
    class FindAllStudentsTest {
        @Test
        void fetchAll_shouldReturnAllStudents() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

            Student expectedStudent1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Student expectedStudent2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher3));
            Student expectedStudent3 = initData.createStudentThree(fieldOfStudy3, List.of(teacher1, teacher2, teacher3));
            List<Student> students = List.of(expectedStudent1, expectedStudent2, expectedStudent3);
            //when
            when(repository.findAll()).thenReturn(students);
            List<StudentDto> actual = service.fetchAll();
            //then
            verify(repository).findAll();
            assertThat(actual).as("Check %s's list size", "students").hasSize(3);
            StudentDto actualStudent1 = actual.get(0);
            StudentDto actualStudent2 = actual.get(1);
            StudentDto actualStudent3 = actual.get(2);
            assertAll("Student1 properties",
                    () -> assertThat(actualStudent1.getId())
                            .as("Check %s's %s", "Student1", "ID").isEqualTo(expectedStudent1.getId()),
                    () -> assertThat(actualStudent1.getFirstName())
                            .as("Check %s's %s", "Student1", "First Name").isEqualTo(expectedStudent1.getFirstName()),
                    () -> assertThat(actualStudent1.getLastName())
                            .as("Check %s's %s", "Student1", "Last Name").isEqualTo(expectedStudent1.getLastName()),
                    () -> assertThat(actualStudent1.getEmail())
                            .as("Check %s's %s", "Student1", "Email").isEqualTo(expectedStudent1.getEmail()),
                    () -> assertThat(actualStudent1.getAge())
                            .as("Check %s's %s", "Student1", "Age").isEqualTo(expectedStudent1.getAge()),
                    () -> assertThat(actualStudent1.getFieldOfStudy())
                            .as("Check %s's %s", "Student1", "fieldOfStudy").isEqualTo(fieldOfStudy1),
                    () -> assertThat(actualStudent1.getTeachers())
                            .as("Check if %s contains %s", "actualStudent1", "teachers")
                            .contains(teacher1, teacher2)
                            .doesNotContain(teacher3)
            );
            assertAll("Student2 properties",
                    () -> assertThat(actualStudent2.getId())
                            .as("Check %s's %s", "Student2", "ID").isEqualTo(expectedStudent2.getId()),
                    () -> assertThat(actualStudent2.getFirstName())
                            .as("Check %s's %s", "Student2", "First Name").isEqualTo(expectedStudent2.getFirstName()),
                    () -> assertThat(actualStudent2.getLastName())
                            .as("Check %s's %s", "Student2", "Last Name").isEqualTo(expectedStudent2.getLastName()),
                    () -> assertThat(actualStudent2.getEmail())
                            .as("Check %s's %s", "Student2", "Email").isEqualTo(expectedStudent2.getEmail()),
                    () -> assertThat(actualStudent2.getAge())
                            .as("Check %s's %s", "Student2", "Age").isEqualTo(expectedStudent2.getAge()),
                    () -> assertThat(actualStudent2.getFieldOfStudy())
                            .as("Check %s's %s", "Student2", "fieldOfStudy").isEqualTo(fieldOfStudy2),
                    () -> assertThat(actualStudent2.getTeachers())
                            .as("Check if %s contains %s", "actualStudent2", "teachers")
                            .contains(teacher3)
                            .doesNotContain(teacher1, teacher2)
            );
            assertAll("Student3 properties",
                    () -> assertThat(actualStudent3.getId())
                            .as("Check %s's %s", "Student3", "ID").isEqualTo(expectedStudent3.getId()),
                    () -> assertThat(actualStudent3.getFirstName())
                            .as("Check %s's %s", "Student3", "First Name").isEqualTo(expectedStudent3.getFirstName()),
                    () -> assertThat(actualStudent3.getLastName())
                            .as("Check %s's %s", "Student3", "Last Name").isEqualTo(expectedStudent3.getLastName()),
                    () -> assertThat(actualStudent3.getEmail())
                            .as("Check %s's %s", "Student3", "Email").isEqualTo(expectedStudent3.getEmail()),
                    () -> assertThat(actualStudent3.getAge())
                            .as("Check %s's %s", "Student3", "Age").isEqualTo(expectedStudent3.getAge()),
                    () -> assertThat(actualStudent3.getFieldOfStudy())
                            .as("Check %s's %s", "Student3", "fieldOfStudy").isEqualTo(fieldOfStudy3),
                    () -> assertThat(actualStudent3.getTeachers())
                            .as("Check if %s contains %s", "actualStudent3", "teachers")
                            .contains(teacher1, teacher2, teacher3)
            );
        }

        @Test
        void fetchAllPaginated_shouldReturnAllStudentsPaginated_givenPageNo_pageSize_sortDir() {
            //given
            int pageNo = 2;
            int pageSize = 2;
            String sortField = "firstName";
            String sortDirection = Sort.Direction.DESC.name();

            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

            Student expectedStudent1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Student expectedStudent2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher3));
            Student expectedStudent3 = initData.createStudentThree(fieldOfStudy3, List.of(teacher1, teacher2, teacher3));
            Page<Student> students = new PageImpl<>(List.of(expectedStudent3));
            //when
            when(repository.findAll(any(Pageable.class))).thenReturn(students);
            Page<StudentDto> actual = service.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
            //then
            verify(repository).findAll(any(Pageable.class));
            List<StudentDto> actualContent = actual.getContent();
            assertThat(actualContent).as("Check %s's list size", "students").hasSize(1);
            StudentDto actualStudent = actualContent.get(0);
            assertAll("Student3 properties",
                    () -> assertThat(actualStudent.getId())
                            .as("Check %s's %s", "Student3", "ID").isEqualTo(expectedStudent3.getId()),
                    () -> assertThat(actualStudent.getFirstName())
                            .as("Check %s's %s", "Student3", "First Name").isEqualTo(expectedStudent3.getFirstName()),
                    () -> assertThat(actualStudent.getLastName())
                            .as("Check %s's %s", "Student3", "Last Name").isEqualTo(expectedStudent3.getLastName()),
                    () -> assertThat(actualStudent.getEmail())
                            .as("Check %s's %s", "Student3", "Email").isEqualTo(expectedStudent3.getEmail()),
                    () -> assertThat(actualStudent.getAge())
                            .as("Check %s's %s", "Student3", "Age").isEqualTo(expectedStudent3.getAge()),
                    () -> assertThat(actualStudent.getFieldOfStudy())
                            .as("Check %s's %s", "Student3", "fieldOfStudy").isEqualTo(fieldOfStudy3),
                    () -> assertThat(actualStudent.getTeachers())
                            .as("Check if %s contains %s", "actualStudent3", "teachers")
                            .contains(teacher1, teacher2, teacher3)
            );
        }
    }

    @Nested
    class FindStudentTest {
        @Test
        void fetchById_shouldFindStudent_givenId() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            StudentDto actual = service.fetchById(expected.getId());
            //then
            ArgumentCaptor<Long> idArgCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).findById(idArgCaptor.capture());
            Long actualId = idArgCaptor.getValue();

            assertThat(actual).as("Check if %s is not null", "Student").isNotNull();
            assertAll("Student properties",
                    () -> assertThat(actualId)
                            .as("Check %s's %s", "Student", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s's %s", "Student", "First Name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s's %s", "Student", "Last Name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s's %s", "Student", "Email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s's %s", "Student", "Age").isEqualTo(expected.getAge()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Student", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "Student", "teachers")
                            .contains(teacher1, teacher2)
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
                    .hasMessage("Invalid Student ID: " + id);
        }
    }

    @Nested
    class DeleteStudentTest {
        @Test
        void remove_shouldRemoveStudent_givenId() {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());

            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            when(repository.findById(anyLong())).thenReturn(Optional.of(expected));
            service.remove(expected.getId());
            //then
            verify(repository).findById(anyLong());
            verify(repository).delete(any(Student.class));
        }

        @Test
        void remove_throwsIllegalArgumentException_givenWrongId() {
            //given
            Long id = 10L;
            //when
            Throwable thrown = catchThrowable(() -> service.remove(id));
            //then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid Student ID: " + id);
        }

        @Test
        void removeAll_shouldDeleteAllStudents() {
            //given
            Student expectedStudent1 = initData.createStudentOne(null, List.of());
            Student expectedStudent2 = initData.createStudentTwo(null, List.of());
            List<Student> students = List.of(expectedStudent1, expectedStudent2);
            //when
            when(repository.findAll()).thenReturn(students);
            service.removeAll();
            //then
            verify(repository).findAll();
            verify(repository).deleteAll();
        }
    }

    @Nested
    class FindStudentsByFirstOrLastNameTest {
        @Test
        void findByFirstOrLastName_returnsStudentsSearchedByFirstOrLastName_givenName() {
            //given
            String name = "w";
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

            Student expectedStudent1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Student expectedStudent2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher3));
            Student expectedStudent3 = initData.createStudentThree(fieldOfStudy3, List.of(teacher1, teacher2, teacher3));
            List<Student> students = List.of(expectedStudent2, expectedStudent3);
            //when
            when(repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString()))
                    .thenReturn(students);
            List<StudentDto> actual = service.findByFirstOrLastName(name);
            //then
            verify(repository).findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString());
            assertThat(actual).as("Check %s's list size", "students").hasSize(2);
            StudentDto actualStudent1 = actual.get(0);
            StudentDto actualStudent2 = actual.get(1);
            assertAll("Student2 properties",
                    () -> assertThat(actualStudent1.getId())
                            .as("Check %s's %s", "Student2", "ID").isEqualTo(expectedStudent2.getId()),
                    () -> assertThat(actualStudent1.getFirstName())
                            .as("Check %s's %s", "Student2", "First Name").isEqualTo(expectedStudent2.getFirstName()),
                    () -> assertThat(actualStudent1.getLastName())
                            .as("Check %s's %s", "Student2", "Last Name").isEqualTo(expectedStudent2.getLastName()),
                    () -> assertThat(actualStudent1.getEmail())
                            .as("Check %s's %s", "Student2", "Email").isEqualTo(expectedStudent2.getEmail()),
                    () -> assertThat(actualStudent1.getAge())
                            .as("Check %s's %s", "Student2", "Age").isEqualTo(expectedStudent2.getAge()),
                    () -> assertThat(actualStudent1.getFieldOfStudy())
                            .as("Check %s's %s", "Student2", "fieldOfStudy").isEqualTo(fieldOfStudy2),
                    () -> assertThat(actualStudent1.getTeachers())
                            .as("Check if %s contains %s", "actualStudent1", "teachers")
                            .contains(teacher3)
                            .doesNotContain(teacher1, teacher2)
            );
            assertAll("Student3 properties",
                    () -> assertThat(actualStudent2.getId())
                            .as("Check %s's %s", "Student3", "ID").isEqualTo(expectedStudent3.getId()),
                    () -> assertThat(actualStudent2.getFirstName())
                            .as("Check %s's %s", "Student3", "First Name").isEqualTo(expectedStudent3.getFirstName()),
                    () -> assertThat(actualStudent2.getLastName())
                            .as("Check %s's %s", "Student3", "Last Name").isEqualTo(expectedStudent3.getLastName()),
                    () -> assertThat(actualStudent2.getEmail())
                            .as("Check %s's %s", "Student3", "Email").isEqualTo(expectedStudent3.getEmail()),
                    () -> assertThat(actualStudent2.getAge())
                            .as("Check %s's %s", "Student3", "Age").isEqualTo(expectedStudent3.getAge()),
                    () -> assertThat(actualStudent2.getFieldOfStudy())
                            .as("Check %s's %s", "Student3", "fieldOfStudy").isEqualTo(fieldOfStudy3),
                    () -> assertThat(actualStudent2.getTeachers())
                            .as("Check if %s contains %s", "actualStudent2", "teachers")
                            .contains(teacher1, teacher2, teacher3)
            );
        }

        @Test
        void findByFirstOrLastNamePaginated_returnsStudentsSearchedByFirstOrLastNamePaginated_givenName_pageNo_pageSize_sortDir() {
            //given
            int pageNo = 1;
            int pageSize = 1;
            String sortField = "name";
            String sortDirection = Sort.Direction.ASC.name();
            String name = "w";

            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy3 = initData.createFieldOfStudyThree(null, List.of(), List.of());

            Student expectedStudent1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Student expectedStudent2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher3));
            Student expectedStudent3 = initData.createStudentThree(fieldOfStudy3, List.of(teacher1, teacher2, teacher3));
            Page<Student> students = new PageImpl<>(List.of(expectedStudent3));
            //when
            when(repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                    .thenReturn(students);
            Page<StudentDto> actual = service.findByFirstOrLastNamePaginated(pageNo, pageSize, sortField, sortDirection, name);
            //then
            verify(repository).findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), any(Pageable.class));
            List<StudentDto> content = actual.getContent();
            assertThat(content).as("Check %s's list size", "students").hasSize(1);
            StudentDto actualStudent = content.get(0);
            assertAll("Student3 properties",
                    () -> assertThat(actualStudent.getId())
                            .as("Check %s's %s", "Student3", "ID").isEqualTo(expectedStudent3.getId()),
                    () -> assertThat(actualStudent.getFirstName())
                            .as("Check %s's %s", "Student3", "First Name").isEqualTo(expectedStudent3.getFirstName()),
                    () -> assertThat(actualStudent.getLastName())
                            .as("Check %s's %s", "Student3", "Last Name").isEqualTo(expectedStudent3.getLastName()),
                    () -> assertThat(actualStudent.getEmail())
                            .as("Check %s's %s", "Student3", "Email").isEqualTo(expectedStudent3.getEmail()),
                    () -> assertThat(actualStudent.getAge())
                            .as("Check %s's %s", "Student3", "Age").isEqualTo(expectedStudent3.getAge()),
                    () -> assertThat(actualStudent.getFieldOfStudy())
                            .as("Check %s's %s", "Student3", "fieldOfStudy").isEqualTo(fieldOfStudy3),
                    () -> assertThat(actualStudent.getTeachers())
                            .as("Check if %s contains %s", "actualStudent3", "teachers")
                            .contains(teacher1, teacher2, teacher3)
            );
        }
    }
}