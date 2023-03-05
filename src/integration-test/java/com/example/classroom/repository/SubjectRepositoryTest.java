package com.example.classroom.repository;

import com.example.classroom.entity.Subject;
import com.example.classroom.enums.Semester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SubjectRepositoryTest {

    @Autowired
    SubjectRepository repository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(subject -> subject.getTeachers()
                .forEach(subject::removeTeacher));
        repository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(entityManager).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Nested
    class FindAllByNameContainingIgnoreCase {

        @Test
        void returnsEmptyListOfSubjects_givenNonExistingName() {

        }

        @Test
        void returnsListOfSubjects_givenName() {
        }

        @Test
        void returnsListOfSubjects_givenNameAndPageable() {
        }

    }


    @Nested
    class FindAllBySemester {

        @Test
        void returnsEmptyListOfSubjects_givenNonExistingSemester() {

        }

        @Test
        void returnsListOfSubjects_givenSemester() {

        }
    }

    private Subject createSubject1() {
        Subject subject = new Subject();
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setSemester(Semester.FIFTH);
        subject.setHoursInSemester(100);
        entityManager.persist(subject);
        return subject;
    }

    private Subject createSubject2() {
        Subject subject = new Subject();
        subject.setName("Art");
        subject.setDescription("Painting");
        subject.setSemester(Semester.SECOND);
        subject.setHoursInSemester(120);
        entityManager.persist(subject);
        return subject;
    }

    private Subject createSubject3() {
        Subject subject = new Subject();
        subject.setName("Science");
        subject.setDescription("General Science");
        subject.setSemester(Semester.FIRST);
        subject.setHoursInSemester(150);
        entityManager.persist(subject);
        return subject;
    }
}