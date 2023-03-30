package com.example.classroom.repository;

import com.example.classroom.enums.Semester;
import com.example.classroom.model.Subject;
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
        void returnsEmptyList_givenNonExistingName() {
            //given
            String name = "ARCH";
            createSubject1();
            createSubject2();
            createSubject3();
            //when
            List<Subject> actual = repository.findAllByNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfSubjects_givenName() {
            //given
            String name = "s";
            Subject expected1 = createSubject1();
            Subject expected2 = createSubject2();
            Subject expected3 = createSubject3();
            //when
            List<Subject> actual = repository.findAllByNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected3)
                    .doesNotContain(expected2);
        }

        @Test
        void returnsListOfSubjects_givenNameAndPageable() {
            //given
            String name = "s";
            Pageable pageable = PageRequest.ofSize(1);
            Subject expected1 = createSubject1();
            Subject expected2 = createSubject2();
            Subject expected3 = createSubject3();
            //when
            Page<Subject> actual = repository.findAllByNameContainingIgnoreCase(name, pageable);
            //then
            assertThat(actual).isNotNull().hasSize(1)
                    .contains(expected1)
                    .doesNotContain(expected2, expected3);
        }

    }


    @Nested
    class FindAllBySemester {

        @Test
        void returnsEmptyList_givenNonExistingSemester() {
            //given
            Semester semester = Semester.THIRD;
            Subject expected1 = createSubject1();
            Subject expected2 = createSubject2();
            Subject expected3 = createSubject3();
            //when
            List<Subject> actual = repository.findAllBySemester(semester);
            //then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfSubjects_givenSemester() {
            //given
            Semester semester = Semester.FIRST;
            Subject expected1 = createSubject1();
            expected1.setSemester(semester);
            entityManager.persist(expected1);
            Subject expected2 = createSubject2();
            Subject expected3 = createSubject3();
            //when
            List<Subject> actual = repository.findAllBySemester(semester);
            //then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected3)
                    .doesNotContain(expected2);
        }
    }

    private Subject createSubject1() {
        Subject subject = new Subject();
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setSemester(Semester.FIFTH);
        subject.setHoursInSemester(100);
        subject.setEctsPoints(5);
        entityManager.persist(subject);
        return subject;
    }

    private Subject createSubject2() {
        Subject subject = new Subject();
        subject.setName("Art");
        subject.setDescription("Painting");
        subject.setSemester(Semester.SECOND);
        subject.setHoursInSemester(120);
        subject.setEctsPoints(10);
        entityManager.persist(subject);
        return subject;
    }

    private Subject createSubject3() {
        Subject subject = new Subject();
        subject.setName("Science");
        subject.setDescription("General Science");
        subject.setSemester(Semester.FIRST);
        subject.setHoursInSemester(150);
        subject.setEctsPoints(15);
        entityManager.persist(subject);
        return subject;
    }
}