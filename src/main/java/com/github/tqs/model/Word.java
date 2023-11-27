package com.github.tqs.model;

import com.github.tqs.model.exceptions.word.AlreadySpelledException;
import com.github.tqs.model.exceptions.word.InvalidNextCharException;
import com.github.tqs.model.exceptions.word.RanOutOfTimeException;

import java.util.Timer;
import java.util.TimerTask;

public class Word {

    private String content;
    private String typed;
    private float x;
    private int time;
    private long start;
    private Timer timer;
    private TimerTask task;

    public Word(String content, float x, int time, TimerTask task) {
        this.content=content;
        this.x=x;
        this.start=System.currentTimeMillis();
        this.time=time;
        this.typed="";
        this.timer=new Timer();
        this.task = task;
        if(time>0){
            this.timer.schedule(this.task, time);
        }

    }

    public long getStart(){
        return this.start;
    }

    public boolean ranOutOfTime() {
        if(this.time<0) return false;
        return start+time<System.currentTimeMillis();
    }

    public String getTyped(){
        return this.typed;
    }

    //Handle the typing of a character
    public void type(char letter) throws AlreadySpelledException, InvalidNextCharException, RanOutOfTimeException {
        // Check if the word is already completely typed
        if(this.typed.length()>=this.content.length()) throw new AlreadySpelledException();

        //Get the next character that should be typed
        String nextCharString = this.content.substring(this.typed.length(),this.typed.length()+1);
        char nextChar = nextCharString.charAt(0);

        // Check if the typed character matches the expected next character
        if(nextChar!=letter) throw new InvalidNextCharException();

        // Check if the word ran out of time
        if(this.ranOutOfTime()) throw new RanOutOfTimeException();

        // Append the typed character to the already typed characters
        this.typed+=nextChar;

        if(isCompleted()){
            if(this.task!=null){
                this.task.cancel();
                this.timer.cancel();
            }
        }
    }

    public String getContent() {
        return this.content;
    }

    // Get the X-coordinate position of the word on the screen
    public float getX() {
        return this.x;
    }

    /**
     * Retunrs the percentage of time that has passed
     * 0% if all time is still left, 100% if the time has gone by.
     * @return The percentage of time passed
     */
    public float timePercent(){
        return (System.currentTimeMillis()-this.start)/(float)time;
    }

    public boolean isCompleted() {
        return content.equalsIgnoreCase(typed.toString());
    }


}
