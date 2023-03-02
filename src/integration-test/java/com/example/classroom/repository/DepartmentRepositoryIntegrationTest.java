package com.example.classroom.repository;

import com.example.classroom.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DepartmentRepositoryIntegrationTest {

    @Autowired
    DepartmentRepository repository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        repository.findAll().forEach(department -> department.getFieldsOfStudy()
                .forEach(department::removeFieldOfStudy));
        repository.deleteAll();
        initTestData();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(entityManager).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Test
    void shouldFindAllByNameContainingIgnoreCase() {
        //given
        String name = "MECH";
        Department expected = new Department();
        expected.setName("Wydział mechaniczny");
        underTest.save(expected);
        //when
        List<Department> actual = underTest.findAllByNameContainingIgnoreCase(name);
        //then
        assertThat(actual).isNotNull().hasSize(1);
        Department actualDepartment = actual.get(0);
        assertThat(actualDepartment).isInstanceOf(Department.class);
        assertThat(actualDepartment.getName()).isEqualTo(expected.getName());
    }

    private void initTestData() {
        createDepartmentMechaniczny();
        createDepartmentMechatroniczny();
        createDepartmentArchitektury();
        createDepartmentChemiczny();
    }

    private void createDepartmentMechaniczny() {
        Department department = new Department();
        department.setName("Wydział Mechaniczny");
        department.setAddress("ul. Mechaniczna 1, 24-192 Oborniki");
        department.setTelNumber("987739874");
        entityManager.persist(department);
    }

    private void createDepartmentMechatroniczny() {
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Mechatroniczna 12, 99-112 Gdynia");
        department.setTelNumber("333444555");
        entityManager.persist(department);
    }

    private void createDepartmentArchitektury() {
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber("321321321");
        entityManager.persist(department);
    }

    private void createDepartmentChemiczny() {
        Department department = new Department();
        department.setName("Wydział Chemiczny");
        department.setAddress("ul. Broniewicza 115, 00-245 Kęty");
        department.setTelNumber("987654321");
        entityManager.persist(department);
    }
}
