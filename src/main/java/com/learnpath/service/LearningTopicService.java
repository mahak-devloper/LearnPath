package com.learnpath.service;

import com.learnpath.entity.LearningTopic;
import com.learnpath.repository.LearningTopicRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LearningTopicService {

    private final LearningTopicRepository learningTopicRepository;

    public LearningTopicService(
            LearningTopicRepository learningTopicRepository) {
        this.learningTopicRepository = learningTopicRepository;
    }

    // Existing roadmap methods

    public LearningTopic saveTopic(LearningTopic topic) {
        return learningTopicRepository.save(topic);
    }

    public List<LearningTopic> getStudentTopics(Long studentId) {
        return learningTopicRepository.findByStudentId(studentId);
    }

    public long getTotalTopics(Long studentId) {
        return learningTopicRepository.countByStudentId(studentId);
    }

    public long getCompletedTopics(Long studentId) {
        return learningTopicRepository
                .countByStudentIdAndStatus(studentId, "COMPLETED");
    }

    public LearningTopic getTopicById(Long topicId) {
        return learningTopicRepository.findById(topicId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));
    }

    public void updateTopicStatus(
            Long topicId, Long studentId, String status) {

        if (!status.equals("NOT_STARTED")
                && !status.equals("IN_PROGRESS")
                && !status.equals("COMPLETED")) {
            throw new IllegalArgumentException("Invalid topic status");
        }

        LearningTopic topic = learningTopicRepository
                .findByIdAndStudentId(topicId, studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));

        topic.setStatus(status);
        learningTopicRepository.save(topic);
    }

    public void deleteTopic(Long topicId, Long studentId) {
        LearningTopic topic = learningTopicRepository
                .findByIdAndStudentId(topicId, studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));

        learningTopicRepository.delete(topic);
    }

    public LearningTopic updateTopic(
            Long topicId, Long studentId, LearningTopic updatedTopic) {

        LearningTopic existingTopic = learningTopicRepository
                .findByIdAndStudentId(topicId, studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));

        existingTopic.setTitle(updatedTopic.getTitle());
        existingTopic.setDescription(updatedTopic.getDescription());
        existingTopic.setDifficulty(updatedTopic.getDifficulty());
        existingTopic.setTargetDate(updatedTopic.getTargetDate());

        return learningTopicRepository.save(existingTopic);
    }

    // Weak Topic Tracker methods

    public List<LearningTopic> getWeakTopics(Long studentId) {
        return learningTopicRepository
                .findByStudentIdAndWeakTrue(studentId);
    }

    public long getWeakTopicCount(Long studentId) {
        return learningTopicRepository
                .countByStudentIdAndWeakTrue(studentId);
    }

    public List<LearningTopic> getWeakTopicsByPriority(
            Long studentId, String priority) {

        return learningTopicRepository
                .findByStudentIdAndWeakTrueAndPriority(
                        studentId, priority);
    }

    public void updateWeakness(
            Long topicId,
            Long studentId,
            boolean weak,
            int confidenceScore,
            String weaknessReason,
            String priority) {

        if (confidenceScore < 1 || confidenceScore > 5) {
            throw new IllegalArgumentException(
                    "Confidence score must be between 1 and 5");
        }

        if (!priority.equals("HIGH")
                && !priority.equals("MEDIUM")
                && !priority.equals("LOW")) {
            throw new IllegalArgumentException("Invalid priority");
        }

        LearningTopic topic = learningTopicRepository
                .findByIdAndStudentId(topicId, studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));

        topic.setWeak(weak);
        topic.setConfidenceScore(confidenceScore);
        topic.setWeaknessReason(weaknessReason);
        topic.setPriority(priority);

        learningTopicRepository.save(topic);
    }

    public void recordRevision(Long topicId, Long studentId) {

        LearningTopic topic = learningTopicRepository
                .findByIdAndStudentId(topicId, studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));

        topic.setRevisionCount(topic.getRevisionCount() + 1);
        topic.setLastRevisionDate(LocalDate.now());
        topic.setRevisionStatus("REVISING");

        learningTopicRepository.save(topic);
    }

    public void updateRevisionStatus(
            Long topicId, Long studentId, String revisionStatus) {

        if (!revisionStatus.equals("NOT_REVISED")
                && !revisionStatus.equals("REVISING")
                && !revisionStatus.equals("IMPROVED")) {
            throw new IllegalArgumentException(
                    "Invalid revision status");
        }

        LearningTopic topic = learningTopicRepository
                .findByIdAndStudentId(topicId, studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Topic not found"));

        topic.setRevisionStatus(revisionStatus);

        if (revisionStatus.equals("IMPROVED")) {
            topic.setWeak(false);
        }

        learningTopicRepository.save(topic);
    }
}