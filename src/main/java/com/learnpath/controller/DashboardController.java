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

        // Default topic statistics
        model.addAttribute("totalTopics", 0L);
        model.addAttribute("completedTopics", 0L);
        model.addAttribute("inProgressTopics", 0L);
        model.addAttribute("pendingTopics", 0L);
        model.addAttribute("weakTopicsCount", 0L);
        model.addAttribute("completionPercentage", 0);

        // Default XP and level values
        model.addAttribute("totalXp", 0);
        model.addAttribute("currentLevel", 1);
        model.addAttribute("levelProgress", 0);
        model.addAttribute("xpToNextLevel", 100);

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

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
                long pendingTopics = Math.max(
                        0,
                        totalTopics - completedTopics - inProgressTopics
                );

                // Weak topics
                long weakTopicsCount =
                        learningTopicRepository
                                .countByStudentIdAndWeakTrue(studentId);

                // Topic completion percentage
                int completionPercentage = totalTopics > 0
                        ? (int) Math.round(
                        completedTopics * 100.0 / totalTopics)
                        : 0;

                // XP and level
                int totalXp = student.getTotalXp() == null
                        ? 0 : student.getTotalXp();

                int currentLevel = student.getCurrentLevel() == null
                        ? 1 : student.getCurrentLevel();

                // XP progress within the current level
                int levelProgress = totalXp % 100;

                // XP remaining until the next level
                int xpToNextLevel = 100 - levelProgress;

                // Add topic statistics to dashboard
                model.addAttribute("totalTopics", totalTopics);
                model.addAttribute("completedTopics", completedTopics);
                model.addAttribute("inProgressTopics", inProgressTopics);
                model.addAttribute("pendingTopics", pendingTopics);
                model.addAttribute("weakTopicsCount", weakTopicsCount);
                model.addAttribute(
                        "completionPercentage", completionPercentage);

                // Add XP statistics to dashboard
                model.addAttribute("totalXp", totalXp);
                model.addAttribute("currentLevel", currentLevel);
                model.addAttribute("levelProgress", levelProgress);
                model.addAttribute("xpToNextLevel", xpToNextLevel);
            }
        }

        return "dashboard";
    }
}

