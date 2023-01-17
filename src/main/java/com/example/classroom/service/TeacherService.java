package com.example.classroom.service;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.TeacherRepository;
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
public class TeacherService {
    private final TeacherRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public TeacherDto create(final TeacherDto dto) {
        Teacher teacher = mapper.map(dto, Teacher.class);
        addReferencingObjects(teacher);
        Teacher saved = repository.save(teacher);
        return mapper.map(saved, TeacherDto.class);
    }

    @Transactional
    public TeacherDto update(final TeacherDto dto) {
        Teacher teacher = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid teacher '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId()));
        removeReferencingObjects(teacher);
        mapper.map(dto, teacher);
        addReferencingObjects(teacher);
        Teacher saved = repository.save(teacher);
        return mapper.map(saved, TeacherDto.class);

    }

    @Transactional
    public List<TeacherDto> fetchAll() {
        List<Teacher> teachers = repository.findAll();
        return teachers.stream().map(teacher -> mapper.map(teacher, TeacherDto.class)).toList();
    }

    private static Sort getSortOrder(String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        return sort;
    }

    @Transactional
    public TeacherDto fetchById(final Long id) {
        Optional<Teacher> byId = repository.findById(id);
        return byId.map(teacher -> mapper.map(teacher, TeacherDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
    }

    @Transactional
    public void remove(final Long id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
        removeReferencingObjects(teacher);
        repository.delete(teacher);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<TeacherDto> findByFirstOrLastName(final String searched) {
        List<Teacher> found = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, TeacherDto.class)).toList();
    }

    @Transactional
    public Page<TeacherDto> fetchAllPaginated(int pageNo,
                                              int pageSize,
                                              String sortField,
                                              String sortDirection) {
        Sort sort = getSortOrder(sortField, sortDirection);

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Teacher> all = repository.findAll(pageable);
        return all.map(teacher -> mapper.map(teacher, TeacherDto.class));
    }

    public Page<TeacherDto> findByFirstOrLastNamePaginated(int pageNo,
                                                           int pageSize,
                                                           String sortField,
                                                           String sortDirection,
                                                           String searched) {
        Sort sort = getSortOrder(sortField, sortDirection);

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Teacher> all = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched, pageable);
        return all.map(student -> mapper.map(student, TeacherDto.class));
    }

    private void addReferencingObjects(final Teacher teacher) {
        Set<Student> students = new HashSet<>(teacher.getStudents());
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartmentDean(teacher.getDepartmentDean());
        students.forEach(teacher::addStudent);
        subjects.forEach(teacher::addSubject);
    }

    private void removeReferencingObjects(final Teacher teacher) {
        Set<Student> students = new HashSet<>(teacher.getStudents());
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartmentDean(null);
        students.forEach(teacher::removeStudent);
        subjects.forEach(teacher::removeSubject);
    }
}
