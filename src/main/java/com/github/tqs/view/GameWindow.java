package com.github.tqs.view;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.model.Difficulty;
import com.github.tqs.model.Game;
import com.github.tqs.model.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class GameWindow extends Observable {

    private Game game;
    private final int height = 600;
    private final int width = 900;
    private final int xPadding = 120;
    private final int yBottomPadding = 50;
    private final JFrame frame;
    private final Font customFont;
    private final Font customFontBold;

    public Game getGame() {
        return game;
    }

    public GameWindow(Difficulty difficulty) throws IOException, NotEnoughWordsException, UnableToReadWordsException, FontFormatException {
        this.game=new Game("src/main/resources/words.txt", 10, difficulty, new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyAll();
            }
        });
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
            }
        };

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    game.handleType(e.getKeyChar());
                    panel.repaint();
                } catch (Exception exception) {

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
}
