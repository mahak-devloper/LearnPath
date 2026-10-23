package com.learnpath.controller;

import com.learnpath.entity.Student;
import com.learnpath.entity.StudySession;
import com.learnpath.repository.StudentRepository;
import com.learnpath.service.StudySessionService;
import com.learnpath.service.StudentProgressService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@Controller
public class StudyPlannerController {

    private final StudySessionService studySessionService;
    private final StudentRepository studentRepository;
    private final StudentProgressService studentProgressService;

    public StudyPlannerController(
            StudySessionService studySessionService,
            StudentRepository studentRepository,
            StudentProgressService studentProgressService) {

        this.studySessionService = studySessionService;
        this.studentRepository = studentRepository;
        this.studentProgressService = studentProgressService;
    }

    // Display Study Planner
    @GetMapping("/study-planner")
    public String showStudyPlanner(
            Authentication authentication,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        model.addAttribute(
                "sessions",
                studySessionService.getSessionsByStudent(student.getId()));

        model.addAttribute(
                "totalSessions",
                studySessionService.getTotalSessions(student.getId()));

        model.addAttribute(
                "completedSessions",
                studySessionService.getCompletedSessions(student.getId()));

        model.addAttribute("studySession", new StudySession());
        model.addAttribute("today", LocalDate.now());

        return "study-planner";
    }

    // Add Study Session
    @PostMapping("/study-planner/add")
    public String addStudySession(
            @ModelAttribute("studySession") StudySession studySession,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        studySession.setStudent(student);
        studySession.setStatus("PENDING");

        studySessionService.saveSession(studySession);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Study session added successfully!");

        return "redirect:/study-planner";
    }

    // Complete Study Session and Award XP
    @PostMapping("/study-planner/complete/{id}")
    public String markAsCompleted(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        StudySession studySession = studySessionService
                .getSessionByIdAndStudent(id, student.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Study session not found"));

        // Award XP only on first completion.
        if (!"COMPLETED".equals(studySession.getStatus())) {

            studySession.setStatus("COMPLETED");
            studySessionService.saveSession(studySession);

            int xp = 20;
            studentProgressService.awardXp(student, xp);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Mission completed! You earned " + xp + " XP! 🎉");

        } else {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "This mission is already completed!");
        }

        return "redirect:/study-planner";
    }

    // Delete Study Session
    @PostMapping("/study-planner/delete/{id}")
    public String deleteStudySession(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        StudySession studySession = studySessionService
                .getSessionByIdAndStudent(id, student.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Study session not found"));

        studySessionService.deleteSession(studySession);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Study session deleted successfully!");

        return "redirect:/study-planner";
    }

    // Open Edit Study Session Page
    @GetMapping("/study-planner/edit/{id}")
    public String editStudySession(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        StudySession studySession = studySessionService
                .getSessionByIdAndStudent(id, student.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Study session not found"));

        if ("COMPLETED".equals(studySession.getStatus())) {
            return "redirect:/study-planner";
        }

        model.addAttribute("studySession", studySession);
        model.addAttribute("today", LocalDate.now());

        return "edit-study-session";
    }

    // Update Study Session
    @PostMapping("/study-planner/update/{id}")
    public String updateStudySession(
            @PathVariable Long id,
            @ModelAttribute("studySession") StudySession updatedSession,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Student student = studentRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        StudySession existingSession = studySessionService
                .getSessionByIdAndStudent(id, student.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Study session not found"));

        if ("COMPLETED".equals(existingSession.getStatus())) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Completed sessions cannot be edited!");

            return "redirect:/study-planner";
        }

        existingSession.setTopic(updatedSession.getTopic());
        existingSession.setStudyDate(updatedSession.getStudyDate());
        existingSession.setDurationMinutes(
                updatedSession.getDurationMinutes());
        existingSession.setNotes(updatedSession.getNotes());

        studySessionService.saveSession(existingSession);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Study session updated successfully!");

        return "redirect:/study-planner";
    }
}

