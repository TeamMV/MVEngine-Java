package dev.mv.editor.loading;

import dev.mv.utils.async.PromiseNull;
import lombok.Getter;

import static dev.mv.utils.Utils.*;

public class LoadingManager {

    @Getter
    private static boolean started = false;
    private static LoadingScreen screen = null;
    private static PromiseNull loading = null;
    private static PromiseNull dots = null;

    public static void start() {
        if (started) return;
        started = true;
        screen = new LoadingScreen();
        loading = new PromiseNull(screen::run);
    }

    public static void start(String message) {
        if (started) return;
        started = true;
        screen = new LoadingScreen(message);
        loading = new PromiseNull(screen::run);
    }

    public static void start(String message, String filename) {
        if (started) return;
        started = true;
        screen = new LoadingScreen(message, filename);
        loading = new PromiseNull(screen::run);
    }

    public static void stop() {
        if (!started) return;
        started = false;
        screen.stop();
        ifNotNull(loading).then(promise -> await(promise));
        ifNotNull(dots).then(promise -> await(promise));
        loading = null;
        dots = null;
        screen = null;
    }

    public static void setMessage(String message) {
        if (!started) return;
        screen.setMessage(message);
    }

    public static void loadingDots() {
        loadingDots(400);
    }

    public static void loadingDots(int timeout) {
        if (!started) return;
        dots = new PromiseNull(() -> {
            int dots = 1;
            while (started) {
                String message = "Loading";
                for (int i = 0; i < dots; i++) {
                    message += ".";
                }
                screen.setMessage(message);
                await(sleep(timeout));
                if (dots < 3) dots++;
                else dots = 1;
            }
        });
    }

}
