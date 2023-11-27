package com.github.tqs.view;

import javax.swing.*;

public class ErrorWindow {

    // Method to display an error message in case of IOException (read/write error)
    public void ioException(){
        JOptionPane.showMessageDialog(null,"Ha habido un error de lectura/escritura mientras se guardaba y so leia la puntuación o se leía el diccionario, por favor, asegurate de tener espacio suficiente y de que el juego se esté ejecutando en una carpeta con los permisos adecuados");
    }

    // Method to display an error message in case of an exception related to the dictionary (NotEnoughWordsException)
    public void dictionaryException(){
        JOptionPane.showMessageDialog(null,"No hay palabras válidas suficientes en el diccionario");
    }

    // Method to display an error message in case of an exception related to the graphical representation of the game
    public void drawException(){
        JOptionPane.showMessageDialog(null,"Ha ocurrido un error mientras se representaba el juego");
    }

}
