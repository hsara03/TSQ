package com.github.tqs;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.WindowListener;
import com.github.tqs.view.GameWindow;
import com.github.tqs.view.SetupWindow;

import java.awt.*;
import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main implements WindowListener {

    public static void main(String[] args) throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException {
        Main.showGameWindow();
    }
    private static void showGameWindow() throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException {
        // 1. let user pick the difficulty
        SetupWindow window = new SetupWindow();
        // 2. start the game with the picked difficulty
        GameWindow game = new GameWindow(window.pickDifficulty(), new Runnable() {
            @Override
            public void run() {
                try {
                    showGameWindow();
                } catch (IOException | NotEnoughWordsException | FontFormatException | UnableToReadWordsException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        game.startPlaying();
        // 3. let the users play again after dying
    }

    @Override
    public void windowClosed() throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException {
        showGameWindow();
    }
}