package com.github.tqs;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.WordProvider;
import org.junit.Before;
import org.junit.Test;

public class WordProviderTest {

    private WordProvider invalidProvider;
    private WordProvider provider;

    @Before
    public void setUp() {
        invalidProvider = new WordProvider(5);
        provider = new WordProvider(5);
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
        assert result==null;
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
        assert result==null;
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
        assert result != null;
        assert result instanceof UnableToReadWordsException;
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
        assert result != null;
        assert result instanceof NotEnoughWordsException;
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
        assert result != null;
        assert result instanceof NotEnoughWordsException;
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
        assert result != null;
        assert result instanceof NotEnoughWordsException;
    }

}
