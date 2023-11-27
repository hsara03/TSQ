package com.github.tqs.model;

public class Score {

    public final String name;
    public final int score;

    public Score(String name, int score){
        this.name=name;
        this.score=score;
    }

    //method is used to check whether an instance of 'Score' contains valid data
    public boolean hasData(){
        return name!=null && score>=0;
    }

}
