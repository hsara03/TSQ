package com.github.tqs;

import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;
import com.github.tqs.exceptions.word.InvalidNextCharException;
import com.github.tqs.exceptions.word.RanOutOfTimeException;
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

    private static WordProvider provider;
    private static List<Word> targetWords = new ArrayList<>();
    private static Word word;
    private static int difficulty = 0;
    private final static int height = 600;
    private final static int width = 900;
    private final static int xPadding = 120;
    private final static int yBottomPadding = 50;

    public static int getHeadroom(){
        float minHeadroom = 5;
        float headroom = 30 - difficulty;
        if(headroom<minHeadroom){
            headroom=minHeadroom;
        }
        return (int) headroom*1000;
    }

    public static void main() throws NotEnoughWordsException, UnableToReadWordsException, IOException, FontFormatException {

        try {
            provider = new WordProvider(30);
            provider.readWordFile("src/main/resources/words.txt");
            for (int i = 0; i < 2; i++) {
                targetWords.add(provider.getNextWord(getHeadroom(), new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("ran out of time");
                    }
                }));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

                g.setFont(customFontBold);

                for (Word word : targetWords) {
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
            }

        };

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(word==null){
                    for (Word typedWord:targetWords) {
                        if(typedWord.getContent().charAt(0)==e.getKeyChar()) {
                            word=typedWord;
                            break;
                        }
                    }
                }
                if(word==null) return;
                try {
                    word.type(e.getKeyChar());
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
                } catch (InvalidNextCharException exception) {
                    // the typed char does not equal to the beginning on this word
                } catch(RanOutOfTimeException exception){
                    // unexpected exception
                    throw new RuntimeException(exception);
                } catch(Exception exception){
                    throw new RuntimeException(exception);
                }

                panel.repaint();
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
