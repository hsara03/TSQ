package com.github.tqs;

import com.github.tqs.exceptions.InvalidWordException;
import com.github.tqs.exceptions.NotEnoughWordsException;
import com.github.tqs.exceptions.UnableToReadWordsException;

import java.io.*;
import java.util.Random;

public class WordProvider {

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
            this.lineCount++;
            int validWords = 0;
            while(true){
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

    public String getNextWord() throws IOException {
        String word=null;
        while(true){
            int offset = lineCount/3 * new Random().nextInt();
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
        return word;
    }


}
