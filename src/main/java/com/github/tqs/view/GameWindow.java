package com.github.tqs.view;

import com.github.tqs.controller.Game;
import com.github.tqs.model.exceptions.game.NoTargetWordException;
import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.exceptions.word.InvalidNextCharException;
import com.github.tqs.model.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

public class GameWindow extends Observable implements GameListener {

    private Game game;
    private final int height = 600;
    private final int width = 900;
    private final int xPadding = 120;
    private final int yBottomPadding = 50;
    private final JFrame frame;
    private final Font customFont;
    private final Font customFontBold;
    private final File ding = new File("src/main/resources/sounds/ding.wav");
    private final File negative = new File("src/main/resources/sounds/negative.wav");
    private final File wrong = new File("src/main/resources/sounds/wrong.wav");
    private final File type = new File("src/main/resources/sounds/type.wav");
    private final Runnable endMethod;

    public Game getGame() {
        return game;
    }

    public GameWindow(Difficulty difficulty, Runnable endMethod) throws IOException, NotEnoughWordsException, UnableToReadWordsException, FontFormatException, NoWordsException {
        this.endMethod=endMethod;
        this.game=new Game("src/main/resources/words.txt", 10, difficulty);
        this.game.addListener(this);
        this.frame = new JFrame("Words");
        this.frame.setSize(width, height);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/Changa-SemiBold.ttf"));
        this.customFontBold = customFont.deriveFont(Font.BOLD, 16);
    }

    public void startPlaying() throws IOException, FontFormatException {
        game.startGame();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setFont(customFontBold);

                for (Word word : game.getTargetWords()) {
                    String targetContent = word.getContent();
                    String typedContent = word.getTyped();
                    FontMetrics fm = g.getFontMetrics();
                    int startX = xPadding + (int) ((width - xPadding * 2) * word.getX());
                    int startY = (int) (word.timePercent() * (height - yBottomPadding));

                    for (int i = 0; i < targetContent.length(); i++) {
                        if (i < typedContent.length()) {
                            g.setColor(Color.GREEN);
                        } else {
                            g.setColor(Color.RED);
                        }
                        String charAsString = Character.toString(targetContent.charAt(i));
                        g.drawString(charAsString, startX, startY);
                        startX += fm.stringWidth(charAsString);
                    }
                }
                drawScore(g);
            }

            private void drawScore(Graphics g) {
                String scoreText = "Puntuación:  " + game.getScore();
                g.setColor(Color.BLACK);
                g.drawString(scoreText, width-200, 30);
            }

        };



        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    game.handleType(e.getKeyChar());
                    panel.repaint();
                } catch (InvalidNextCharException | NoTargetWordException exception) {
                    invalidChar();
                } catch (Exception exception){
                    // ignore
                }
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                panel.repaint();
            }
        }, 0, 1000 / 60);

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    public void gameEnded() {
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(negative));
            clip.start();
            this.frame.dispose();
            this.endMethod.run();
        } catch (Exception e){
            System.out.println("this system doesn't support audio output");
        }
    }

    @Override
    public void gameStarted() {

    }

    @Override
    public void wordTyped() {
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(ding));
            clip.start();
        } catch (Exception e){
            System.out.println("this system doesn't support audio output");
        }
    }

    @Override
    public void invalidChar() {
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(wrong));
            clip.start();
        } catch (Exception e){
            System.out.println("this system doesn't support audio output");
        }
    }

    @Override
    public void charTyped() {
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(type));
            clip.start();
        } catch (Exception e){
            System.out.println("this system doesn't support audio output");
        }
    }
}
