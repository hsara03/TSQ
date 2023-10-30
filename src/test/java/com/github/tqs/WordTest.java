package com.github.tqs;

import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;
import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;
import com.github.tqs.game.Word;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.TimerTask;

public class WordTest {

    private WordProvider provider;

    @Before
    public void setUp() throws NotEnoughWordsException, UnableToReadWordsException, java.io.IOException {
        provider = new WordProvider(5);
        provider.readWordFile("src/main/resources/words.mini.txt");
    }

    @Test
    public void typeValidChar() throws IOException, AlreadySpelledException, RanOutOfTimeException, InvalidNextCharException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        word.type(content.charAt(0));
    }

    @Test
    public void typeInvalidChar() throws IOException, AlreadySpelledException, RanOutOfTimeException {
        Word word = provider.getNextWord();
        try {
            word.type('.');
        } catch(InvalidNextCharException exception){
            // expected
        }
    }

    @Test
    public void ranOutOfTime() throws IOException, AlreadySpelledException, InvalidNextCharException, InterruptedException {
        Word word = provider.getNextWord(0, new TimerTask() {
            @Override
            public void run() {
                // dummy task
            }
        });
        Thread.sleep(1);
        try {
            word.type(word.getContent().charAt(0));
        } catch(RanOutOfTimeException exception){
            // expected
        }
    }

    @Test
    public void spellCompletely() throws IOException, AlreadySpelledException, InvalidNextCharException, RanOutOfTimeException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        for (int i = 0; i < content.length(); i++) {
            word.type(content.charAt(i));
        }
    }

    @Test
    public void overspell() throws IOException, InvalidNextCharException, RanOutOfTimeException, AlreadySpelledException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        for (int i = 0; i < content.length(); i++) {
            word.type(content.charAt(i));
        }
        try {
            word.type('a');
        } catch(AlreadySpelledException exception){
            // expected
        }
    }

}
