package com.github.tqs.view;

import com.github.tqs.model.exceptions.provider.NoWordsException;
import com.github.tqs.model.exceptions.provider.NotEnoughWordsException;
import com.github.tqs.model.exceptions.provider.UnableToReadWordsException;
import com.github.tqs.view.exceptions.InputCancelledException;

import java.awt.*;
import java.io.IOException;

public interface WindowListener {

    void windowClosed() throws IOException, NotEnoughWordsException, FontFormatException, UnableToReadWordsException, InputCancelledException, NoWordsException;

}
