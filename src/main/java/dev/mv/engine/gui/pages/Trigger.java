package dev.mv.engine.gui.pages;

import dev.mv.engine.gui.components.Element;

public class Trigger {
    private String name;
    private Element listen;
    private Runnable action;

    public Trigger(String name, Element listen) {
        this.name = name;
        this.listen = listen;
    }

    public Trigger(String name) {
        this.name = name;
    }

    public Trigger() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Element getListen() {
        return listen;
    }

    public void setListen(Element listen) {
        this.listen = listen;
    }

    public void trigger() {
        if (action != null) action.run();
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}
