package dev.mv.engine.exceptions;

import dev.mv.engine.MVEngine;
import dev.mv.engine.game.mod.loader.ModIntegration;
import dev.mv.utils.Utils;
import dev.mv.utils.function.FaultyRunnable;
import dev.mv.utils.logger.Logger;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Exceptions {

    private static String exceptionPath = "dev.mv.engine.exceptions";
    private static HashMap<String, LinkedException> messages = new HashMap<>();

    public static void readExceptionINI(InputStream stream) {
        Properties properties = new Properties();
        attempt(() -> properties.load(stream));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (((String)entry.getKey()).matches("\\[.*\\]")) continue;
            if (((String)entry.getKey()).startsWith("#")) continue;
            parse((String) entry.getKey(), (String) entry.getValue());
        }
    }

    private static void parse(String id, String message) {
        if (id.equals("~")) {
            exceptionPath = message;
            return;
        }

        String[] parts = message.split(";", 2);
        if (parts.length == 1) {
            messages.put(id, new LinkedException(RuntimeException.class, message));
        }
        String classPath = parts[0].trim().replaceFirst("~", exceptionPath.endsWith(".") ? exceptionPath : exceptionPath + ".");
        if (!classPath.contains(".")) {
            classPath = "java.lang." + classPath;
        }
        try {
            messages.put(id, new LinkedException((Class<? extends Throwable>) ModIntegration.loadClass(classPath), parts[1].trim()));
        } catch (Exception e) {
            send(new MalformedExceptionsFileException("The exceptions.ini file has an invalid class identifier at ID = \"" + id + "\"!"));
        }
    }

    public static void send(Throwable throwable) {
        Logger.error("Oops! It seems like there was an " + throwable.getClass().getCanonicalName() + "!");
        Logger.error(Utils.iter(throwable.getStackTrace()).map(StackTraceElement::toString).collect().unsafe().join((a, b) -> a + b, System.lineSeparator()));
        MVEngine.instance().getExceptionHandler().handle(throwable);
    }

    public static void send(String id, Object... args) {
        LinkedException linkedException = messages.get(id);
        if (linkedException != null) {
            send(linkedException.create(args));
        }
        else {
            send(new RuntimeException("Unknown exception id!"));
        }
    }

    public static <E extends Throwable> void attempt(FaultyRunnable<E> action) {
        try {
            action.run();
        } catch (Throwable e) {
            try {
                send((E) e);
            } catch (ClassCastException c) {
                send(c);
            }
        }
    }

    private static class LinkedException {

        private Class<? extends Throwable> clazz;
        private String message;

        public LinkedException(Class<? extends Throwable> clazz, String message) {
            this.clazz = clazz;
            this.message = message;
        }

        public Throwable create(Object... args) {
            try {
                return clazz.getConstructor(String.class).newInstance(String.format(message, args));
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                return new RuntimeException(String.format(message, args));
            }
        }
    }
}
