package com.github.tqs.view;

import com.github.tqs.model.exceptions.score.InvalidNameException;
import com.github.tqs.view.exceptions.InputCancelledException;
import com.github.tqs.model.Difficulty;
import com.github.tqs.model.HighScoreManager;
import com.github.tqs.model.Score;

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

    // Shows the setup window, skipping the username input is possible
    public Difficulty showSkippingUsername() throws IOException, InputCancelledException {
        try {
            return this.pickDifficulty();
        } catch (InputCancelledException exception){
            return this.show();
        }
    }

    // Shows the full setup window, including username input
    public Difficulty show() throws IOException, InputCancelledException {
        this.pickUsername();
        try {
            return this.pickDifficulty();
        } catch (InputCancelledException exception){
            return this.show();
        }
    }

    // Prompts the user to pick a difficulty level
    private Difficulty pickDifficulty() throws IOException, InputCancelledException {
        Score highScore= highScoreManager.readHighscore();
        Score score = highScoreManager.getLastScore();
        List<String> messages = new ArrayList<>();
        if(highScore.hasData()) messages.add("La puntuación más alta es de " + highScore.name + ": " + highScore.score);
        if(score.hasData()) messages.add("La puntuación de la última partida de " + score.name + " fue " + score.score);
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
        if (difficulty==null) {
            throw new InputCancelledException();
        }
        return options.get(difficulty);
    }

    // Prompts the user to input username
    private void pickUsername() throws InputCancelledException, IOException {
        highScoreManager.readHighscore();
        try {
            String name = null;
            if(highScoreManager.getLastScore().hasData()){
                name=highScoreManager.getLastScore().name;
            }
            System.out.println("last name " + name);
            String playerName = JOptionPane.showInputDialog(null, "Ingrese su nombre de jugador:", name);
            if(playerName==null) throw new InputCancelledException();
            highScoreManager.setPlayerName(playerName);
        } catch (InvalidNameException exception){
            JOptionPane.showMessageDialog(null,"El nombre del jugador no es válido, debe tener de 1-16 carácteres alfanumericos, sin carácteres especiales");
            this.pickUsername();
        }
    }

}
