package com.example.classroom.service;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.StudentRepository;
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
public class StudentService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper mapper;

    public StudentService(StudentRepository studentRepository, TeacherRepository teacherRepository, ModelMapper mapper) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.mapper = mapper;
    }

    @Transactional
    public StudentDto create(StudentDto dto) {
        Student student = mapper.map(dto, Student.class);
        assignTeachers(student, student.getTeachers());
        Student saved = studentRepository.save(student);
        return mapper.map(saved, StudentDto.class);
    }

    @Transactional
    public StudentDto update(StudentDto dto) {
        Student student = studentRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid student '" + dto + "' with ID: " + dto.getId()));
        removeTeachers(student, new HashSet<>(student.getTeachers()));
        mapper.map(dto, student);
        assignTeachers(student, student.getTeachers());
        Student saved = studentRepository.save(student);
        return mapper.map(saved, StudentDto.class);

    }

    public List<StudentDto> fetchAll() {
        List<Student> all = studentRepository.findAll();
        return all.stream().map(student -> mapper.map(student, StudentDto.class)).collect(Collectors.toList());
    }

    public Page<StudentDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Student> all = studentRepository.findAll(pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }


    public StudentDto fetchById(Long id) {
        Optional<Student> byId = studentRepository.findById(id);
        return byId.map(student -> mapper.map(student, StudentDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student id: " + id));
        removeTeachers(student, new HashSet<>(student.getTeachers()));
        studentRepository.delete(student);
    }

    public List<StudentDto> findByFirstOrLastName(String searched) {
        List<Student> found = studentRepository.findAllByFirstNameOrLastName(searched);
        return found.stream().map(s -> mapper.map(s, StudentDto.class)).collect(Collectors.toList());
    }

    public Page<StudentDto> findByFirstOrLastNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Student> all = studentRepository.findAllByFirstNameOrLastName(searched, pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }

    @Transactional
    public void assignTeachers(Student student, Set<Teacher> teachers) {
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
    }

    @Transactional
    public void removeTeachers(Student student, Set<Teacher> teachers) {
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::removeTeacher);
            teacherRepository.saveAll(teachers);
            studentRepository.save(student);
        }
    }

    @Transactional
    public void removeAll() {
        for (Student student : studentRepository.findAll()){
            teacherRepository.findAll().forEach(t -> t.removeStudent(student));
        }
        studentRepository.deleteAll();
    }
}
