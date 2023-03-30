package com.example.classroom.repository;

import com.example.classroom.model.Department;
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
class DepartmentRepositoryTest {

    @Autowired
    DepartmentRepository repository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(department -> department.getFieldsOfStudy()
                .forEach(department::removeFieldOfStudy));
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
            createDepartmentMechatroniczny();
            createDepartmentMechaniczny();
            //when
            List<Department> actual = repository.findAllByNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        void returnsListOfDepartments_givenName() {
            //given
            String name = "MECH";
            Department expected1 = createDepartmentMechatroniczny();
            Department expected2 = createDepartmentMechaniczny();
            //when
            List<Department> actual = repository.findAllByNameContainingIgnoreCase(name);
            //then
            assertThat(actual).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(expected1, expected2);
        }

        @Test
        void returnsListOfDepartmentsOnGivenPage_givenNameAndPageable() {
            //given
            String name = "MECH";
            Pageable pageable = PageRequest.ofSize(1);
            Department expected1 = createDepartmentMechatroniczny();
            Department expected2 = createDepartmentMechaniczny();
            //when
            Page<Department> actual = repository.findAllByNameContainingIgnoreCase(name, pageable);
            //then
            assertThat(actual).isNotNull().hasSize(1)
                    .contains(expected1)
                    .doesNotContain(expected2);
        }
    }

    private Department createDepartmentMechaniczny() {
        Department department = new Department();
        department.setName("Wydział Mechaniczny");
        department.setAddress("ul. Mechaniczna 1, 24-192 Oborniki");
        department.setTelNumber("987739874");
        entityManager.persist(department);
        return department;
    }

    private Department createDepartmentMechatroniczny() {
        Department department = new Department();
        department.setName("Wydział Mechatroniczny");
        department.setAddress("ul. Mechatroniczna 12, 99-112 Gdynia");
        department.setTelNumber("333444555");
        entityManager.persist(department);
        return department;
    }

    private Department createDepartmentArchitektury() {
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber("321321321");
        entityManager.persist(department);
        return department;
    }

    private Department createDepartmentChemiczny() {
        Department department = new Department();
        department.setName("Wydział Chemiczny");
        department.setAddress("ul. Broniewicza 115, 00-245 Kęty");
        department.setTelNumber("987654321");
        entityManager.persist(department);
        return department;
    }
}
