package com.github.tqs.model;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class HighScoreManager {

    private static String path = "highscore.txt";
    private static HighScoreManager instance;

    private String filePath;
    private File highscoreFile;
    private int highscore;
    private int lastScore;

    private HighScoreManager(String filePath) throws IOException {
        this.filePath = filePath;
        this.highscoreFile=new File(this.filePath);
        if(!Files.isRegularFile(this.highscoreFile.toPath())){
            Files.createFile(this.highscoreFile.toPath());
        }
        this.highscoreFile.setWritable(true);
        this.highscoreFile.setReadable(true);
        this.lastScore=-1;
    }

    public void resetCurrentScore(){
        this.lastScore=0;
    }

    public void incrementCurrentScore(int amount){
        this.lastScore+=amount;
    }

    /**
     * will return -1 if no last score is present
     */
    public int getLastScore(){
        return this.lastScore;
    }

    public static HighScoreManager getInstance() throws IOException {
        if(HighScoreManager.instance==null){
            HighScoreManager.instance = new HighScoreManager(HighScoreManager.path);
        }
        return HighScoreManager.instance;
    }

    public void setHighscore(int score) throws IOException {
        this.readHighscore();
        if(this.highscore<score) this.highscore=score;
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.highscoreFile, true));
        PrintWriter out = new PrintWriter(writer);
        out.println(score);
        writer.close();
    }

    public int readHighscore() throws IOException {
        this.highscore=-1;
        Scanner myReader = new Scanner(this.highscoreFile);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            int score =Integer.parseInt(data);
            if(score>this.highscore) this.highscore=score;
            this.lastScore = score;
        }
        myReader.close();
        if(this.highscore<0) this.highscore=0;
        return this.highscore;
    }
}
