package com.github.tqs.view;

import com.github.tqs.model.Difficulty;
import com.github.tqs.model.HighScoreManager;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupWindow {

    // score will be -1 if no game was played, this is NOT the highscore, this is the SCORE OF THE LAST GAME
    // can be used to retrieve the TRUE HIGHSCORE
    private HighScoreManager highScoreManager;

    public SetupWindow() throws IOException {
        this.highScoreManager=HighScoreManager.getInstance();
    }

    public Difficulty pickDifficulty() throws IOException{
        int highScore= highScoreManager.readHighscore(); // leer puntuacion maxima
        int score = highScoreManager.getLastScore();
        List<String> messages = new ArrayList<>();
        if(highScore>0) messages.add("La puntuación más alta es " + highScore);
        if(score>=0) messages.add("La puntuación de la última partida fue " + score);
        messages.add("Selecciona la dificultad del juego:");
        Map<String, Difficulty> options = new HashMap<>();
        options.put("Fácil", Difficulty.EASY);
        options.put("Mediano", Difficulty.NORMAL);
        options.put("Difícil", Difficulty.HARD);
        String difficulty = (String) JOptionPane.showInputDialog(null,
                String.join("\n", messages),
                "Dificultad",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options.keySet().toArray(),
                "Mediano");
        return options.get(difficulty);
    }

}
