package com.learnpath.service;

import com.learnpath.entity.Student;
import com.learnpath.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProgressService {

    private final StudentRepository studentRepository;

    public StudentProgressService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public void awardXp(Student student, int xp) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException("Valid student is required.");
        }

        if (xp <= 0) {
            throw new IllegalArgumentException("XP must be greater than zero.");
        }

        Student savedStudent = studentRepository.findById(student.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found."));

        int currentXp = savedStudent.getTotalXp() == null
                ? 0 : savedStudent.getTotalXp();

        int totalXp = currentXp + xp;

        savedStudent.setTotalXp(totalXp);
        savedStudent.setCurrentLevel(calculateLevel(totalXp));

        studentRepository.save(savedStudent);
    }

    public int calculateLevel(int totalXp) {
        if (totalXp < 0) {
            throw new IllegalArgumentException("XP cannot be negative.");
        }

        // Har 100 XP par ek level increase hoga.
        return (totalXp / 100) + 1;
    }
}

