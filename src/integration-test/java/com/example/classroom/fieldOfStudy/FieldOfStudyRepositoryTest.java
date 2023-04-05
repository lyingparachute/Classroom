package com.example.classroom.fieldOfStudy;

import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.enums.Semester;
import com.example.classroom.security.WithMockCustomUser;
import com.example.classroom.subject.Subject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@WithMockCustomUser
class FieldOfStudyRepositoryTest {

    @Autowired
    FieldOfStudyRepository repository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(fieldOfStudy -> fieldOfStudy.getStudents()
                .forEach(fieldOfStudy::removeStudent));
        repository.findAll().forEach(fieldOfStudy -> fieldOfStudy.getSubjects()
                .forEach(fieldOfStudy::removeSubject));
        repository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(entityManager).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Nested
    class FindAll {
        @Test
        void returnsSortedList_givenSortOrder() {
            // Given
            Sort sort = Sort.by(Sort.Direction.DESC, "name");
            FieldOfStudy fieldOfStudy1 = createFieldOfStudy1();
            FieldOfStudy fieldOfStudy2 = createFieldOfStudy2();
            FieldOfStudy fieldOfStudy3 = createFieldOfStudy3();
            // When
            List<FieldOfStudy> actual = repository.findAll(sort);
            // Then
            assertThat(actual).isNotNull()
                    .containsExactly(fieldOfStudy2, fieldOfStudy1, fieldOfStudy3);
        }
    }

    @Nested
    class FindAllByNameContainingIgnoreCase {

        @Test
        void returnsEmptyList_givenNonExistingName() {
            // Given
            String name = "chemia";
            createFieldOfStudy1();
            createFieldOfStudy2();
            // When
            List<FieldOfStudy> actual = repository.findAllByNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfFieldsOfStudies_givenName() {
            // Given
            String name = "mech";
            FieldOfStudy expected1 = createFieldOfStudy1();
            FieldOfStudy expected2 = createFieldOfStudy2();
            FieldOfStudy expected3 = createFieldOfStudy3();
            // When
            List<FieldOfStudy> actual = repository.findAllByNameContainingIgnoreCase(name);
            // Then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected2)
                    .doesNotContain(expected3);
        }

        @Test
        void returnsListOfFieldsOfStudiesOnGivenPage_givenNameAndPageable() {
            // Given
            String name = "mech";
            Pageable pageable = PageRequest.ofSize(1);
            FieldOfStudy expected1 = createFieldOfStudy1();
            FieldOfStudy expected2 = createFieldOfStudy2();
            FieldOfStudy expected3 = createFieldOfStudy3();
            // When
            Page<FieldOfStudy> actual = repository.findAllByNameContainingIgnoreCase(name, pageable);
            // Then
            assertThat(actual).isNotNull().hasSize(1)
                    .contains(expected1)
                    .doesNotContain(expected2, expected3);
        }
    }

    @Nested
    class FindAllSubjectsFromFieldOfStudy {

        @Test
        void returnsEmptyList_givenId_fieldOfStudyWithoutSubjects() {
            // Given
            FieldOfStudy fieldOfStudy = createFieldOfStudy1();
            // When
            List<Subject> actual = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudy.getId());
            // Then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfSubjects_givenId() {
            // Given
            Subject subject1 = createSubject1();
            Subject subject2 = createSubject2();
            Subject subject3 = createSubject3();
            FieldOfStudy fieldOfStudy = createFieldOfStudy1();
            fieldOfStudy.addSubject(subject1);
            fieldOfStudy.addSubject(subject2);
            // When
            List<Subject> actual = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudy.getId());
            // Then
            assertThat(actual).isNotNull().hasSize(2)
                    .contains(subject1, subject2)
                    .doesNotContain(subject3);
        }
    }

    @Nested
    class FindAllByLevelOfEducation {

        @Test
        void returnsEmptyList_givenLevelOfEducationAndSortOrder() {
            // Given
            LevelOfEducation levelOfEducation = LevelOfEducation.SECOND;
            Sort sort = Sort.by("name");
            createFieldOfStudy2();
            createFieldOfStudy3();
            // When
            List<FieldOfStudy> actual = repository.findAllByLevelOfEducation(levelOfEducation, sort);
            // Then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfFieldsOfStudies_givenFirstLevelOfEducationAndSortOrder() {
            // Given
            LevelOfEducation levelOfEducation = LevelOfEducation.FIRST;
            Sort sort = Sort.by("name");
            FieldOfStudy expected1 = createFieldOfStudy1();
            FieldOfStudy expected2 = createFieldOfStudy2();
            FieldOfStudy expected3 = createFieldOfStudy3();
            // When
            List<FieldOfStudy> actual = repository.findAllByLevelOfEducation(levelOfEducation, sort);
            // Then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactly(expected3, expected2)
                    .doesNotContain(expected1);
        }

        @Test
        void returnsListOfFieldsOfStudies_givenSecondLevelOfEducationAndSortOrder() {
            // Given
            LevelOfEducation levelOfEducation = LevelOfEducation.SECOND;
            Sort sort = Sort.by("name");
            FieldOfStudy expected1 = createFieldOfStudy1();
            FieldOfStudy expected2 = createFieldOfStudy2();
            FieldOfStudy expected3 = createFieldOfStudy3();
            // When
            List<FieldOfStudy> actual = repository.findAllByLevelOfEducation(levelOfEducation, sort);
            // Then
            assertThat(actual).isNotNull().hasSize(1)
                    .contains(expected1)
                    .doesNotContain(expected2, expected3);
        }
    }

    private FieldOfStudy createFieldOfStudy1() {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Inżynieria mechaniczno-medyczna");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.MGR);
        entityManager.persist(fieldOfStudy);
        return fieldOfStudy;
    }

    private FieldOfStudy createFieldOfStudy2() {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Mechatronika");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.PT);
        fieldOfStudy.setTitle(AcademicTitle.BACH);
        entityManager.persist(fieldOfStudy);
        return fieldOfStudy;
    }

    private FieldOfStudy createFieldOfStudy3() {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Informatyka");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.DR);
        entityManager.persist(fieldOfStudy);
        return fieldOfStudy;
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