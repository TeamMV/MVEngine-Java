package dev.mv.engine.gui.parsing.page;

public class UnsupportedEventException extends Exception {
    public UnsupportedEventException() {
        super();
    }

    public UnsupportedEventException(String msg) {
        super(msg);
    }

    public UnsupportedEventException(Throwable cause) {
        super(cause);
    }
}
