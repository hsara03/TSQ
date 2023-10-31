package com.github.tqs.game;

import com.github.tqs.WordProvider;
import com.github.tqs.exceptions.game.NoTargetWordException;
import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

public class Game {

    private WordProvider provider;
    private final List<Word> targetWords;
    private Word word;
    private int difficulty;
    private int highscore;
    private boolean playing;
    private File highscoreFile;

    public Game(String path, int minimumWords) throws NotEnoughWordsException, UnableToReadWordsException, IOException {
        this.word = null;
        this.difficulty = 0;
        this.targetWords= new ArrayList<>();
        this.highscore = 0;
        this.playing = false;
        this.provider=new WordProvider(minimumWords);
        this.provider.readWordFile(path);
        this.readHighscore();
    }

    public void startGame() throws IOException {
        this.playing=true;
        for (int i = 0; i < 2; i++) {
            targetWords.add(provider.getNextWord(getHeadroom(), new TimerTask() {
                @Override
                public void run() {
                    playing = false;
                    try {
                        setHighscore(difficulty);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }
    }

    public List<Word> getTargetWords() {
        return targetWords;
    }

    private int getHeadroom(){
        float minHeadroom = 5;
        float headroom = 30 - difficulty;
        if(headroom<minHeadroom) headroom=minHeadroom;
        return (int) headroom*1000;
    }

    public void setHighscore(int highscore) throws IOException {
        this.readHighscore();
        this.highscore=highscore;
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.highscoreFile));
        writer.write(String.valueOf(highscore));
        writer.close();
    }

    public void handleType(char typed) throws IOException, InvalidNextCharException, AlreadySpelledException, RanOutOfTimeException, NoTargetWordException {
        if(word==null){
            for (Word typedWord:targetWords) {
                if(typedWord.getContent().charAt(0)==typed) {
                    word=typedWord;
                    break;
                }
            }
        }
        if(word==null) {
            throw new NoTargetWordException();
        }
        word.type(typed);
        if (word.isCompleted()) {
            difficulty++;
            targetWords.remove(word);
            targetWords.add(provider.getNextWord(getHeadroom(), new TimerTask() {
                @Override
                public void run() {
                    System.out.println("ran out of time");
                }
            }));
            word=null;
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public int readHighscore() throws IOException {
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
        return this.highscore;
    }
}
