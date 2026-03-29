package com.example.trainingapp.controller;

import com.example.trainingapp.model.Exercise;
import com.example.trainingapp.model.Training;
import com.example.trainingapp.model.TrainingRecord;
import com.example.trainingapp.repository.ExerciseRepository;
import com.example.trainingapp.repository.TrainingRecordRepository;
import com.example.trainingapp.repository.TrainingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TrainingController {

    private final TrainingRepository repo;
    private final ExerciseRepository exerciseRepository;
    private final TrainingRecordRepository trainingRecordRepository;

    public TrainingController(TrainingRepository repo,
                              ExerciseRepository exerciseRepository,
                              TrainingRecordRepository trainingRecordRepository) {
        this.repo = repo;
        this.exerciseRepository = exerciseRepository;
        this.trainingRecordRepository = trainingRecordRepository;
    }

    // 日付ごとの画面
    @GetMapping("/trainings")
    public String training(@RequestParam String date, Model model) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate parsedDate = LocalDate.parse(date, formatter);

        Training training = repo.findFirstByDate(parsedDate).orElse(null);

        Map<String, List<TrainingRecord>> groupedRecords = new HashMap<>();

        if (training != null && training.getRecords() != null) {
            for (TrainingRecord r : training.getRecords()) {

                if (r.getExercise() == null) continue;

                String part = r.getExercise().getPart();
                String name = r.getExercise().getName();

                groupedRecords
                        .computeIfAbsent(part, k -> new ArrayList<>())
                        .add(r);
            }
        }

        model.addAttribute("groupedRecords", groupedRecords);
        model.addAttribute("date", date);

        return "training-list";
    }

    // 追加画面
    @GetMapping("/trainings/add")
    public String addForm(@RequestParam String date, Model model) {
        model.addAttribute("date", date);
        return "training-add";
    }

    // 追加処理
    @PostMapping("/trainings/add")
    public String addExercise(@RequestParam String date,
                              @RequestParam Long exerciseId) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate parsedDate = LocalDate.parse(date, formatter);

        Training training = repo.findFirstByDate(parsedDate)
                .orElseGet(() -> {
                    Training t = new Training();
                    t.setDate(parsedDate);
                    return repo.save(t);
                });

        System.out.println("Training ID = " + training.getId());
        System.out.println("date = " + parsedDate + " / Training ID = " + training.getId());

        // ⭐ 最大5件制限（任意）
        if (training.getRecords() != null && training.getRecords().size() >= 5) {
            throw new RuntimeException("5種目までやで");
        }

        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();

        TrainingRecord record = new TrainingRecord();
        record.setTraining(training);
        record.setExercise(exercise);

        trainingRecordRepository.save(record);

        return "redirect:/trainings?date=" + date;
    }

    // 部位→種目取得API
    @GetMapping("/api/exercises")
    @ResponseBody
    public List<Exercise> getExercises(@RequestParam String part) {
        if (part.equals("FULL")) {
            return exerciseRepository.findAll();
        }
        return exerciseRepository.findByPart(part);
    }

    // 日付ごとの種目取得API
    @GetMapping("/api/exercises-by-date")
    @ResponseBody
    public List<Map<String, String>> getExercisesByDate(@RequestParam String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate parsedDate = LocalDate.parse(date, formatter);

        Training training = repo.findFirstByDate(parsedDate).orElse(null);

        List<Map<String, String>> result = new ArrayList<>();

        if (training != null && training.getRecords() != null) {
            for (TrainingRecord r : training.getRecords()) {

                if (r.getExercise() == null) continue;

                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(r.getId()));
                map.put("part", r.getExercise().getPart());
                map.put("name", r.getExercise().getName());

                result.add(map);
            }
        }

        return result;
    }

    // カレンダー用API
    @GetMapping("/api/trainings")
    @ResponseBody
    public List<Map<String, String>> getTrainings(@RequestParam String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate parsedDate = LocalDate.parse(date, formatter);

        Training training = repo.findFirstByDate(parsedDate).orElse(null);

        List<Map<String, String>> result = new ArrayList<>();

        if (training != null && training.getRecords() != null) {
            for (TrainingRecord r : training.getRecords()) {

                if (r.getExercise() == null) continue;

                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(r.getId()));
                map.put("part", r.getExercise().getPart());
                map.put("name", r.getExercise().getName());
                map.put("sets", r.getSets() != null ? r.getSets().toString() : "");
                map.put("reps", r.getReps() != null ? r.getReps().toString() : "");
                map.put("weight", r.getWeight() != null ? r.getWeight().toString() : "");
                map.put("exerciseId", r.getExercise().getId().toString());

                TrainingRecord prev = trainingRecordRepository
                        .findTopByExerciseIdAndTrainingDateLessThanOrderByTrainingDateDesc(
                                r.getExercise().getId(),
                                parsedDate
                        );
                if (prev != null) {
                    map.put("prevWeight", prev.getWeight() != null ? prev.getWeight().toString() : "");
                    map.put("prevReps", prev.getReps() != null ? prev.getReps().toString() : "");
                    map.put("prevSets", prev.getSets() != null ? prev.getSets().toString() : "");
                }

                result.add(map);
            }

        }

        return result;
    }
    @PostMapping("/trainings/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable Long id) {
        trainingRecordRepository.deleteById(id);
    }

    @PostMapping("/trainings/record")
    @ResponseBody
    public void saveRecord(@RequestParam Long id,
                           @RequestParam Integer sets,
                           @RequestParam Integer reps,
                           @RequestParam Double weight) {

        TrainingRecord record = trainingRecordRepository.findById(id).orElseThrow();

        record.setSets(sets);
        record.setReps(reps);
        record.setWeight(weight);

        trainingRecordRepository.save(record);
    }

    /*グラフ用API*/
    @GetMapping("/api/records/history")
    @ResponseBody
    public List<Map<String, Object>> getHistory(
            @RequestParam Long exerciseId,
            @RequestParam String period) {

        LocalDate now = LocalDate.now();
        LocalDate fromDate;

        switch (period) {
            case "3m":
                fromDate = now.minusMonths(3);
                break;
            case "1y":
                fromDate = now.minusYears(1);
                break;
            default:
                fromDate = now.minusMonths(1);
        }

        List<TrainingRecord> records =
                trainingRecordRepository.findByExerciseIdAndTrainingDateAfterOrderByTrainingDateAsc(
                        exerciseId, fromDate);

        List<Map<String, Object>> result = new ArrayList<>();

        for (TrainingRecord r : records) {
            if (r.getWeight() == null) continue;

            Map<String, Object> map = new HashMap<>();
            map.put("date", r.getTraining().getDate().toString());
            map.put("weight", r.getWeight());

            result.add(map);
        }

        return result;
    }
    /**種目一覧*/
    @GetMapping("/api/exercises/all")
    @ResponseBody
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }
    @PostMapping("/exercises/add")
    @ResponseBody
    public void addExercise(@RequestParam String name,
                            @RequestParam String part) {

        Exercise ex = new Exercise();
        ex.setName(name);
        ex.setPart(part);

        exerciseRepository.save(ex);
    }

}