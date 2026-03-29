package com.example.trainingapp.controller;

import com.example.trainingapp.model.Exercise;
import com.example.trainingapp.repository.ExerciseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExerciseController {

    private final ExerciseRepository repo;

    public ExerciseController(ExerciseRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/exercises")
    public String list(Model model) {
        model.addAttribute("exercises", repo.findAll());
        return "exercise-list";
    }

    @GetMapping("/exercises/new")
    public String form(Model model) {
        model.addAttribute("exercise", new Exercise());
        return "exercise-form";
    }

    @PostMapping("/exercises")
    public String save(@ModelAttribute Exercise exercise) {
        repo.save(exercise);
        return "redirect:/exercises";
    }


}