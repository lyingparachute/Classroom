package com.example.classroom.breadcrumb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ExtendWith(MockitoExtension.class)
class BreadcrumbServiceTest {

    @InjectMocks
    private BreadcrumbService breadcrumbService;

    @Test
    void getBreadcrumbs() {
        // Given
        String endpoint = "dashboard/students/edit/12";

        // When
        List<Breadcrumb> actualBreadcrumbs = breadcrumbService.getBreadcrumbs(endpoint);

        // Then
        assertThat(actualBreadcrumbs).extracting(
                Breadcrumb::getLabel,
                Breadcrumb::getUrl
        ).contains(
                tuple("Classroom", "/"),
                tuple("Dashboard", "dashboard"),
                tuple("Students", "dashboard/students"),
                tuple("Edit  /  12", "dashboard/students/edit/12")
        );
    }
}