package dev.mv.engine.gui.parsing.gui;

public class ParsedClickMethod {
    String name;
    Class<?>[] types;
    Object[] params;
    boolean addSelf = false;
    int selfPos;
}
