package com.learnpath.repository;

import com.learnpath.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {


    List<StudySession> findByStudentIdOrderByStudyDateAsc(Long studentId);

    Optional<StudySession> findByIdAndStudentId(Long id, Long studentId);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, String status);


}
