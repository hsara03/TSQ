package com.github.tqs.game;

import com.github.tqs.WordProvider;
import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    private WordProvider provider;
    private final List<Word> targetWords;
    private Word word;
    private int difficulty;
    private int highscore;
    private boolean playing;
    private File highscoreFile;

    private int getHeadroom(){
        float minHeadroom = 5;
        float headroom = 30 - difficulty;
        if(headroom<minHeadroom) headroom=minHeadroom;
        return (int) headroom*1000;
    }

    public Game() throws NotEnoughWordsException, UnableToReadWordsException, IOException {
        this.word = null;
        this.difficulty = 0;
        this.targetWords= new ArrayList<>();
        this.highscore = 0;
        this.playing = false;
        this.provider=new WordProvider(50);
        this.provider.readWordFile("src/main/resources/words.txt");
        this.readHighscore();
    }

    public void setHighscore(int highscore) throws IOException {
        this.readHighscore();
        this.highscore=highscore;
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.highscoreFile));
        writer.write(String.valueOf(highscore));
        writer.close();
    }

    public void readHighscore() throws IOException {
        this.highscore=-1;
        if(this.highscoreFile==null){
            this.highscoreFile=new File("highscore.txt");
        }
        if(!Files.isRegularFile(this.highscoreFile.toPath())){
            Files.createFile(this.highscoreFile.toPath());
        }
        this.highscoreFile.setWritable(true);
        this.highscoreFile.setReadable(true);
        Scanner myReader = new Scanner(this.highscoreFile);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            this.highscore=Integer.parseInt(data);
        }
        myReader.close();
        if(this.highscore<0) this.highscore=0;
    }
}
