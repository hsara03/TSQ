package com.github.tqs;

import com.github.tqs.exceptions.word.InvalidWordException;
import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.game.Word;

import java.io.*;
import java.util.Random;
import java.util.TimerTask;

public class WordProvider {

    private static Random random = new Random();

    private String fileName;
    private BufferedReader bufferedReader;
    private int lineCount;
    private int minimumWords;

    public WordProvider(int minimumWords){
        this.minimumWords=minimumWords;
        this.lineCount=0;
    }

    private void checkWord(String word) throws InvalidWordException{
        if(!word.matches("^[a-z]+$")){
            throw new InvalidWordException();
        }
    }

    public void readWordFile(String filePath) throws UnableToReadWordsException, NotEnoughWordsException {
        this.fileName=filePath;
        File file = new File(filePath);
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            int validWords = 0;
            while(true){
                this.lineCount++;
                String word = bufferedReader.readLine();
                if(word==null) break;
                try {
                    this.checkWord(word);
                    validWords++;
                } catch(InvalidWordException exception){
                    // ignore word, invalid
                }
            }
            if(validWords<minimumWords) throw new NotEnoughWordsException();
        } catch (IOException exception){
            throw new UnableToReadWordsException();
        }
    }

    public Word getNextWord() throws IOException{
        return this.getNextWord(-1, null);
    }

    public Word getNextWord(int headroom, TimerTask task) throws IOException {
        String word=null;
        while(true){
            int offset = (int) (lineCount * WordProvider.random.nextFloat());
            if(offset<1) offset=1;
            for (int i = 0; i < offset; i++) {
                word = bufferedReader.readLine();
            }

            if(word==null) {
                this.bufferedReader.close();
                this.bufferedReader = new BufferedReader(new FileReader(this.fileName));
                continue;
            }

            try {
                checkWord(word);
                break;
            } catch(InvalidWordException e){
                // the word was invalid, skip and try another
            }
        }
        System.out.println("provided word");
        return new Word(word, WordProvider.random.nextFloat(), headroom, task);
    }


}
