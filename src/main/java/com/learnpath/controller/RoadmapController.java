package com.learnpath.controller;

import com.learnpath.entity.LearningTopic;
import com.learnpath.entity.Student;
import com.learnpath.repository.StudentRepository;
import com.learnpath.service.LearningTopicService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roadmap")
public class RoadmapController {

    private final LearningTopicService learningTopicService;
    private final StudentRepository studentRepository;

    public RoadmapController(
            LearningTopicService learningTopicService,
            StudentRepository studentRepository) {
        this.learningTopicService = learningTopicService;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public String showRoadmap(
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "topics",
                learningTopicService.getStudentTopics(student.getId()));

        model.addAttribute(
                "totalTopics",
                learningTopicService.getTotalTopics(student.getId()));

        model.addAttribute(
                "completedTopics",
                learningTopicService.getCompletedTopics(student.getId()));

        model.addAttribute("learningTopic", new LearningTopic());

        return "roadmap";
    }

    @PostMapping("/add")
    public String addTopic(
            @Valid @ModelAttribute("learningTopic") LearningTopic topic,
            BindingResult result,
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (student == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute(
                    "topics",
                    learningTopicService.getStudentTopics(student.getId()));

            model.addAttribute(
                    "totalTopics",
                    learningTopicService.getTotalTopics(student.getId()));

            model.addAttribute(
                    "completedTopics",
                    learningTopicService.getCompletedTopics(student.getId()));

            return "roadmap";
        }

        topic.setStudent(student);
        topic.setStatus("NOT_STARTED");

        learningTopicService.saveTopic(topic);

        return "redirect:/roadmap";
    }

    @PostMapping("/update-status")
    public String updateTopicStatus(
            @RequestParam Long topicId,
            @RequestParam String status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (student == null) {
            return "redirect:/login";
        }

        try {
            learningTopicService.updateTopicStatus(
                    topicId,
                    student.getId(),
                    status);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Topic status updated successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unable to update topic status.");
        }

        return "redirect:/roadmap";
    }

    @PostMapping("/delete")
    public String deleteTopic(
            @RequestParam Long topicId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (student == null) {
            return "redirect:/login";
        }

        try {
            learningTopicService.deleteTopic(topicId, student.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Topic deleted successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Unable to delete topic.");
        }

        return "redirect:/roadmap";
    }
    @PostMapping("/edit")
    public String editTopic(
            @RequestParam Long topicId,
            @Valid @ModelAttribute("learningTopic") LearningTopic updatedTopic,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (student == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please enter valid topic details.");
            return "redirect:/roadmap";
        }

        try {
            learningTopicService.updateTopic(
                    topicId, student.getId(), updatedTopic);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Topic updated successfully!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Unable to update topic.");
        }

        return "redirect:/roadmap";
    }

    @PostMapping("/update-weakness")
    public String updateWeakness(
            Authentication authentication,
            @RequestParam Long topicId,
            @RequestParam boolean weak,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        learningTopicService.updateWeakness(
                topicId,
                student.getId(),
                weak,
                3,
                null,
                "MEDIUM"
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                weak ? "Topic marked as weak!" : "Topic marked as strong!"
        );

        return "redirect:/roadmap";
    }
}