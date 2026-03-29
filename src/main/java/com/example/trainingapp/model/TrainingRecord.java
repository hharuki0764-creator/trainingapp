package com.example.trainingapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TrainingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Training training;

    @ManyToOne
    private Exercise exercise;

    Integer sets;   // セット数
    Integer reps;   // 回数
    Double weight;  // 重量

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    LocalDateTime createdAt; // 任意（履歴用）
}