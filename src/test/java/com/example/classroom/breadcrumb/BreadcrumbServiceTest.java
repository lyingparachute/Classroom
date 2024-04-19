package com.example.classroom.breadcrumb;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.groups.Tuple.tuple;

@ExtendWith(MockitoExtension.class)
class BreadcrumbServiceTest {

    @Nested
    class getBreadcrumbs {

        @Test
        void returnsListOfBreadcrumbs_givenEndpointWithoutEdit() {
            // Given
            String endpoint = "dashboard/students/12/subjects";

            // When
            Collection<Breadcrumb> actualBreadcrumbs = BreadcrumbService.getBreadcrumbs(endpoint);

            // Then
            assertThat(actualBreadcrumbs).extracting(
                    Breadcrumb::label,
                    Breadcrumb::url,
                    Breadcrumb::last
            ).contains(
                    tuple("Classroom", "/", false),
                    tuple("Dashboard", "dashboard", false),
                    tuple("Students", "dashboard/students", false),
                    tuple("12", "dashboard/students/12", false),
                    tuple("Subjects", "dashboard/students/12/subjects", true)
            );
        }

        @Test
        void returnsListOfBreadcrumbs_givenEndpointWithEdit() {
            // Given
            String endpoint = "dashboard/students/12/edit";

            // When
            Collection<Breadcrumb> actualBreadcrumbs = BreadcrumbService.getBreadcrumbs(endpoint);

            // Then
            assertThat(actualBreadcrumbs).extracting(
                    Breadcrumb::label,
                    Breadcrumb::url,
                    Breadcrumb::last
            ).contains(
                    tuple("Classroom", "/", false),
                    tuple("Dashboard", "dashboard", false),
                    tuple("Students", "dashboard/students", false),
                    tuple("Edit", "dashboard/students/12/edit", true)
            );
        }

        @Test
        void throwsIllegalArgumentException_givenNull() {
            // Given
            String endpoint = null;

            // When
            Throwable thrown = catchThrowable(() -> BreadcrumbService.getBreadcrumbs(endpoint));

            //Then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid endpoint!");
        }

        @Test
        void throwsIllegalArgumentException_givenEmptyEndpoint() {
            // Given
            String endpoint = "";

            // When
            Throwable thrown = catchThrowable(() -> BreadcrumbService.getBreadcrumbs(endpoint));

            //Then
            assertThat(thrown)
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid endpoint!");
        }
    }
}