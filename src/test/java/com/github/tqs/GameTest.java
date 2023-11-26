package com.github.tqs;

import com.github.tqs.exceptions.game.NoTargetWordException;
import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.model.Difficulty;
import com.github.tqs.model.Game;
import com.github.tqs.model.Word;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;




public class GameTest {

    /**
     * tries to initialize a word with an invalid word list file
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
     * tries to spell out a word with an invalid character; first, selects a word from the generated
     * word list, then, after selecting a word (trying to spell its first character), inputs an invalid
     * character
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
            result=exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof InvalidNextCharException);
    }

    /**
     * tries to spell out starting with an invalid character, when no word has been picked
     * as a target yet, so, no valid targets will resolve for the first character being typed
     */
    @Test()
    public void testInvalidTarget() {
        Exception result = null;
        try {
            Game game = new Game("src/main/resources/words.mini.txt", 10, Difficulty.NORMAL);
            game.handleType(' '); // no targets will be available
        } catch (Exception exception){
            result=exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof NoTargetWordException);
    }


}
