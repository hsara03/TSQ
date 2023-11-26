package com.github.tqs;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;
import com.github.tqs.model.Word;
import com.github.tqs.model.WordProvider;
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

    /**
     * try to spell out a single (valid) letter
     */
    @Test
    public void typeValidChar() throws IOException, AlreadySpelledException, RanOutOfTimeException, InvalidNextCharException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        word.type(content.charAt(0));
        assert word.getTyped()!=null;
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
        assert result!=null;
        assert result instanceof InvalidNextCharException;
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
        assert result!=null;
        assert result instanceof RanOutOfTimeException;
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
        assert result==null;
    }

    @Test
    public void spellCompletely() throws IOException, AlreadySpelledException, InvalidNextCharException, RanOutOfTimeException {
        Word word = provider.getNextWord();
        String content = word.getContent();
        for (int i = 0; i < content.length(); i++) {
            word.type(content.charAt(i));
        }
        assert word.isCompleted();
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
        assert result!=null;
        assert result instanceof AlreadySpelledException;
    }

}
