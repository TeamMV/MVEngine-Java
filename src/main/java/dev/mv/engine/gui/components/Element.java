package dev.mv.engine.gui.components;

import dev.mv.engine.gui.event.*;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Element {
    protected int x, y, width , height;
    protected Element parent;
    protected Window window;
    protected List<String> tags;
    protected String id;

    protected ClickListener clickListener = null;
    protected KeyListener keyListener = null;
    protected TextChangeListener textChangeListener = null;
    protected HoverListener hoverListener = null;
    protected ScrollListener scrollListener = null;

    protected Element(Element parent) {
        this(-1, -1, parent);
    }

    protected Element(int x, int y) {
        this(x, y, null);
    }

    protected Element(int x, int y, Element parent) {
        this(x, y, 0, 0, parent);
    }

    protected Element(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    protected Element(int x, int y, int width, int height, Element parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    public abstract void draw(DrawContext2D draw, Theme theme);

    public abstract void attachListener(EventListener listener);
    
    public void addTag(String tag) {
        if(tags.contains(tag)) return;
        tags.add(tag);
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    public String[] getTags() {
        return tags.toArray(new String[0]);
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    //-----getters-----

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
