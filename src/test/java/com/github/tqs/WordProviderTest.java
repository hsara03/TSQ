package com.github.tqs;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.Word;
import com.github.tqs.model.WordProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.TimerTask;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class WordProviderTest {

    private WordProvider invalidProvider;
    private WordProvider provider;
    private WordProvider mockProvider;

    @Before
    public void setUp() {
        invalidProvider = new WordProvider(5);
        provider = new WordProvider(5);
        mockProvider = Mockito.mock(WordProvider.class);
    }

    @Test
    public void testMockNextWord() throws IOException {
        TimerTask mockTimerTask = Mockito.mock(TimerTask.class);

        when(mockProvider.getNextWord(anyInt(), any(TimerTask.class)))
                .thenReturn(new Word("test", 0.5f, 1000, mockTimerTask));

        Word result = mockProvider.getNextWord(10, mockTimerTask);

        verify(mockProvider).getNextWord(anyInt(), any(TimerTask.class));
        assertNotNull(result);
        assertEquals("test", result.getContent());
    }





    /**
     * tries to initialize with a valid word list file
     */
    @Test
    public void testValidFile() {
        Exception result = null;
        try {
            this.provider.readWordFile("src/main/resources/words.mini.txt");
        } catch (Exception exception){
            // unexpected
            result=exception;
        }
        assertNull(result);
    }

    @Test
    public void testNextWords() {
        Exception result = null;
        try {
            this.provider.readWordFile("src/main/resources/words.mini.txt");
            for (int i = 0; i < 10; i++) {
                assert this.provider.getNextWord() != null;
            }
        } catch (Exception exception){
            // unexpected
            result=exception;
        }
        assertNull(result);
    }

    /**
     * test invalid word list file
     */
    @Test
    public void testInvalidFile() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("this file doesnt exist");
        } catch(Exception exception) {
            // thrown, test passed
            result = exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof UnableToReadWordsException);
    }

    /**
     * tries to initialize with an empty word list
     */
    @Test
    public void testEmptyWords() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/empty_word_list.txt");
        } catch(Exception exception) {
            // thrown, test passed
            result = exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof NotEnoughWordsException);
    }

    /**
     * tries to initialize with too few words
     */
    @Test
    public void testNotEnoughWords() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/not_enough_words.txt");
        } catch(Exception exception) {
            result = exception;
            // thrown, test passed
        }
        assertNotNull(result);
        assertTrue(result instanceof NotEnoughWordsException);
    }

    /**
     * tries to initialize with a good amount of words, but with not a minimum amount of valid words
     */
    @Test
    public void testNotEnoughValidWords() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/no_enough_valid_words.txt");
        } catch(Exception exception) {
            result = exception;
            // thrown, test passed
        }
        assertNotNull(result);
        assertTrue(result instanceof NotEnoughWordsException);
    }

}
