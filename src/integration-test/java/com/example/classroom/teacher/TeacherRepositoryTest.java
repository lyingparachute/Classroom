package com.example.classroom.teacher;

import com.example.classroom.security.WithMockCustomUser;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
class TeacherRepositoryTest {

    @Autowired
    TeacherRepository repository;

    @Autowired
    EntityManager entityManager;

    Teacher expected1;
    Teacher expected2;
    Teacher expected3;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(teacher -> teacher.getStudents()
                .forEach(teacher::removeStudent));
        repository.findAll().forEach(teacher -> teacher.getSubjects()
                .forEach(teacher::removeSubject));
        repository.findAll().forEach(teacher -> teacher.setDepartment(null));
        repository.deleteAll();
        expected1 = createTeacher1();
        expected2 = createTeacher2();
        expected3 = createTeacher3();
    }

    @Nested
    class FindAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase {
        @Test
        void returnsEmptyList_givenNonExistingName() {
            // Given
            String name = "ARCH";
            // When
            List<Teacher> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfTeachers_givenName() {
            // Given
            String name = "ja";

            // When
            List<Teacher> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected2)
                    .doesNotContain(expected3);
        }

        @Test
        void returnsListOfTeachers_givenLastName() {
            // Given
            String name = "adam";

            // When
            List<Teacher> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected3)
                    .doesNotContain(expected2);
        }

        @Test
        void returnsListOfTeachers_givenNameAndPageable() {
            // Given
            String name = "adam";
            Pageable pageable = PageRequest.ofSize(1);

            // When
            Page<Teacher> actual = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, pageable);
            // Then
            assertThat(actual).isNotNull().hasSize(1)
                    .containsExactlyInAnyOrder(expected1)
                    .doesNotContain(expected3, expected2);
        }
    }

    @Nested
    class FindByEmail {
        //TODO - create test for custom repository method
        @Test
        void findByEmail() {
        }
    }

    @Transactional
    public Teacher createTeacher1() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        entityManager.persist(teacher);
        return teacher;
    }

    @Transactional
    public Teacher createTeacher2() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        entityManager.persist(teacher);
        return teacher;
    }

    @Transactional
    public Teacher createTeacher3() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Adam");
        teacher.setLastName("Bartosiewicz");
        teacher.setEmail("g.bartosiewicz@gmail.com");
        teacher.setAge(51);
        entityManager.persist(teacher);
        return teacher;
    }
}