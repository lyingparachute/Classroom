package com.example.classroom.service;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public StudentDto create(StudentDto dto) {
        Student student = mapper.map(dto, Student.class);
        addReferencingObjects(student);
        Student saved = repository.save(student);
        return mapper.map(saved, StudentDto.class);
    }

    @Transactional
    public StudentDto update(StudentDto dto) {
        Student student = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid student '" + dto + "' with ID: " + dto.getId()));
        removeReferencingObjects(student);
        mapper.map(dto, student);
        addReferencingObjects(student);
        Student saved = repository.save(student);
        return mapper.map(saved, StudentDto.class);

    }

    public List<StudentDto> fetchAll() {
        List<Student> all = repository.findAll();
        return all.stream().map(student -> mapper.map(student, StudentDto.class)).toList();
    }

    public Page<StudentDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Student> all = repository.findAll(pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }


    public StudentDto fetchById(Long id) {
        Optional<Student> byId = repository.findById(id);
        return byId.map(student -> mapper.map(student, StudentDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
        removeReferencingObjects(student);
        repository.delete(student);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<StudentDto> findByFirstOrLastName(String searched) {
        List<Student> found = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, StudentDto.class)).toList();
    }

    public Page<StudentDto> findByFirstOrLastNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Student> all = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched, pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }

    private void addReferencingObjects(Student student) {
        student.setFieldOfStudy(student.getFieldOfStudy());
        Set<Teacher> teachers = new HashSet<>(student.getTeachers());
        teachers.forEach(student::addTeacher);
    }

    private void removeReferencingObjects(Student student) {
        student.setFieldOfStudy(null);
        Set<Teacher> teachers = new HashSet<>(student.getTeachers());
        teachers.forEach(student::removeTeacher);
    }
}
