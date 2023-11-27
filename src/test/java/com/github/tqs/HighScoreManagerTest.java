package com.github.tqs;

import com.github.tqs.model.HighScoreManager;
import com.github.tqs.model.Score;
import com.github.tqs.model.exceptions.score.InvalidNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class HighScoreManagerTest {

    private File getHighscoreFile(){
        return new File("./highscore.txt");
    }

    private int getNumberOfLines(){
        int lineCount;
        try (Stream<String> stream = Files.lines(this.getHighscoreFile().toPath(), StandardCharsets.UTF_8)) {
            lineCount = (int) stream.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lineCount;
    }

    // Creates a new file with the provided lines as content
    private File createFileWithData(List<String> lines) throws IOException {
        this.deleteHighscoreFile();
        File file = getHighscoreFile();
        if(!Files.isRegularFile(file.toPath())){
            Files.createFile(file.toPath());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        PrintWriter out = new PrintWriter(writer);
        for (String line:lines) {
            out.println(line);
        }
        writer.close();
        return file;
    }

    private void deleteHighscoreFile(){
        File file = getHighscoreFile();
        file.delete();
    }

    @BeforeEach
    public void setUp() throws IOException {
        deleteHighscoreFile();
    }

    // Test: Create method, checks if the highscore file is created
    @Test
    public void testCreate(){
        deleteHighscoreFile();
        Exception result = null;
        try {
            HighScoreManager.getInstance().readHighscore();
        } catch (IOException exception){
            result=exception;
        }
        assertNull(result);
        assertTrue(getHighscoreFile().exists());
    }

    // Test: Invalid data points, checks if invalid data points are removed from the highscore file
    @Test
    public void testInvalidDataPoints() throws IOException {
        List<String> invalidData = new ArrayList<>();
        invalidData.add("prueba");
        createFileWithData(invalidData);
        assertEquals(1, getNumberOfLines());
        HighScoreManager.getInstance().readHighscore();
        assertEquals(0, getNumberOfLines());
    }

    // Test: Invalid numbers, checks if lines with invalid numbers are removed from the highscore file
    @Test
    public void testInvalidNumbers() throws IOException {
        List<String> invalidData = new ArrayList<>();
        invalidData.add("prueba invalid_number");
        createFileWithData(invalidData);
        assertEquals(1, getNumberOfLines());
        HighScoreManager.getInstance().readHighscore();
        assertEquals(0, getNumberOfLines());
    }

    // Test: Score read and write, checks if scores are correctly read and written to the highscore file
    @Test
    public void testScoreReadWrite() throws IOException, InvalidNameException {
        deleteHighscoreFile();
        HighScoreManager manager = HighScoreManager.getInstance();
        manager.readHighscore();
        manager.setPlayerName("test1");
        manager.setHighscore(50);
        manager.setPlayerName("test2");
        manager.setHighscore(25);
        manager.resetCurrentScore();
        Score highscore = manager.readHighscore();
        assertEquals(50, highscore.score);
        assertEquals("test1", highscore.name);
        assertEquals(25, manager.getLastScore().score);
        assertEquals("test2", manager.getLastScore().name);
    }

    // Test to check if HighScoreManager throws an exception when an invalid player name is set
    @Test
    public void testInvalidPlayerName() {
        Exception result = null;
        try {
            HighScoreManager.getInstance().setPlayerName("hola hola hola");
        } catch (Exception exception){
            result=exception;
        }
        assertNotNull(result);
        assertTrue(result instanceof InvalidNameException);
    }

    // Test to check if HighScoreManager does not throw an exception when a valid player name is set
    @Test
    public void testValidPlayerName() {
        Exception result = null;
        try {
            HighScoreManager.getInstance().setPlayerName("hola");
        } catch (InvalidNameException | IOException exception){
            result=exception;
        }
        assertNull(result);
    }

    // Test to check the hasData functionality in the Score class
    @Test
    public void testDataValue() {
        Score filledScore = new Score("name", 10);
        assertTrue(filledScore.hasData());

        Score emptyScore = new Score(null, -1);
        assertFalse(emptyScore.hasData());
    }

    @Test
    public void testInvalidStoredUsername() throws IOException {
        List<String> invalidData = new ArrayList<>();
        invalidData.add("0 00ínválíd00");
        createFileWithData(invalidData);
        assertEquals(1, getNumberOfLines());
        HighScoreManager.getInstance().readHighscore();
        assertEquals(0, getNumberOfLines());
    }

    @Test
    public void testIncrement() throws IOException {
        HighScoreManager manager = HighScoreManager.getInstance();
        manager.resetCurrentScore();
        manager.incrementCurrentScore(10);
        assertEquals(10, manager.getLastScore().score);
    }


}
