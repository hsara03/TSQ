package com.github.tqs.model;

import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;

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

    public void type(char letter) throws AlreadySpelledException, InvalidNextCharException, RanOutOfTimeException {
        if(this.typed.length()>=this.content.length()) throw new AlreadySpelledException();
        String nextCharString = this.content.substring(this.typed.length(),this.typed.length()+1);
        char nextChar = nextCharString.charAt(0);
        if(nextChar!=letter) throw new InvalidNextCharException();
        if(this.ranOutOfTime()) throw new RanOutOfTimeException();
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

    public float getX() {
        return this.x;
    }

    /**
     * returns 0% if all time is still left, 100% if time has gone by
     * @return
     */
    public float timePercent(){
        return (System.currentTimeMillis()-this.start)/(float)time;
    }

    public boolean isCompleted() {
        return content.equalsIgnoreCase(typed.toString());
    }


}
