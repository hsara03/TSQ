package com.github.tqs;

import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;
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

    @Test
    public void testValidFile() throws UnableToReadWordsException, NotEnoughWordsException {
        this.provider.readWordFile("src/main/resources/words.mini.txt");
    }

    @Test
    public void testNextWords() throws UnableToReadWordsException, NotEnoughWordsException, java.io.IOException, com.github.tqs.exceptions.NoWordsException {
        this.provider.readWordFile("src/main/resources/words.mini.txt");
        for (int i = 0; i < 10; i++) {
            System.out.println(this.provider.getNextWord());
        }
    }

    @Test
    public void testInvalidFile() throws NotEnoughWordsException {
        try {
            this.invalidProvider.readWordFile("this file doesnt exist");
        } catch(UnableToReadWordsException exception) {
            // thrown, test passed
        } catch(NotEnoughWordsException exception){
            throw exception;
        }
    }

    @Test
    public void testEmptyWords() throws UnableToReadWordsException, NotEnoughWordsException {
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/empty_word_list.txt");
        } catch(NotEnoughWordsException exception) {
            // thrown, test passed
        } catch(UnableToReadWordsException exception){
            throw exception;
        }
    }

    @Test
    public void testNotEnoughWords() throws UnableToReadWordsException, NotEnoughWordsException {
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/not_enough_words.txt");
        } catch(NotEnoughWordsException exception) {
            // thrown, test passed
        } catch(UnableToReadWordsException exception){
            throw exception;
        }
    }

    @Test
    public void testNotEnoughValidWords() throws UnableToReadWordsException, NotEnoughWordsException {
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/no_enough_valid_words.txt");
        } catch(NotEnoughWordsException exception) {
            // thrown, test passed
        } catch(UnableToReadWordsException exception){
            throw exception;
        }
    }

}
