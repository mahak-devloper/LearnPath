package com.learnpath.controller;

import com.learnpath.entity.Student;
import com.learnpath.repository.StudentRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;

    public DashboardController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/")
    public String dashboard(Authentication authentication, Model model) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String email = authentication.getName();

            Student student = studentRepository.findByEmail(email)
                    .orElse(null);

            if (student != null) {
                model.addAttribute("student", student);
            }
        }

        return "dashboard";
    }
}