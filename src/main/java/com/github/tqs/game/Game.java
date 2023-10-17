package com.github.tqs.game;

import com.github.tqs.WordProvider;
import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
public class Game {

    private List<Word> words;
    private Player player;
    private WordProvider wordProvider;

    private final int BASE_Y =600; //IDK how it works size

    public Game() {
        player = new Player();
        words = new ArrayList<>();
        wordProvider= new WordProvider(100);

        try{
            wordProvider.readWordFile("src/main/resources/words.txt");
        } catch (NotEnoughWordsException | UnableToReadWordsException e) {
            throw new RuntimeException(e);
        }
    }

    public void loopPrincipal() {
        while(player.haslifes()){
            updateWords();
            checkCollisions();
        }
    }

    private void updateWords(){
        for (Word word: words) {
            // word.move();
        }
    }

    private void checkCollisions() {
        Iterator<Word> iterator= words.iterator();
        while(iterator.hasNext()) {
            Word word = iterator.next();

            /*
            if (word.hasCollidedWithBase(BASE_Y)){
                player.decreaseLife();
                iterator.remove(); //eliminar la paraula que ha colisionat
            }*/
        }
    }
}
