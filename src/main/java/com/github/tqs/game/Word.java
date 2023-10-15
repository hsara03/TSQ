package com.github.tqs.game;

public class Word {

    private String content;
    private  int posX;
    private int posY;
    private int velocity;

    public Word(String content, int posX, int posY, int velocity) {
        this.content=content;
        this.posX=posX;
        this.posY=posY;
        this.velocity=velocity;
    }

    public void move() {
        this.posY +=velocity;
    }

    public boolean hasCollidedWithBase(int baseY) {
        return this.posY>=baseY;
    }

    public String getContent() {
        return content;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
