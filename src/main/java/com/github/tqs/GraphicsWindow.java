package com.github.tqs;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;
import com.github.tqs.game.Game;
import com.github.tqs.game.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GraphicsWindow {

    private Game game;
    private final static int height = 600;
    private final static int width = 900;
    private final static int xPadding = 120;
    private final static int yBottomPadding = 50;

    public static void main() throws NotEnoughWordsException, UnableToReadWordsException, IOException, FontFormatException {

        Game game = new Game("src/main/resources/words.txt", 50);
        game.startGame();

        JFrame frame = new JFrame("Words");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/Changa-SemiBold.ttf"));
        Font customFontBold = customFont.deriveFont(Font.BOLD, 16);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if(game.isPlaying()){
                    g.setFont(customFontBold);

                    for (Word word : game.getTargetWords()) {
                        String targetContent = word.getContent();
                        String typedContent = word.getTyped();
                        FontMetrics fm=g.getFontMetrics();
                        int startX = xPadding +(int) ((width- xPadding *2) * word.getX());
                        int startY = (int) (word.timePercent() * (height-yBottomPadding));

                        for (int i = 0; i < targetContent.length(); i++) {
                            if (i < typedContent.length()) {
                                g.setColor(Color.GREEN);  // The character matches and is typed correctly
                            } else {
                                g.setColor(Color.RED);    // The character is yet to be typed or is typed wrongly
                            }
                            String charAsString = Character.toString(targetContent.charAt(i));
                            g.drawString(charAsString, startX, startY);
                            startX += fm.stringWidth(charAsString); // Increment by the width of the character we just drew
                        }
                    }
                } else {
                    try {
                        g.drawString("score: "+ String.valueOf(game.readHighscore()), xPadding, 50);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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
                } catch (Exception exception){
                    // XD
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
