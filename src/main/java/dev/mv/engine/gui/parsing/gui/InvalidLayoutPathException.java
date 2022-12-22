package dev.mv.engine.gui.parsing.gui;

public class InvalidLayoutPathException extends Exception{
    public InvalidLayoutPathException() {
        super();
    }

    public InvalidLayoutPathException(String msg) {
        super(msg);
    }

    public InvalidLayoutPathException(Exception cause) {
        super(cause);
    }
}
