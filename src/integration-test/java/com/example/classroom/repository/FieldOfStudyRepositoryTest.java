package com.example.classroom.repository;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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
            //given
            Sort sort = Sort.by(Sort.Direction.DESC, "name");
            FieldOfStudy fieldOfStudy1 = createFieldOfStudy1();
            FieldOfStudy fieldOfStudy2 = createFieldOfStudy2();
            FieldOfStudy fieldOfStudy3 = createFieldOfStudy3();
            //when
            List<FieldOfStudy> actual = repository.findAll(sort);
            //then
            assertThat(actual).isNotNull()
                    .containsExactly(fieldOfStudy2, fieldOfStudy1, fieldOfStudy3);
        }
    }

    @Nested
    class FindAllByNameContainingIgnoreCase {

        @Test
        void returnsEmptyList_givenNonExistingName() {
            //given
            String name = "chemia";
            createFieldOfStudy1();
            createFieldOfStudy2();
            //when
            List<FieldOfStudy> actual = repository.findAllByNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfFieldsOfStudies_givenName() {
        }

        @Test
        void returnsListOfFieldsOfStudiesOnGivenPage_givenNameAndPageable() {
        }
    }

    @Nested
    class FindAllSubjectsFromFieldOfStudy {

        @Test
        void returnsEmptyList_givenId_fieldOfStudyWithoutSubjects() {
        }

        @Test
        void returnsListOfSubjects_givenId() {
        }
    }

    @Nested
    class FindAllByLevelOfEducation {

        @Test
        void returnsEmptyList_givenLevelOfEducationAndSortOrder() {
        }

        @Test
        void returnsListOfFieldsOfStudies_givenLevelOfEducationAndSortOrder() {
        }
    }

    public FieldOfStudy createFieldOfStudy1() {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Inżynieria mechaniczno-medyczna");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.MGR);
        entityManager.persist(fieldOfStudy);
        return fieldOfStudy;
    }

    public FieldOfStudy createFieldOfStudy2() {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Mechatronika");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.PT);
        fieldOfStudy.setTitle(AcademicTitle.BACH);
        entityManager.persist(fieldOfStudy);
        return fieldOfStudy;
    }

    public FieldOfStudy createFieldOfStudy3() {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Informatyka");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.DR);
        entityManager.persist(fieldOfStudy);
        return fieldOfStudy;
    }
}