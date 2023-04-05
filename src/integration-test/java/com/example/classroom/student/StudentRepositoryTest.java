package com.example.classroom.student;

import com.example.classroom.security.WithMockCustomUser;
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
@WithMockCustomUser
class StudentRepositoryTest {

    @Autowired
    StudentRepository repository;

    @Autowired
    EntityManager entityManager;

    Student expected1;
    Student expected2;
    Student expected3;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(student -> student.getTeachers()
                .forEach(student::removeTeacher));
        repository.deleteAll();
        expected1 = createStudent1();
        expected2 = createStudent2();
        expected3 = createStudent3();
    }

    @Nested
    class FindAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase {
        @Test
        void returnsEmptyList_givenNonExistingName() {
            // Given
            String name = "ARCH";
            // When
            List<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfStudents_givenName() {
            // Given
            String name = "ang";
            // When
            List<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().hasSize(1)
                    .containsExactlyInAnyOrder(expected2)
                    .doesNotContain(expected1, expected3);
        }

        @Test
        void returnsListOfStudents_givenLastName() {
            // Given
            String name = "ko";
            // When
            List<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected3)
                    .doesNotContain(expected2);
        }

        @Test
        void returnsListOfStudents_givenNameAndPageable() {
            // Given
            String name = "ko";
            Pageable pageable = PageRequest.ofSize(1);
            // When
            Page<Student> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, pageable);
            // Then
            assertThat(actual).isNotNull().hasSize(1)
                    .containsExactlyInAnyOrder(expected1)
                    .doesNotContain(expected3, expected2);
        }
    }

    public Student createStudent1() {
        Student student = new Student();
        student.setFirstName("Kowal");
        student.setLastName("Brodaczowy");
        student.setEmail("b.brodaczowy@gmail.com");
        student.setAge(25);
        entityManager.persist(student);
        return student;
    }

    public Student createStudent2() {
        Student student = new Student();
        student.setFirstName("Angela");
        student.setLastName("Romanski");
        student.setEmail("angela.romanski@gmail.com");
        student.setAge(21);
        entityManager.persist(student);
        return student;
    }

    public Student createStudent3() {
        Student student = new Student();
        student.setFirstName("Kornelia");
        student.setLastName("Sernatowicz");
        student.setEmail("kornelia.sernatowicz@gmail.com");
        student.setAge(18);
        entityManager.persist(student);
        return student;
    }
}