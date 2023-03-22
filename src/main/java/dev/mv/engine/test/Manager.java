package dev.mv.engine.test;

import dev.mv.engine.gui.functions.GuiFunction;

public class Manager {

    @GuiFunction
    public void hello() {
        System.out.println("Hello World!");
    }

    @GuiFunction
    public void exit(int code) {
        System.exit(code);
    }

}