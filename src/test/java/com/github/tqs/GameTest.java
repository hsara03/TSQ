package com.github.tqs;

import com.github.tqs.model.HighScoreManager;
import com.github.tqs.model.exceptions.game.NoTargetWordException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.exceptions.word.InvalidNextCharException;
import com.github.tqs.model.Difficulty;
import com.github.tqs.controller.Game;
import com.github.tqs.model.Word;

import com.github.tqs.view.GameWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GameTest {

    /**
     * Test: Tries to initialize a word with an invalid word list file.
     */
    @Test()
    public void testInvalidFile() {
        Exception result = null;
        try {
            new Game("invalid file", 10, Difficulty.NORMAL);
        } catch (Exception exception){
            result=exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof UnableToReadWordsException);
    }

    /**
     * Test: Tries to spell out a word with an invalid character.
     * First, selects a word from the generated word list, then, after selecting a word
     * (trying to spell its first character), inputs an invalid character
     */
    @Test()
    public void testInvalidType() {
        Exception result = null;

        try {
            Game game = new Game("src/main/resources/words.mini.txt", 10, Difficulty.NORMAL);
            game.startGame();
            for (Word word:game.getTargetWords()) {
                game.handleType(word.getContent().charAt(0));
                break;
            }
            game.handleType(' '); // invalid char
        } catch (Exception exception){
            result = exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof InvalidNextCharException);
    }

    /**
     * Test: Tries to spell out starting with an invalid character, when no word has been picked
     * as a target yet, so, no valid targets will resolve for the first character being typed.
     */
    @Test()
    public void testInvalidTarget() {
        Exception result = null;
        try {
            Game game = new Game("src/main/resources/words.mini.txt", 10, Difficulty.NORMAL);
            game.handleType(' '); // no targets will be available
        } catch (Exception exception){
            result = exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof NoTargetWordException);
    }

    /**
     * Test: Tries to spell out starting with an invalid character, when no word has been picked
     * as a target yet, so, no valid targets will resolve for the first character being typed.
     */
    @Test()
    public void testPairwiseMultiplier() {
        final String path = "src/main/resources/words.mini.txt";
        Exception result = null;
        try {
            Game gameEasy = new Game(path, 10, Difficulty.EASY);
            assertEquals(1, gameEasy.getScoreMultiplier());

            Game gameNormal = new Game(path, 10, Difficulty.NORMAL);
            assertEquals(2, gameNormal.getScoreMultiplier());

            Game gameHard = new Game(path, 10, Difficulty.HARD);
            assertEquals(3, gameHard.getScoreMultiplier());
        } catch (Exception exception){
            result = exception;
        }
        Assertions.assertNull(result);
    }

    //
    @Test
    public void testValidEndTask(){
        // Verifies that the end-of-time task is executed correctly when the timer runs out
        Exception result = null;
        try {
            // Setting up a game with a minimal word list and a player name
            HighScoreManager.getInstance().setPlayerName("dummy");
            Game game = new Game("src/main/resources/words.mini.txt", 10, Difficulty.NORMAL);
            game.startGame();

            // Retrieving the end-of-time task and running it manually
            TimerTask task = game.getEndOfTimeTask();
            assertNotNull(task);
            task.run();

            // Verifying that the game is no longer in progress after the task is executed
            assertFalse(game.isPlaying());
        } catch (Exception exception){
            result = exception; // Unexpected behavior
        }
        assertNull(result);
    }

    @Test()
    public void testSpellScore() {
        // Verifies that the game calculates the score correctly when spelling a word
        Exception result = null;
        try {
            // Setting up a game with a minimal word list and easy difficulty
            Game game = new Game("src/main/resources/words.mini.txt", 10, Difficulty.EASY);
            game.startGame();
            assertEquals(0,game.getScore());

            // Spelling out the first word and checking if the score matches the score multiplier
            for (Word word:game.getTargetWords()) {
                for (int i = 0; i < word.getContent().length(); i++) {
                    game.handleType(word.getContent().charAt(i));
                }
                break;
            }
            assertEquals(game.getScoreMultiplier(),game.getScore());
        } catch (Exception exception){
            result = exception;
        }
        assertNull(result);
    }

    @Test()
    public void testWindowListener(){
        GameWindow mockWindow = mock(GameWindow.class);
        Exception result = null;
        try {
            Game game = new Game("src/main/resources/words.mini.txt", 10, Difficulty.EASY);
            game.addListener(mockWindow);
            game.startGame();
            // trigger typed+char
            for (Word word:game.getTargetWords()) {
                for (int i = 0; i < word.getContent().length(); i++) {
                    if(i==1){
                        // trigger invalid next char
                        Exception invalidType = null;
                        try {
                            char correctChar = word.getContent().charAt(i);
                            if(correctChar=='a') {
                                game.handleType('b');
                            } else {
                                game.handleType('a');
                            }
                        } catch (Exception exception) {
                            invalidType=exception;
                        }
                        assertNotNull(invalidType);
                        assertTrue(invalidType instanceof InvalidNextCharException);
                    }
                    game.handleType(word.getContent().charAt(i));
                }
                break;
            }
            game.endGame();
        } catch (Exception exception){
            result = exception;
        }
        assertNull(result);
    }


}
