package com.github.tqs;

import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.exceptions.word.AlreadySpelledException;
import com.github.tqs.model.exceptions.word.InvalidNextCharException;
import com.github.tqs.model.exceptions.word.RanOutOfTimeException;
import com.github.tqs.model.Word;
import com.github.tqs.model.WordProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.*;


public class WordTest {

    private WordProvider provider;

    @BeforeEach
    public void setUp() throws NotEnoughWordsException, UnableToReadWordsException, java.io.IOException, NoWordsException {
        // Setting up the WordProvider with a minimal word list
        provider = new WordProvider(5, new BufferedReader(new FileReader("src/main/resources/words.mini.txt")));
        provider.readWordFile("src/main/resources/words.mini.txt");
    }

    /**
     * try to spell out a single (valid) letter
     */
    @Test
    public void typeValidChar() throws IOException, AlreadySpelledException, RanOutOfTimeException, InvalidNextCharException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        word.type(content.charAt(0));
        assertNotNull(word.getTyped());
    }

    /**
     * try to spell out a single (invalid) letter
     */
    @Test
    public void typeInvalidChar() {
        Exception result = null;
        try {
            Word word = provider.getNextWord();
            word.type('.');
        } catch(Exception exception){
            result = exception;
            // expected
        }
        assertNotNull(result);
        assertTrue(result instanceof InvalidNextCharException);
    }

    /**
     * try to spell out a word after it the time to type it expired (test valor limit)
     */
    @Test
    public void ranOutOfTime() {
        Exception result = null;
        try {
            Word word = provider.getNextWord(0, new TimerTask() {
                @Override
                public void run() {
                    // dummy task
                }
            });
            Thread.sleep(1);
            word.type(word.getContent().charAt(0));
        } catch(Exception exception){
            // expected
            result=exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof RanOutOfTimeException);
    }

    /**
     * try to spell out a word just barely before it expires (test valor frontera)
     */
    @Test
    public void typeJustBeforeExpire() {
        Exception result = null;
        try {
            int headroom = 100;
            int grace = 30; // not all cpus will take the same amount of time to process the next instruction
            Word word = provider.getNextWord(headroom, new TimerTask() {
                @Override
                public void run() {
                    // dummy task
                }
            });
            long finish = word.getStart()+headroom;
            Thread.sleep(finish-System.currentTimeMillis()-grace);
            word.type(word.getContent().charAt(0));
        } catch(Exception exception){
            // unexpected
            result=exception;
        }
        assertNull(result);
    }

    @Test
    public void spellCompletely() throws IOException, AlreadySpelledException, InvalidNextCharException, RanOutOfTimeException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        for (int i = 0; i < content.length(); i++) {
            word.type(content.charAt(i));
        }
        assertTrue(word.isCompleted());
    }

    @Test
    public void overspell() {
        Exception result = null;
        try {
            Word word = provider.getNextWord();
            String content = word.getContent();
            for (int i = 0; i < content.length(); i++) {
                word.type(content.charAt(i));
            }
            word.type('a');
        } catch(Exception exception){
            // expected
            result=exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof AlreadySpelledException);
    }

    /**
     * Gets the time left to type the word
     */
    @Test
    public void getTimeLeft() {
        // Verifies that the time left to type the word is non-negative
        Exception result = null;
        try {
            Word word = provider.getNextWord();
            assertTrue(word.timePercent()<1);
        } catch (Exception exception){
            result=exception;
        }
        assertNull(result);
    }

    /**
     * Gets the X coordinate of the word
     */
    @Test
    public void getX() {
        // Verifies that the X coordinate of the word is between 0 and 1
        Exception result = null;
        try {
            Word word = provider.getNextWord();
            assertTrue(word.getX()>=0);
            assertTrue(word.getX()<=1);
        } catch (Exception exception){
            result=exception;
        }
        assertNull(result);
    }

}
