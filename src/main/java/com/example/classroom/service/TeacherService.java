package com.example.classroom.service;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.TeacherRepository;
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
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final ModelMapper mapper;

    public TeacherService(TeacherRepository teacherRepository, ModelMapper mapper) {
        this.teacherRepository = teacherRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TeacherDto create(final TeacherDto dto) {
        Teacher teacher = mapper.map(dto, Teacher.class);
        addReferencingObjects(teacher);
        Teacher saved = teacherRepository.save(teacher);
        return mapper.map(saved, TeacherDto.class);
    }

    @Transactional
    public TeacherDto update(final TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid teacher '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId()));
        removeReferencingObjects(teacher);
        mapper.map(dto, teacher);
        addReferencingObjects(teacher);
        Teacher saved = teacherRepository.save(teacher);
        return mapper.map(saved, TeacherDto.class);

    }

    @Transactional
    public List<TeacherDto> fetchAll() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teachers.stream().map(teacher -> mapper.map(teacher, TeacherDto.class)).toList();
    }

    @Transactional
    public Page<TeacherDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Teacher> all = teacherRepository.findAll(pageable);
        return all.map(teacher -> mapper.map(teacher, TeacherDto.class));
    }

    @Transactional
    public TeacherDto fetchById(final Long id) {
        Optional<Teacher> byId = teacherRepository.findById(id);
        return byId.map(teacher -> mapper.map(teacher, TeacherDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
    }

    @Transactional
    public void remove(final Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
        removeReferencingObjects(teacher);
        teacherRepository.delete(teacher);
    }

    @Transactional
    public void removeAll() {
        teacherRepository.findAll().forEach(this::removeReferencingObjects);
        teacherRepository.deleteAll();
    }

    public List<TeacherDto> findByFirstOrLastName(final String searched) {
        List<Teacher> found = teacherRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, TeacherDto.class)).toList();
    }

    public Page<TeacherDto> findByFirstOrLastNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Teacher> all = teacherRepository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched, pageable);
        return all.map(student -> mapper.map(student, TeacherDto.class));
    }

    private void addReferencingObjects(final Teacher teacher){
        Set<Student> students = new HashSet<>(teacher.getStudents());
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartmentDean(teacher.getDepartmentDean());
        students.forEach(teacher::addStudent);
        subjects.forEach(teacher::addSubject);
    }

    private void removeReferencingObjects(final Teacher teacher){
        Set<Student> students = new HashSet<>(teacher.getStudents());
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartmentDean(null);
        students.forEach(teacher::removeStudent);
        subjects.forEach(teacher::removeSubject);
    }
}
