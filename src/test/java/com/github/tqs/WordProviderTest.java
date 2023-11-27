package com.github.tqs;

import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.Word;
import com.github.tqs.model.WordProvider;
import org.junit.jupiter.api.BeforeEach; // JUnit 5 BeforeEach import
import org.junit.jupiter.api.Test; // JUnit 5 Test import

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*; // JUnit 5 Assertions import
import static org.mockito.Mockito.*;



public class WordProviderTest {

    private WordProvider invalidProvider;
    private WordProvider provider;
    private BufferedReader mockedReader;



    // Setup method to initialize common resources before each test
    @BeforeEach
    public void setUp() throws IOException {
        // Mocking a BufferedReader to simulate reading lines from a file
        mockedReader = mock(BufferedReader.class);
        when(mockedReader.readLine()).thenReturn("test", (String) null);

        //Creating instances of WordProvider for testing
        provider = new WordProvider(10, mockedReader);
        invalidProvider = new WordProvider(5, mockedReader);
    }

    // Test case to check if getNextWord() returns a non-null Word with content "test"
    @Test
    public void testGetNextWord() throws IOException {
        Word word = provider.getNextWord();
        assertNotNull(word);
        assertEquals("test", word.getContent());
    }


    // Test case to check if reading a valid word list file doesn't throw an exception
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

    // Test case to check if reading multiple words from a valid file doesn't throw an exception
    @Test
    public void testNextWords() {
        Exception result = null;
        try {
            this.provider.readWordFile("src/main/resources/words.mini.txt");
            for (int i = 0; i < 10; i++) {
                assertNotNull(this.provider.getNextWord());
            }
        } catch (Exception exception){
            // unexpected
            result=exception;
        }
        assertNull(result);
    }


    // Test case to check if reading an invalid word list file throws UnableToReadWordsException
    @Test
    public void testInvalidFile() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("this file doesnt exist");
        } catch(Exception exception) {
            // Exception thrown as expected
            result = exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof UnableToReadWordsException);
    }

    // Test case to check if reading an empty word list file throws NoWordsException
    @Test
    public void testEmptyWords() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/empty_word_list.txt");
        } catch(Exception exception) {
            // Exception thrown as expected
            result = exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof NoWordsException);
    }

    // Test case to check if reading a file with too few words throws NotEnoughWordsException
    @Test
    public void testNotEnoughWords() {
        Exception result = null;
        try {
            this.invalidProvider.readWordFile("src/main/resources/invalid/not_enough_words.txt");
        } catch(Exception exception) {
            result = exception;
            // Exception thrown as expected

        }
        assertNotNull(result);
        assertTrue(result instanceof NotEnoughWordsException);
    }


    // Test case to check if reading a file with not enough valid words throws NotEnoughWordsException
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
