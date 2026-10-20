package com.learnpath.controller;

import com.learnpath.entity.Student;
import com.learnpath.entity.LearningTopic;
import com.learnpath.repository.StudentRepository;
import com.learnpath.service.LearningTopicService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/weak-topics")
public class WeakTopicController {

    private final LearningTopicService learningTopicService;
    private final StudentRepository studentRepository;

    public WeakTopicController(
            LearningTopicService learningTopicService,
            StudentRepository studentRepository) {
        this.learningTopicService = learningTopicService;
        this.studentRepository = studentRepository;
    }

    private Student getLoggedInStudent(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("Please log in first");
        }

        return studentRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Student not found"));
    }

    @GetMapping
    public String showWeakTopics(
            Authentication authentication, Model model) {

        Student student = getLoggedInStudent(authentication);

        model.addAttribute(
                "weakTopics",
                learningTopicService.getWeakTopics(student.getId()));

        model.addAttribute(
                "totalWeakTopics",
                learningTopicService.getWeakTopicCount(student.getId()));

        model.addAttribute("student", student);

        return "weak-topics";
    }

    @PostMapping("/update")
    public String updateWeakness(
            Authentication authentication,
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "true") boolean weak,
            @RequestParam int confidenceScore,
            @RequestParam(required = false) String weaknessReason,
            @RequestParam String priority,
            RedirectAttributes redirectAttributes) {

        Student student = getLoggedInStudent(authentication);

        learningTopicService.updateWeakness(
                topicId,
                student.getId(),
                weak,
                confidenceScore,
                weaknessReason,
                priority);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Weak topic details updated!");

        return "redirect:/weak-topics";
    }

    @PostMapping("/revise")
    public String recordRevision(
            Authentication authentication,
            @RequestParam Long topicId,
            RedirectAttributes redirectAttributes) {

        Student student = getLoggedInStudent(authentication);

        learningTopicService.recordRevision(
                topicId, student.getId());

        redirectAttributes.addFlashAttribute(
                "successMessage", "Revision recorded successfully!");

        return "redirect:/weak-topics";
    }

    @PostMapping("/revision-status")
    public String updateRevisionStatus(
            Authentication authentication,
            @RequestParam Long topicId,
            @RequestParam String revisionStatus,
            RedirectAttributes redirectAttributes) {

        Student student = getLoggedInStudent(authentication);

        learningTopicService.updateRevisionStatus(
                topicId,
                student.getId(),
                revisionStatus);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Revision status updated!");

        return "redirect:/weak-topics";
    }
}