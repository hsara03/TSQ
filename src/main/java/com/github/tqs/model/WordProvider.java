package com.github.tqs.model;

import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.word.InvalidWordException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;

import java.io.*;
import java.util.Random;
import java.util.TimerTask;

public class WordProvider {

    private static Random random = new Random();

    private String fileName;
    private BufferedReader bufferedReader;
    private int lineCount;
    private int minimumWords;

    public WordProvider(int minimumWords, BufferedReader reader){
        this.minimumWords=minimumWords;
        this.lineCount=0;
        this.bufferedReader = reader;
    }

    private void checkWord(String word) throws InvalidWordException{
        if(!word.matches("^[a-z]{3,}+$")){
            throw new InvalidWordException();
        }
    }

    // Read words from the word file, checking for validity and minimum word count
    public void readWordFile(String filePath) throws UnableToReadWordsException, NotEnoughWordsException, NoWordsException {
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
            if(validWords==0) throw new NoWordsException();
            if(validWords<minimumWords) throw new NotEnoughWordsException();
        } catch (IOException exception){
            throw new UnableToReadWordsException();
        }
    }

    // Get the next word from the word file
    public Word getNextWord() throws IOException{
        return this.getNextWord(-1, null);
    }

    //Get the next word with a specified headroom and TimerTak
    public Word getNextWord(int headroom, TimerTask task) throws IOException {
        String word=null;
        while(true){
            int offset = (int) (lineCount * WordProvider.random.nextFloat());
            if(offset<1) offset=1;
            for (int i = 0; i < offset; i++) {
                word = bufferedReader.readLine();
            }

            //If the end of the file is reached, reset the bufferedReader
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
