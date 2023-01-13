package com.example.classroom.service;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.SubjectRepository;
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
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final ModelMapper mapper;

    public SubjectService(SubjectRepository subjectRepository, TeacherRepository teacherRepository, FieldOfStudyRepository fieldOfStudyRepository, ModelMapper mapper) {
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.fieldOfStudyRepository = fieldOfStudyRepository;
        this.mapper = mapper;
    }

    @Transactional
    public SubjectDto create(SubjectDto dto) {
        Subject subject = mapper.map(dto, Subject.class);
        addTeachersAndFieldOfStudy(subject);
        Subject saved = subjectRepository.save(subject);
        return mapper.map(saved, SubjectDto.class);
    }

    @Transactional
    public SubjectDto update(SubjectDto dto) {
        Subject subject = subjectRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject '" + dto + "' with ID: " + dto.getId()));
        removeTeachersAndFieldOfStudy(subject);
        mapper.map(dto, subject);
        addTeachersAndFieldOfStudy(subject);
        Subject saved = subjectRepository.save(subject);
        return mapper.map(saved, SubjectDto.class);

    }

    public List<SubjectDto> fetchAll() {
        List<Subject> subjects = subjectRepository.findAll();
        return subjects.stream().map(subject -> mapper.map(subject, SubjectDto.class)).collect(Collectors.toList());
    }

    public Page<SubjectDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Subject> all = subjectRepository.findAll(pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }


    public SubjectDto fetchById(Long id) {
        Optional<Subject> byId = subjectRepository.findById(id);
        return byId.map(subject -> mapper.map(subject, SubjectDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
        removeTeachersAndFieldOfStudy(subject);
        subjectRepository.delete(subject);
    }

    @Transactional
    public void removeAll() {
        for (Subject subject : subjectRepository.findAll()){
            teacherRepository.findAll().forEach(t -> t.removeSubject(subject));
        }
        subjectRepository.deleteAll();
    }

    public List<SubjectDto> findByName(String searched) {
        List<Subject> found = subjectRepository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, SubjectDto.class)).collect(Collectors.toList());
    }

    public Page<SubjectDto> findByNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Subject> all = subjectRepository.findAllByNameContainingIgnoreCase(searched, pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }


    private void addTeachersAndFieldOfStudy(Subject subject) {
        HashSet<Teacher> teachers = new HashSet<>(subject.getTeachers());
        FieldOfStudy fieldOfStudy = subject.getFieldOfStudy();
        if (!teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
//            teachers.forEach(t -> t.getSubjects().add(subject));
//            teachers.forEach(subject::addTeacher);
        }
        if (fieldOfStudy != null) {
            fieldOfStudy.addSubject(subject);
//            subject.setFieldOfStudy(fieldOfStudy);
//            fieldOfStudy.getSubjects().add(subject);
        }
    }

    private void removeTeachersAndFieldOfStudy(Subject subject) {
        HashSet<Teacher> teachers = new HashSet<>(subject.getTeachers());
        FieldOfStudy fieldOfStudy = subject.getFieldOfStudy();
        if (!teachers.isEmpty()) {
            teachers.forEach(subject::removeTeacher);
//            teachers.forEach(t -> t.getSubjects().remove(subject));
//            teachers.forEach(subject::removeTeacher);
        }
        if (fieldOfStudy != null) {
            fieldOfStudy.removeSubject(subject);
        }
    }
}
