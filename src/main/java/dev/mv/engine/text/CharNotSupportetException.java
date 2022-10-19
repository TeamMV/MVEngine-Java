package dev.mv.engine.text;

public class CharNotSupportetException extends Exception {
    public CharNotSupportetException(char c) {
        super("character: " + c + ", keyCode: " + (c + 0));
    }
}
