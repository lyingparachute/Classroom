package com.example.classroom.service;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.enums.Semester;
import com.example.classroom.model.Subject;
import com.example.classroom.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public SubjectDto create(SubjectDto dto) {
        Subject subject = mapper.map(dto, Subject.class);
        addReferencingObjects(subject);
        Subject saved = repository.save(subject);
        return mapper.map(saved, SubjectDto.class);
    }

    @Transactional
    public SubjectDto update(SubjectDto dto) {
        Subject subject = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject '" + dto + "' with ID: " + dto.getId()));
        removeReferencingObjects(subject);
        mapper.map(dto, subject);
        addReferencingObjects(subject);
        return mapper.map(subject, SubjectDto.class);

    }

    public List<SubjectDto> fetchAll() {
        List<Subject> subjects = repository.findAll();
        return subjects.stream().map(subject -> mapper.map(subject, SubjectDto.class)).toList();
    }

    public Page<SubjectDto> fetchAllPaginated(int pageNo,
                                              int pageSize,
                                              String sortField,
                                              String sortDirection) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Subject> all = repository.findAll(pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }

    public Map<Semester, List<Subject>> fetchAllGroupedBySemesters() {
        return Map.ofEntries(
                entry(Semester.FIRST, repository.findAllBySemester(Semester.FIRST)),
                entry(Semester.SECOND, repository.findAllBySemester(Semester.SECOND)),
                entry(Semester.THIRD, repository.findAllBySemester(Semester.THIRD)),
                entry(Semester.FOURTH, repository.findAllBySemester(Semester.FOURTH)),
                entry(Semester.FIFTH, repository.findAllBySemester(Semester.FIFTH)),
                entry(Semester.SIXTH, repository.findAllBySemester(Semester.SIXTH)),
                entry(Semester.SEVENTH, repository.findAllBySemester(Semester.SEVENTH))
        );
    }

    public SubjectDto fetchById(Long id) {
        Optional<Subject> byId = repository.findById(id);
        return byId.map(subject -> mapper.map(subject, SubjectDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Subject subject = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
        removeReferencingObjects(subject);
        repository.delete(subject);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<SubjectDto> findByName(String searched) {
        List<Subject> found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, SubjectDto.class)).toList();
    }

    public Page<SubjectDto> findByNamePaginated(int pageNo,
                                                int pageSize,
                                                String sortField,
                                                String sortDirection,
                                                String searched) {
        Sort sort = getSortOrder(sortField, sortDirection);

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Subject> all = repository.findAllByNameContainingIgnoreCase(searched, pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }

    private void addReferencingObjects(Subject subject) {
        subject.setFieldOfStudy(subject.getFieldOfStudy());
        subject.getTeachers().forEach(subject::addTeacher);
    }

    private void removeReferencingObjects(Subject subject) {
        subject.setFieldOfStudy(null);
        subject.getTeachers().forEach(subject::removeTeacher);
    }

    private static Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }
}
