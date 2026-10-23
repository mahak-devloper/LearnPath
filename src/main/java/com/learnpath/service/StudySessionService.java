package com.learnpath.service;

import com.learnpath.entity.StudySession;
import com.learnpath.repository.StudySessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;

    public StudySessionService(StudySessionRepository studySessionRepository) {
        this.studySessionRepository = studySessionRepository;
    }

    public List<StudySession> getSessionsByStudent(Long studentId) {
        return studySessionRepository.findByStudentIdOrderByStudyDateAsc(studentId);
    }

    public StudySession saveSession(StudySession session) {
        return studySessionRepository.save(session);
    }

    public long getTotalSessions(Long studentId) {
        return studySessionRepository.countByStudentId(studentId);
    }

    public long getCompletedSessions(Long studentId) {
        return studySessionRepository.countByStudentIdAndStatus(studentId, "COMPLETED");
    }

    public Optional<StudySession> getSessionByIdAndStudent(
            Long sessionId, Long studentId) {

        return studySessionRepository.findByIdAndStudentId(sessionId, studentId);
    }
    public void deleteSession(StudySession studySession) {
        studySessionRepository.delete(studySession);
    }
}

