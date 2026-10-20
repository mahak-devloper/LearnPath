package com.learnpath.repository;

import com.learnpath.entity.LearningTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningTopicRepository
        extends JpaRepository<LearningTopic, Long> {

    // Get all topics of a student
    List<LearningTopic> findByStudentId(Long studentId);

    // Count all topics
    long countByStudentId(Long studentId);

    // Count completed topics
    long countByStudentIdAndStatus(Long studentId, String status);

    // Find a topic belonging to a specific student
    Optional<LearningTopic> findByIdAndStudentId(
            Long id, Long studentId);

    // Get weak topics of a student
    List<LearningTopic> findByStudentIdAndWeakTrue(Long studentId);

    // Count weak topics
    long countByStudentIdAndWeakTrue(Long studentId);

    // Get weak topics by priority
    List<LearningTopic> findByStudentIdAndWeakTrueAndPriority(
            Long studentId, String priority);
}