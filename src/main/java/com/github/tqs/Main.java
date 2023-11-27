package com.github.tqs;

import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.view.exceptions.InputCancelledException;
import com.github.tqs.model.Difficulty;
import com.github.tqs.view.WindowListener;
import com.github.tqs.view.ErrorWindow;
import com.github.tqs.view.GameWindow;
import com.github.tqs.view.SetupWindow;

import java.awt.*;
import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main implements WindowListener {

    public static void main(String[] args) {
        try {
            // Show the game window, handling various exceptions
            Main.showGameWindow(false);
        } catch (IOException exception) {
            new ErrorWindow().ioException();
        } catch (NotEnoughWordsException | UnableToReadWordsException | NoWordsException exception){
            new ErrorWindow().dictionaryException();
        } catch (FontFormatException exception){
            new ErrorWindow().drawException();
        } catch (InputCancelledException exception){
            // Ignore InputCancelledException (user cancelled the setup), we can close the game
        }
    }

    // Method to show the game window with an option to skip the username input
    private static void showGameWindow(boolean skipUsername) throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException, InputCancelledException, NoWordsException {
        // 1. Let the user pick the difficulty
        SetupWindow window = new SetupWindow();
        Difficulty difficulty;
        if(skipUsername){
            difficulty = window.showSkippingUsername();
        } else {
            difficulty = window.show();
        }
        // 2. Start the game with the picked difficulty
        GameWindow game = new GameWindow(difficulty, () -> {
            try {
                // Show the game window again after it's closed
                showGameWindow(true);
            } catch (IOException | NotEnoughWordsException | FontFormatException | UnableToReadWordsException |
                     InputCancelledException | NoWordsException e) {
                // Throw a runtime exception if an error occurs while reopening the game window
                throw new RuntimeException(e);
            }
        });
        game.startPlaying();
        // 3. Let the users play again after dying
    }

    // Handle the windowClosed event
    @Override
    public void windowClosed() throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException, InputCancelledException, NoWordsException {
        // Show the game window again after it's closed
        showGameWindow(true);
    }
}