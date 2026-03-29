package com.example.trainingapp.repository;

import com.example.trainingapp.model.Training;
import com.example.trainingapp.model.TrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {
    TrainingRecord findTopByExerciseIdAndTrainingDateLessThanOrderByTrainingDateDesc(
            Long exerciseId, LocalDate date);
    List<TrainingRecord> findByExerciseIdAndTrainingDateAfterOrderByTrainingDateAsc(
            Long exerciseId, LocalDate date);
}