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
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final ModelMapper mapper;

    public TeacherService(TeacherRepository teacherRepository, ModelMapper mapper) {
        this.teacherRepository = teacherRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TeacherDto create(TeacherDto dto) {
        Teacher teacher = mapper.map(dto, Teacher.class);
        assignStudents(teacher, new HashSet<>(teacher.getStudents()));
        addSubjects(teacher, new HashSet<>(teacher.getSubjects()));
        Teacher saved = teacherRepository.save(teacher);
        return mapper.map(saved, TeacherDto.class);
    }

    @Transactional
    public TeacherDto update(TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid teacher '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId()));
        removeStudents(teacher, new HashSet<>(teacher.getStudents()));
        removeSubjects(teacher, new HashSet<>(teacher.getSubjects()));
        mapper.map(dto, teacher);
        assignStudents(teacher, teacher.getStudents());
        addSubjects(teacher, teacher.getSubjects());
        Teacher saved = teacherRepository.save(teacher);
        return mapper.map(saved, TeacherDto.class);

    }

    @Transactional
    public List<TeacherDto> fetchAll() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teachers.stream().map(teacher -> mapper.map(teacher, TeacherDto.class)).collect(Collectors.toList());
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
    public TeacherDto fetchById(Long id) {
        Optional<Teacher> byId = teacherRepository.findById(id);
        return byId.map(teacher -> mapper.map(teacher, TeacherDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
        teacherRepository.delete(teacher);
    }

    public List<TeacherDto> findByFirstOrLastName(String searched) {
        List<Teacher> found = teacherRepository.findAllByFirstNameOrLastName(searched);
        return found.stream().map(s -> mapper.map(s, TeacherDto.class)).collect(Collectors.toList());
    }

    public Page<TeacherDto> findByFirstOrLastNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Teacher> all = teacherRepository.findAllByFirstNameOrLastName(searched, pageable);
        return all.map(student -> mapper.map(student, TeacherDto.class));
    }

    public void assignStudents(Teacher teacher, Set<Student> students) {
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
        }
    }

    private void removeStudents(Teacher teacher, Set<Student> students) {
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::removeStudent);
        }
    }

    public void addSubjects(Teacher teacher, Set<Subject> subjects) {
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(teacher::addSubject);
        }
    }

    private void removeSubjects(Teacher teacher, Set<Subject> subjects) {
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(teacher::removeSubject);
        }
    }

    @Transactional
    public void removeAll() {
        teacherRepository.deleteAll();
    }
}
