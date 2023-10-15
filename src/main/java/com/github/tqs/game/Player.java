package com.github.tqs.game;

public class Player {
    private int lifes;
    private int puntuation;

    public Player() {
        this.lifes=3;
        this.puntuation=0;
    }

    public void decreaseLife() {
        this.lifes -=1;
    }

    public void increaseScore(int points) {
        this.puntuation+=points;
    }

    public boolean haslifes() {
        return this.lifes > 0;
    }

    public int getLifes() {
        return lifes;
    }

    public int getPuntuation() {
        return puntuation;
    }
}
