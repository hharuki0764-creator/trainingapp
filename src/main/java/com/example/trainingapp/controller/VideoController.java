package com.example.trainingapp.controller;

import com.example.trainingapp.model.Exercise;
import com.example.trainingapp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VideoController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    // 画面表示
    @GetMapping("/video")
    public String videoPage(@RequestParam Long exerciseId, Model model) {
        Exercise ex = exerciseRepository.findById(exerciseId).orElse(null);
        model.addAttribute("exercise", ex);
        return "video";
    }

    // 動画登録
    @PostMapping("/video/save")
    @ResponseBody
    public void saveVideo(Long exerciseId, String url) {
        Exercise ex = exerciseRepository.findById(exerciseId).orElseThrow();
        ex.setVideoUrl(url);
        exerciseRepository.save(ex);
    }

    // 動画削除
    @PostMapping("/video/delete")
    @ResponseBody
    public void deleteVideo(Long exerciseId) {
        Exercise ex = exerciseRepository.findById(exerciseId).orElseThrow();
        ex.setVideoUrl(null);
        exerciseRepository.save(ex);
    }
}
