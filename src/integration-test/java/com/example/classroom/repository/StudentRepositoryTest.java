package com.example.classroom.repository;

import com.example.classroom.model.Student;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    StudentRepository repository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(student -> student.getTeachers()
                .forEach(student::removeTeacher));
        repository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(entityManager).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Nested
    class FindAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase {
        @Test
        void returnsEmptyList_givenNonExistingName() {
            //given
            String name = "ARCH";
            createStudent1();
            createStudent2();
            createStudent3();
            //when
            List<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfStudents_givenName() {
            //given
            String name = "wer";
            Student expected1 = createStudent1();
            Student expected2 = createStudent2();
            Student expected3 = createStudent3();
            //when
            List<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().hasSize(1)
                    .containsExactlyInAnyOrder(expected2)
                    .doesNotContain(expected1, expected3);
        }

        @Test
        void returnsListOfStudents_givenLastName() {
            //given
            String name = "ko";
            Student expected1 = createStudent1();
            Student expected2 = createStudent2();
            Student expected3 = createStudent3();
            //when
            List<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected3)
                    .doesNotContain(expected2);
        }

        @Test
        void returnsListOfStudents_givenNameAndPageable() {
            //given
            String name = "ko";
            Pageable pageable = PageRequest.ofSize(1);
            Student expected1 = createStudent1();
            Student expected2 = createStudent2();
            Student expected3 = createStudent3();
            //when
            Page<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, pageable);
            //then
            assertThat(actual).isNotNull().hasSize(1)
                    .containsExactlyInAnyOrder(expected1)
                    .doesNotContain(expected3, expected2);
        }
    }

    public Student createStudent1() {
        Student student = new Student();
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        entityManager.persist(student);
        return student;
    }

    public Student createStudent2() {
        Student student = new Student();
        student.setFirstName("Weronika");
        student.setLastName("Romanski");
        student.setEmail("w.romanski@gmail.com");
        student.setAge(21);
        entityManager.persist(student);
        return student;
    }

    public Student createStudent3() {
        Student student = new Student();
        student.setFirstName("Kornelia");
        student.setLastName("Sernatowicz");
        student.setEmail("a.sernatowicz@gmail.com");
        student.setAge(18);
        entityManager.persist(student);
        return student;
    }
}