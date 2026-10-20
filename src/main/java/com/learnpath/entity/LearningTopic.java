package com.learnpath.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "learning_topics")
public class LearningTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Topic title is required")
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @NotNull(message = "Please select a target date")
    private LocalDate targetDate;

    @Column(nullable = false)
    private String status = "NOT_STARTED";

    // Weak Topic Tracker fields

    @Column(nullable = false)
    private boolean weak = false;

    @Column(nullable = false)
    private int confidenceScore = 3;

    private String weaknessReason;

    private String priority = "MEDIUM";

    private String revisionStatus = "NOT_REVISED";

    private int revisionCount = 0;

    private LocalDate lastRevisionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public LearningTopic() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Weak Topic Tracker getters and setters

    public boolean isWeak() {
        return weak;
    }

    public void setWeak(boolean weak) {
        this.weak = weak;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getWeaknessReason() {
        return weaknessReason;
    }

    public void setWeaknessReason(String weaknessReason) {
        this.weaknessReason = weaknessReason;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRevisionStatus() {
        return revisionStatus;
    }

    public void setRevisionStatus(String revisionStatus) {
        this.revisionStatus = revisionStatus;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public LocalDate getLastRevisionDate() {
        return lastRevisionDate;
    }

    public void setLastRevisionDate(LocalDate lastRevisionDate) {
        this.lastRevisionDate = lastRevisionDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}