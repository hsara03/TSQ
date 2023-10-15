package com.github.tqs;

import com.github.tqs.exceptions.InvalidWordException;
import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;

import java.io.*;

public class WordProvider {

    private BufferedReader bufferedReader;
    private int minimumWords;

    WordProvider(int minimumWords){
        this.minimumWords=minimumWords;
    }

    private void checkWord(String word) throws InvalidWordException{
        if(!word.matches("^[a-z]+$")){
            throw new InvalidWordException();
        }
    }

    public void readWordFile(String filePath) throws UnableToReadWordsException, NotEnoughWordsException {
        File file = new File(filePath);
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            int validWords = 0;
            while(true){
                String word = bufferedReader.readLine();
                try {
                    this.checkWord(word);
                    validWords++;
                    if(validWords>=this.minimumWords) break;
                } catch(InvalidWordException exception){
                    // ignore word, invalid
                }
            }
            if(validWords<minimumWords) throw new NotEnoughWordsException();
        } catch (IOException exception){
            throw new UnableToReadWordsException();
        }
    }

    public String getNextWord(){
        while(true){
            
        }
    }


}
