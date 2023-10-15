package com.github.tqs;

import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;

public class WordProviderTest {

    private WordProvider provider;
    void setUp() {
        provider = new WordProvider(5);
    }
    public void testValidFile() throws UnableToReadWordsException, NotEnoughWordsException {
        WordProvider provider= new WordProvider(5);
        provider.readWordFile("src/main/resources/words.txt");
    }


}
