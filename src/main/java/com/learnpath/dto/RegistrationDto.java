package com.learnpath.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationDto {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,
            message = "Password must be between 8 and 72 characters")
    private String password;

    @NotBlank(message = "Learning goal is required")
    private String learningGoal;

    @NotBlank(message = "Please select your skill level")
    private String currentSkillLevel;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(String learningGoal) {
        this.learningGoal = learningGoal;
    }

    public String getCurrentSkillLevel() {
        return currentSkillLevel;
    }

    public void setCurrentSkillLevel(String currentSkillLevel) {
        this.currentSkillLevel = currentSkillLevel;
    }
}