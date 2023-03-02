package com.example.classroom.repository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FieldOfStudyRepositoryTest {

    @Nested
    class FindAll {
        @Test
        void returnsSortedList_givenSortOrder() {
        }
    }

    @Nested
    class FindAllByNameContainingIgnoreCase {

        @Test
        void returnsEmptyList_givenNonExistingName() {
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
}