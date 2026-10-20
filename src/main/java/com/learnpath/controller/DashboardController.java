package com.learnpath.controller;

import com.learnpath.entity.Student;
import com.learnpath.repository.StudentRepository;
import com.learnpath.repository.LearningTopicRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final LearningTopicRepository learningTopicRepository;

    public DashboardController(
            StudentRepository studentRepository,
            LearningTopicRepository learningTopicRepository) {

        this.studentRepository = studentRepository;
        this.learningTopicRepository = learningTopicRepository;
    }

    @GetMapping("/")
    public String dashboard(
            Authentication authentication,
            Model model) {

        // Default values when no student is available
        model.addAttribute("totalTopics", 0L);
        model.addAttribute("completedTopics", 0L);
        model.addAttribute("inProgressTopics", 0L);
        model.addAttribute("pendingTopics", 0L);
        model.addAttribute("weakTopicsCount", 0L);
        model.addAttribute("completionPercentage", 0);

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(
                authentication.getPrincipal())) {

            String email = authentication.getName();

            Student student = studentRepository
                    .findByEmail(email)
                    .orElse(null);

            if (student != null) {

                model.addAttribute("student", student);

                Long studentId = student.getId();

                // Total learning topics
                long totalTopics =
                        learningTopicRepository
                                .countByStudentId(studentId);

                // Completed learning topics
                long completedTopics =
                        learningTopicRepository
                                .countByStudentIdAndStatus(
                                        studentId, "COMPLETED");

                // Topics currently in progress
                long inProgressTopics =
                        learningTopicRepository
                                .countByStudentIdAndStatus(
                                        studentId, "IN_PROGRESS");

                // Remaining topics
                long pendingTopics =
                        Math.max(
                                0,
                                totalTopics
                                        - completedTopics
                                        - inProgressTopics
                        );

                // Weak topics
                long weakTopicsCount =
                        learningTopicRepository
                                .countByStudentIdAndWeakTrue(
                                        studentId);

                // Overall topic completion percentage
                int completionPercentage = totalTopics > 0
                        ? (int) Math.round(
                        completedTopics * 100.0 / totalTopics)
                        : 0;

                // Send data to dashboard.html
                model.addAttribute(
                        "totalTopics", totalTopics);

                model.addAttribute(
                        "completedTopics", completedTopics);

                model.addAttribute(
                        "inProgressTopics", inProgressTopics);

                model.addAttribute(
                        "pendingTopics", pendingTopics);

                model.addAttribute(
                        "weakTopicsCount", weakTopicsCount);

                model.addAttribute(
                        "completionPercentage",
                        completionPercentage);
            }
        }

        return "dashboard";
    }
}