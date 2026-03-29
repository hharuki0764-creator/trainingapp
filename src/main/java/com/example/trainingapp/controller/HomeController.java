package com.example.trainingapp.controller;

import com.example.trainingapp.model.Training;
import com.example.trainingapp.model.TrainingRecord;
import com.example.trainingapp.repository.TrainingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.*;
import java.util.*;

@Controller
public class HomeController {

    private final TrainingRepository trainingRepository; // ⭐ これ追加

    public HomeController(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        LocalDate now = LocalDate.now();

        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        if (month < 1) {
            month = 12;
            year--;
        } else if (month > 12) {
            month = 1;
            year++;
        }

        YearMonth yearMonth = YearMonth.of(year, month);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstDay = yearMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();

        List<List<Integer>> calendar = new ArrayList<>();
        List<Integer> week = new ArrayList<>();

        for (int i = 0; i < startDayOfWeek % 7; i++) {
            week.add(0);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            week.add(day);

            if (week.size() == 7) {
                calendar.add(week);
                week = new ArrayList<>();
            }
        }

        if (!week.isEmpty()) {
            while (week.size() < 7) {
                week.add(0);
            }
            calendar.add(week);
        }

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("calendar", calendar);

        // ⭐ ドット用データ
        List<Training> trainings = trainingRepository.findAll();
        Map<LocalDate, List<String>> trainingMap = new HashMap<>();

        for (Training t : trainings) {
            if (t.getRecords() == null) continue;

            for (TrainingRecord r : t.getRecords()) {
                LocalDate date = t.getDate();
                String part = r.getExercise().getPart();

                trainingMap
                        .computeIfAbsent(date, k -> new ArrayList<>())
                        .add(part);
            }
        }

        model.addAttribute("trainingMap", trainingMap);

        Map<String, List<String>> trainingMapStr = new HashMap<>();

        for (Training t : trainings) {
            String dateStr = t.getDate().toString(); // ←これが超重要

            for (TrainingRecord r : t.getRecords()) {
                if (r.getExercise() == null) continue;

                String part = r.getExercise().getPart();

                trainingMapStr
                        .computeIfAbsent(dateStr, k -> new ArrayList<>())
                        .add(part);
            }
        }
        model.addAttribute("trainingMapStr", trainingMapStr);

        return "calendar";
    }
}