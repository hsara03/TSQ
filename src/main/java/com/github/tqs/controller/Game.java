package com.github.tqs.controller;

import com.github.tqs.model.exceptions.game.NoTargetWordException;
import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.exceptions.score.InvalidNameException;
import com.github.tqs.model.exceptions.word.AlreadySpelledException;
import com.github.tqs.model.exceptions.word.InvalidNextCharException;
import com.github.tqs.model.exceptions.word.RanOutOfTimeException;
import com.github.tqs.model.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Game {

    private final Set<GameListener> listeners;
    private WordProvider provider;
    private final List<Word> targetWords;
    private Word word;
    private int difficulty;
    private HighScoreManager highscore;
    private boolean playing;
    private int scoreMultiplier;

    public Game(String path, int minimumWords, Difficulty difficulty) throws NotEnoughWordsException, UnableToReadWordsException, IOException, NoWordsException {
        switch (difficulty) {
            case NORMAL:
                this.difficulty = 10;
                break;
            case HARD:
                this.difficulty = 20;
                break;
            default:
                this.difficulty = 0;
                break;
        }
        this.listeners=new HashSet<>();
        this.word = null;
        this.targetWords= new ArrayList<>();
        this.playing = false;
        this.provider=new WordProvider(minimumWords, new BufferedReader(new FileReader("src/main/resources/words.txt")));
        this.provider.readWordFile(path);
        switch (difficulty) {
            case NORMAL:
                this.scoreMultiplier = 2;
                break;
            case HARD:
                this.scoreMultiplier = 3;
                break;
            default:
                this.scoreMultiplier = 1;
                break;
        }
        this.highscore = HighScoreManager.getInstance();
        this.highscore.readHighscore();
        this.highscore.resetCurrentScore();
    }

    public void handleType(char typed) throws InvalidNextCharException, AlreadySpelledException, RanOutOfTimeException, NoTargetWordException {

        // If there is no current target word, find a matching one based on the typed character
        if(word==null){
            for (Word typedWord:targetWords) {
                if(typedWord.getContent().charAt(0)==typed) {
                    word=typedWord;
                    break;
                }
            }
        }

        // If there is still no target word, throw an exception
        if(word==null) {
            throw new NoTargetWordException();
        }

        // Try to type the character into the current word.
        word.type(typed);

        //If the word is completed, update the score, increase difficulty, and get a new target word.
        if (word.isCompleted()) {
            this.highscore.incrementCurrentScore(this.scoreMultiplier);
            difficulty++;
            targetWords.remove(word);
            word = null;

            // Notify listeners that a word was typed.
            for (GameListener listener: listeners) {
                listener.wordTyped();
            }

            // Asynchronously get the next target word.
            CompletableFuture.runAsync(() -> {
                try {
                    targetWords.add(provider.getNextWord(getHeadroom(), getEndOfTimeTask()));
                } catch (IOException e) {
                    endGame();
                }
            });
        } else {
            // Notify listeners that a character was typed.
            for (GameListener listener: listeners) {
                listener.charTyped();
            }
        }
    }

    public void addListener(GameListener listener){
        this.listeners.add(listener);
    }

    public TimerTask getEndOfTimeTask() {
        return new TimerTask() {
            @Override
            public void run() {
                endGame();
            }
        };
    }

    public void endGame(){
        if(!playing) return;
        playing=false;
        try {
            highscore.setHighscore(highscore.getLastScore().score);
        } catch (IOException | InvalidNameException e) {
            // score won't be saved
            highscore.resetCurrentScore();
        }
        for (GameListener listener: listeners){
            listener.gameEnded();
        }
    }

    public void startGame() throws IOException {
        this.playing=true;
        for (int i = 0; i < this.scoreMultiplier; i++) {
            int delay = i*1000;
            Word word = provider.getNextWord(getHeadroom()+delay, getEndOfTimeTask());
            if(i>0){
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        targetWords.add(word);
                    }
                }, delay);
            } else {
                targetWords.add(word);
            }
        }
    }

    //Calculate and returns a time limit value for the player to complete a word.
    //The time is adjusted based n the game difficulty and ensure a minimum margin.
    private int getHeadroom(){
        float minHeadroom = 5;
        float headroom = 30 - difficulty;
        if(headroom<minHeadroom) headroom=minHeadroom;
        return (int) headroom*1000;
    }

    public List<Word> getTargetWords() {
        return targetWords;
    }

    // Get the current score
    public int getScore() {
        return this.highscore.getLastScore().score;
    }

    public int getScoreMultiplier() {
        return this.scoreMultiplier;
    }

    public boolean isPlaying() {
        return playing;
    }
}
