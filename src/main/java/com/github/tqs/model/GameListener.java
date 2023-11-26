package com.github.tqs.model;

public interface GameListener {

    void gameEnded();
    void gameStarted();
    void wordTyped();
    void invalidChar();
    void charTyped();

}
