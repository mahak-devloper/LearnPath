package com.learnpath.controller;

import com.learnpath.dto.RegistrationDto;
import com.learnpath.entity.Student;
import com.learnpath.service.StudentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;

@Controller
public class StudentController {

    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;

    public StudentController(
            StudentService studentService,
            PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute("registrationDto")
            RegistrationDto dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (studentService.emailExists(dto.getEmail())) {
            model.addAttribute("error", "Email is already registered!");
            return "register";
        }

        Student student = new Student();
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        student.setLearningGoal(dto.getLearningGoal());
        student.setCurrentSkillLevel(dto.getCurrentSkillLevel());

        studentService.saveStudent(student);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("email", "");
        return "login";
    }
}