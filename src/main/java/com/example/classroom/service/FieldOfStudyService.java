package com.example.classroom.service;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.Semester;
import com.example.classroom.repository.FieldOfStudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class FieldOfStudyService {
    private final FieldOfStudyRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public FieldOfStudyDto create(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = mapper.map(dto, FieldOfStudy.class);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    @Transactional
    public FieldOfStudyDto update(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeReferencingObjects(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);

    }

    public List<FieldOfStudyDto> fetchAll() {
        List<FieldOfStudy> all = repository.findAll();
        return all.stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).toList();
    }

    private static Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }

    public FieldOfStudyDto fetchById(Long id) {
        Optional<FieldOfStudy> byId = repository.findById(id);
        return byId.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
    }

    @Transactional
    public void remove(Long id) {
        FieldOfStudy fieldOfStudy = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
        removeReferencingObjects(fieldOfStudy);
        repository.delete(fieldOfStudy);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<FieldOfStudyDto> findByName(String searched) {
        List<FieldOfStudy> found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, FieldOfStudyDto.class)).toList();
    }

    public Page<FieldOfStudyDto> findByNamePaginated(int pageNo,
                                                     int pageSize,
                                                     String sortField,
                                                     String sortDirection,
                                                     String searched) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = repository.findAllByNameContainingIgnoreCase(searched, pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }

    public Page<FieldOfStudyDto> fetchAllPaginated(int pageNo,
                                                   int pageSize,
                                                   String sortField,
                                                   String sortDirection) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = repository.findAll(pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }

    public Map<Semester, List<Subject>> fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(Long fieldOfStudyId) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Map.ofEntries(
                entry(Semester.FIRST, filterSubjectsBySemester(subjects, Semester.FIRST).toList()),
                entry(Semester.SECOND, filterSubjectsBySemester(subjects, Semester.SECOND).toList()),
                entry(Semester.THIRD, filterSubjectsBySemester(subjects, Semester.THIRD).toList()),
                entry(Semester.FOURTH, filterSubjectsBySemester(subjects, Semester.FOURTH).toList()),
                entry(Semester.FIFTH, filterSubjectsBySemester(subjects, Semester.FIFTH).toList()),
                entry(Semester.SIXTH, filterSubjectsBySemester(subjects, Semester.SIXTH).toList()),
                entry(Semester.SEVENTH, filterSubjectsBySemester(subjects, Semester.SEVENTH).toList())
        );
    }

    private Stream<Subject> filterSubjectsBySemester(List<Subject> subjects, Semester semester) {
        return subjects.stream().filter(s -> s.getSemester().equals(semester));
    }

    public Map<Semester, Integer> calculateHoursInEachSemesterFromFieldOfStudy(Long fieldOfStudyId) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Map.ofEntries(
                entry(Semester.FIRST, filterSubjectsBySemester(subjects, Semester.FIRST)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.SECOND, filterSubjectsBySemester(subjects, Semester.SECOND)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.THIRD, filterSubjectsBySemester(subjects, Semester.THIRD)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.FOURTH, filterSubjectsBySemester(subjects, Semester.FOURTH)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.FIFTH, filterSubjectsBySemester(subjects, Semester.FIFTH)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.SIXTH, filterSubjectsBySemester(subjects, Semester.SIXTH)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.SEVENTH, filterSubjectsBySemester(subjects, Semester.SEVENTH)
                        .mapToInt(Subject::getHoursInSemester).sum())
        );
    }

    public List<String> splitDescription(Long id) {
        FieldOfStudyDto dto = fetchById(id);
        String description = dto.getDescription();
        if (description != null) {
            return Stream.of(description.split(";"))
                    .map(String::strip)
                    .toList();
        }
        return List.of();
    }

    public int getNumberOfSemesters(Long id) {
        LevelOfEducation levelOfEducation = fetchById(id).getLevelOfEducation();
        if (levelOfEducation.equals(LevelOfEducation.FIRST))
            return 7;
        return 3;
    }

    private void addReferencingObjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = new HashSet<>(fieldOfStudy.getSubjects());
        Set<Student> students = new HashSet<>(fieldOfStudy.getStudents());
        fieldOfStudy.setDepartment(fieldOfStudy.getDepartment());
        subjects.forEach(fieldOfStudy::addSubject);
        students.forEach(fieldOfStudy::addStudent);
    }

    private void removeReferencingObjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = new HashSet<>(fieldOfStudy.getSubjects());
        Set<Student> students = new HashSet<>(fieldOfStudy.getStudents());
        fieldOfStudy.setDepartment(null);
        subjects.forEach(fieldOfStudy::removeSubject);
        students.forEach(fieldOfStudy::removeStudent);
    }
}
