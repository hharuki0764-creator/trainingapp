package com.example.trainingapp.repository;

import com.example.trainingapp.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findByDate(LocalDate date);
    Optional<Training> findFirstByDate(LocalDate date);
}