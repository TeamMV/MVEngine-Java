package dev.mv.engine.exceptions;

public class IllegalAudioFormatException extends Exception {
    public IllegalAudioFormatException() {
    }

    public IllegalAudioFormatException(String message) {
        super(message);
    }

    public IllegalAudioFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalAudioFormatException(Throwable cause) {
        super(cause);
    }

    public IllegalAudioFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
