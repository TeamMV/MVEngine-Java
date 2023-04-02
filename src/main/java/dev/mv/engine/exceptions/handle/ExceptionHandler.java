package dev.mv.engine.exceptions.handle;

public interface ExceptionHandler {
    void handle(Throwable throwable);

    final class Default implements ExceptionHandler {
        public static final Default INSTANCE = new Default();

        private Default() {
        }

        @Override
        public void handle(Throwable throwable) {
            if (throwable instanceof RuntimeException e) {
                throw e;
            }
            throw new RuntimeException(throwable);
        }
    }
}
