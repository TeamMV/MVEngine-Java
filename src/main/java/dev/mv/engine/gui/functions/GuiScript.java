package dev.mv.engine.gui.functions;

import dev.mv.engine.MVEngine;

import javax.naming.ServiceUnavailableException;
import java.lang.reflect.Method;

public class GuiScript {
    private Language language;
    private String script;
    private Class<?> clazz;
    private Object instance;


    public GuiScript(Language language, String src) {
        try {
            this.language = language;
            if (language == Language.JAVA) {
                clazz = ClassLoader.getSystemClassLoader().loadClass(src);
                instance = clazz.getDeclaredConstructor().newInstance();
            } else {
                MVEngine.Exceptions.__throw__(new ServiceUnavailableException("The script language " + language + " is not supported for the guis yet!"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GuiMethod findMethod(String name, Class<?>[] paramTypes, String id) throws NoSuchMethodException {
        if (language == Language.JAVA) {
            Method method = clazz.getDeclaredMethod(name, paramTypes);
            if (!method.isAnnotationPresent(GuiFunction.class)) return null;
            String restrict = method.getAnnotation(GuiFunction.class).restricted();
            if (restrict.equals(id) || restrict.equals("any") || restrict.equals("none"))
                return new LinkedJavaMethod(method, instance);
        }
        return null;
    }
}
