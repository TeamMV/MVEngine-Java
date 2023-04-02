package dev.mv.engine.game.event;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MethodList {

    private Map<Priority, LinkedMethod[]> methods = new HashMap<>();

    MethodList() {
    }

    public void add(LinkedMethod method, Priority priority) {
        LinkedMethod[] m = methods.get(priority);
        if (m == null || m.length == 0) {
            m = new LinkedMethod[1];
            m[0] = method;
            methods.put(priority, m);
            return;
        }
        m = Arrays.copyOf(m, m.length + 1);
        m[m.length - 1] = method;
        methods.put(priority, m);
    }

    @NotNull
    public LinkedMethod[] getMethods(Priority priority) {
        LinkedMethod[] m = methods.get(priority);
        return m == null ? new LinkedMethod[0] : m;
    }

}
