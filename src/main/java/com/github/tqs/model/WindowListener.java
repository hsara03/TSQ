package com.github.tqs.model;

import com.github.tqs.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.exceptions.provider.UnableToReadWordsException;

import java.awt.*;
import java.io.IOException;

public interface WindowListener {

    void windowClosed() throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException;

}
