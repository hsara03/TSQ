package com.github.tqs.model;

import com.github.tqs.exceptions.game.NoTargetWordException;
import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.exceptions.word.AlreadySpelledException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;

import java.io.*;
import java.nio.file.Files;
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

    public Game(String path, int minimumWords, Difficulty difficulty) throws NotEnoughWordsException, UnableToReadWordsException, IOException {
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

    public void addListener(GameListener listener){
        this.listeners.add(listener);
    }

    private TimerTask getEndOfTimeTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if(!playing) return;
                playing=false;
                try {
                    highscore.setHighscore(highscore.getLastScore());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (GameListener listener: listeners){
                    listener.gameEnded();
                }
            }
        };
    }

    public void startGame() throws IOException {
        this.playing=true;
        for (int i = 0; i < this.scoreMultiplier; i++) {
            targetWords.add(provider.getNextWord(getHeadroom(), getEndOfTimeTask()));
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

    public void handleType(char typed) throws InvalidNextCharException, AlreadySpelledException, RanOutOfTimeException, NoTargetWordException {
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
            this.highscore.incrementCurrentScore(this.scoreMultiplier);
            difficulty++;
            targetWords.remove(word);
            word=null;
            for (GameListener listener: listeners) {
                listener.wordTyped();
            }
            CompletableFuture.runAsync(() -> {
                try {
                    targetWords.add(provider.getNextWord(getHeadroom(), getEndOfTimeTask()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            for (GameListener listener: listeners) {
                listener.charTyped();
            }
        }
    }

    public int getScore() {
        return this.highscore.getLastScore();
    }
    protected int getScoreMultiplier() {
        return this.scoreMultiplier;
    }
    public boolean isPlaying() {
        return playing;
    }
}
