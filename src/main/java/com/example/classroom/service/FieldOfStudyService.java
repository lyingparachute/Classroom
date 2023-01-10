package com.example.classroom.service;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.SubjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FieldOfStudyService {
    
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper mapper;

    public FieldOfStudyService(FieldOfStudyRepository fieldOfStudyRepository, SubjectRepository subjectRepository, StudentRepository studentRepository, ModelMapper mapper) {
        this.fieldOfStudyRepository = fieldOfStudyRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public FieldOfStudyDto create(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = mapper.map(dto, FieldOfStudy.class);
        addDepartmentSubjectsStudents(fieldOfStudy);
        FieldOfStudy saved = fieldOfStudyRepository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    @Transactional
    public FieldOfStudyDto update(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = fieldOfStudyRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeDepartmentSubjectsStudents(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addDepartmentSubjectsStudents(fieldOfStudy);
        FieldOfStudy saved = fieldOfStudyRepository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);

    }

    public List<FieldOfStudyDto> fetchAll() {
        List<FieldOfStudy> all = fieldOfStudyRepository.findAll();
        return all.stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).collect(Collectors.toList());
    }

    public Page<FieldOfStudyDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = fieldOfStudyRepository.findAll(pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }


    public FieldOfStudyDto fetchById(Long id) {
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findById(id);
        return byId.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
    }

    @Transactional
    public void remove(Long id) {
        FieldOfStudy fieldOfStudy = fieldOfStudyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
//        removeDepartmentSubjectsStudents(fieldOfStudy);
        fieldOfStudyRepository.delete(fieldOfStudy);
    }

    @Transactional
    public void removeAll() {
//        fieldOfStudyRepository.findAll().forEach(this::removeDepartmentSubjectsStudents);
        fieldOfStudyRepository.deleteAll();
    }

    public List<FieldOfStudyDto> findByName(String searched) {
        List<FieldOfStudy> found = fieldOfStudyRepository.findAllByName(searched);
        return found.stream().map(s -> mapper.map(s, FieldOfStudyDto.class)).collect(Collectors.toList());
    }

    public Page<FieldOfStudyDto> findByNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = fieldOfStudyRepository.findAllByName(searched, pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }

    private void addDepartment(FieldOfStudy fieldOfStudy) {
        Department department = fieldOfStudy.getDepartment();
        if (department != null) {
            department.getFieldsOfStudy().add(fieldOfStudy);
        }
    }

    private void removeDepartment(FieldOfStudy fieldOfStudy) {
        Department department = fieldOfStudy.getDepartment();
        if (department != null) {
            department.getFieldsOfStudy().remove(fieldOfStudy);
        }
    }

    private void addSubjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = fieldOfStudy.getSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(subject -> subject.setFieldOfStudy(fieldOfStudy));
        }
    }

    private void removeSubjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = fieldOfStudy.getSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(subject -> subject.setFieldOfStudy(null));
        }
    }

    private void addStudents(FieldOfStudy fieldOfStudy) {
        Set<Student> students = fieldOfStudy.getStudents();
        if (students != null && !students.isEmpty()) {
            students.forEach(student -> student.setFieldOfStudy(fieldOfStudy));
        }
    }

    private void removeStudents(FieldOfStudy fieldOfStudy) {
        Set<Student> students = fieldOfStudy.getStudents();
        if (students != null && !students.isEmpty()) {
            students.forEach(student -> student.setFieldOfStudy(null));
        }
    }

    private void addDepartmentSubjectsStudents(FieldOfStudy fieldOfStudy) {
        addDepartment(fieldOfStudy);
        addSubjects(fieldOfStudy);
        addStudents(fieldOfStudy);
        fieldOfStudyRepository.save(fieldOfStudy);
    }

    private void removeDepartmentSubjectsStudents(FieldOfStudy fieldOfStudy) {
        removeDepartment(fieldOfStudy);
        removeSubjects(fieldOfStudy);
        removeStudents(fieldOfStudy);
    }
}