package com.github.tqs.model;

import com.github.tqs.model.exceptions.score.InvalidNameException;
import com.github.tqs.model.exceptions.score.ScoreDataInvalid;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class HighScoreManager {

    private static String path = "highscore.txt";
    private static HighScoreManager instance;
    private String playerName;
    private String filePath;
    private File highscoreFile;
    private Score highscore;
    private Score lastScore;

    private HighScoreManager(String filePath) throws IOException {
        this.filePath = filePath;

        // Create the highscore file if it doesn't exist
        this.createFile();

        // Set file permissions
        this.highscoreFile.setWritable(true);
        this.highscoreFile.setReadable(true);

        // Initialize lastScore with default values
        this.lastScore=new Score(null, -1);
    }

    private File createFile() throws IOException {
        this.highscoreFile=new File(this.filePath);
        if(!Files.isRegularFile(this.highscoreFile.toPath())){
            Files.createFile(this.highscoreFile.toPath());
        }
        return this.highscoreFile;
    }

    // Set the player name for scoring, throws exception if the name is invalid
    public void setPlayerName(String name) throws InvalidNameException {
        if(!name.matches(getPlayerNameRegex())) throw new InvalidNameException();
        this.playerName=name;
        this.lastScore=new Score(name, this.lastScore.score);
    }


    private String getPlayerNameRegex(){
        return "^[A-Za-z0-9]{1,16}+$";
    }

    // Reset the current score to default values when the player start to play
    public void resetCurrentScore(){
        this.lastScore=new Score(this.playerName, 0);
    }

    // Increment the current score by a specified amount
    public void incrementCurrentScore(int amount){
        this.lastScore = new Score(this.playerName, this.lastScore.score + amount);
    }

    /**
     * Returns the last obtained score.
     * If no last score is present, it returns a Score object with a score of -1.
     */
    public Score getLastScore(){
        return this.lastScore;
    }

    // Get the singleton instance of HighScoreManager
    public static HighScoreManager getInstance() throws IOException {
        if(HighScoreManager.instance==null){
            HighScoreManager.instance = new HighScoreManager(HighScoreManager.path);
        }
        return HighScoreManager.instance;
    }

    // Set the highscore if the provided score is higher, write to the highscore file
    public void setHighscore(int score) throws IOException, InvalidNameException {
        String currentName = this.playerName;
        if(this.playerName==null) throw new InvalidNameException();
        this.readHighscore();
        this.playerName=currentName;
        if(this.highscore.score<score) this.highscore=new Score(playerName, score);
        this.lastScore=new Score(currentName, score);
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.highscoreFile, true));
        PrintWriter out = new PrintWriter(writer);
        out.println(score + " " + this.playerName);
        writer.close();
    }

    // Read the highscore from the file, update highscore and lastScore
    public Score readHighscore() throws IOException {
        this.highscore=new Score(null, -1);
        try {
            Scanner scoreReader;
            try {
                scoreReader = new Scanner(this.highscoreFile);
            } catch (FileNotFoundException fileNotFoundException){
                this.createFile();
                scoreReader = new Scanner(this.highscoreFile);
            }
            while (scoreReader.hasNextLine()) {
                String data = scoreReader.nextLine();
                String[] parts = data.split(" ");
                if(parts.length<2) {
                    throw new ScoreDataInvalid();
                }
                int score = 0;
                try {
                    score = Integer.parseInt(parts[0]);
                } catch (NumberFormatException exception){
                    throw new ScoreDataInvalid();
                }
                if(!parts[1].matches(getPlayerNameRegex())) {
                    throw new ScoreDataInvalid();
                }
                String name = parts[1];
                if(score>this.highscore.score) {
                    this.highscore=new Score(name, score);
                }
                this.lastScore = new Score(name, score);
                this.playerName = name;
            }
            scoreReader.close();
        } catch (IOException exception) {
            throw exception;
        } catch (ScoreDataInvalid exception) {
            // clear invalid highscore data
            PrintWriter writer = new PrintWriter(highscoreFile);
            writer.print("");
            writer.close();
        }
        return this.highscore;
    }
}
