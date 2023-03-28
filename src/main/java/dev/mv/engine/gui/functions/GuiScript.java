package dev.mv.engine.gui.functions;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.game.mod.loader.ModIntegration;

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
                clazz = ModIntegration.loadClass(src);
                instance = clazz.getDeclaredConstructor().newInstance();
            } else {
                Exceptions.send(new ServiceUnavailableException("The script language " + language + " is not supported for the guis yet!"));
            }
        } catch (Exception e) {
            Exceptions.send("GUI_SCRIPT_LOAD", src);
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
