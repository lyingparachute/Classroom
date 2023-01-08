package com.example.classroom.repository.util;

import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.SubjectRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class InitSubject {

    private final SubjectRepository repository;

    public InitSubject(SubjectRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Subject createSubjectMaths(List<Teacher> teachers){
        Subject subject = new Subject();
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setHoursInSemester(100);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(subject::addTeacher);
            repository.save(subject);
        }
        repository.save(subject);
        return subject;
    }
}
