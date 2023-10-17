package com.github.tqs.game;

import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;

public class Word {

    private String content;
    private String typed;
    private int x;
    private int ending;

    public Word(String content, int x, int ending) {
        this.content=content;
        this.x=x;
        this.ending=ending;
        this.typed="";
    }

    public boolean ranOutOfTime() {
        return System.currentTimeMillis()>this.ending;
    }

    public String getTyped(){
        return this.typed;
    }

    public void type(char letter) throws AlreadySpelledException, InvalidNextCharException, RanOutOfTimeException {
        String nextCharString = this.content.substring(this.typed.length(),1);
        if(nextCharString.isEmpty()) throw new AlreadySpelledException();
        char nextChar = nextCharString.charAt(0);
        if(nextChar!=letter) throw new InvalidNextCharException();
        if(this.ranOutOfTime()) throw new RanOutOfTimeException();
        this.typed+=nextChar;
    }

    public String getContent() {
        return this.content;
    }

    public int getX() {
        return this.x;
    }

}
