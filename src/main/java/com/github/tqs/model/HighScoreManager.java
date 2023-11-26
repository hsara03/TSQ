package com.github.tqs.model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class HighScoreManager {
    private String filePath;

    public HighScoreManager(String filePath) {
        this.filePath = filePath;
    }

    public int readHighScore() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return Integer.parseInt(content.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public void writeHighScore(int score) {
        try {
            Files.write(Paths.get(filePath), String.valueOf(score).getBytes(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
