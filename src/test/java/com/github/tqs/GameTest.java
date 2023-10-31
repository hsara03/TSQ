package com.github.tqs;

import com.github.tqs.exceptions.game.NoTargetWordException;
import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;
import com.github.tqs.game.Game;
import com.github.tqs.game.Word;
import org.junit.Test;

import java.io.IOException;

public class GameTest {

    private Game game;

    @Test()
    public void testInvalidFile() throws IOException, NotEnoughWordsException {
        try {
            Game game = new Game("invalid file", 10);
        } catch (UnableToReadWordsException exception){
            // expected
        }
    }

    @Test()
    public void testInvalidType() throws IOException, NotEnoughWordsException, NoTargetWordException, AlreadySpelledException, RanOutOfTimeException, UnableToReadWordsException {
        try {
            Game game = new Game("src/main/resources/words.mini.txt", 10);
            game.startGame();
            for (Word word:game.getTargetWords()) {
                game.handleType(word.getContent().charAt(0));
                break;
            }
            game.handleType(' '); // invalid char
        } catch (InvalidNextCharException exception){
            // expected
        }
    }

    @Test()
    public void testInvalidTarget() throws IOException, NotEnoughWordsException, AlreadySpelledException, RanOutOfTimeException, UnableToReadWordsException, InvalidNextCharException {
        try {
            Game game = new Game("src/main/resources/words.mini.txt", 10);
            game.handleType(' '); // no targets will be available
        } catch (NoTargetWordException exception){
            // expected
        }
    }


}
