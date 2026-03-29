package com.example.trainingapp.model;

import jakarta.persistence.*;

@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 種目名（例：スクワット）

    private String part; // 部位（FULL, LEG, SHOULDER など）

    // ★ 空コンストラクタ（必須）
    public Exercise() {
    }

    // ★ 初期データ用コンストラクタ
    public Exercise(String name, String part) {
        this.name = name;
        this.part = part;
    }

    @Column(name = "video_url")
    private String videoUrl;

    // --- getter / setter ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPart() {
        return part;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}