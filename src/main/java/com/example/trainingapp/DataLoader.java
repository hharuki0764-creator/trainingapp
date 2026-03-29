package com.example.trainingapp;

import com.example.trainingapp.model.Exercise;
import com.example.trainingapp.repository.ExerciseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(ExerciseRepository repo) {
        return args -> {

            // 全身
            repo.save(new Exercise("バービー", "FULL"));

            // 全身
            repo.save(new Exercise("ベンチプレス", "CHEST"));
            // 脚
            repo.save(new Exercise("スクワット", "LEG"));
            repo.save(new Exercise("レッグプレス", "LEG"));

            // 肩
            repo.save(new Exercise("ショルダープレス", "SHOULDER"));
            repo.save(new Exercise("サイドレイズ", "SHOULDER"));

            // 背中
            repo.save(new Exercise("デッドリフト", "BACK"));
            repo.save(new Exercise("ラットプルダウン", "BACK"));

            // 腕
            repo.save(new Exercise("アームカール", "ARM"));
            repo.save(new Exercise("トライセプスエクステンション", "ARM"));
        };
    }
}