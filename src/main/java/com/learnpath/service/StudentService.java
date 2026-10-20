package com.learnpath.service;

import com.learnpath.entity.Student;
import com.learnpath.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public boolean emailExists(String email) {
        return studentRepository.existsByEmail(email);
    }
}
