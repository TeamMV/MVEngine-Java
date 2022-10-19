package dev.mv.engine.exception;

import java.util.function.Consumer;

public class ExceptionHandler {

    private static Consumer<Throwable> onThrow;

    public static void throwNew(Throwable tr) {
        if (onThrow == null) return;
        onThrow.accept(tr);
    }

    public static void setOnThrow(Consumer<Throwable> action) {
        onThrow = action;
    }
}
