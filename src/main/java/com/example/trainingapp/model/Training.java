package com.example.trainingapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @OneToMany(mappedBy = "training")
    private List<TrainingRecord> records;
}
