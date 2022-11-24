package systems.ultimate.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import systems.ultimate.classroom.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s from Student s where s.firstName like %?1% or s.lastName like %?1%")
    List<Student> findAllByFirstNameOrLastName(String firstName);
}