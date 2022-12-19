package dev.mv.engine.gui.components;

import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.KeyListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Render2D;
import dev.mv.engine.render.shared.Window;

import java.util.HashMap;
import java.util.Map;

public abstract class Element {
    protected int x, y, width , height;
    protected Element parent;
    protected Window window;

    private Map<Integer, Boolean> keysPressed;

    protected ClickListener clickListener = null;
    protected KeyListener keyListener = null;
    protected TextChangeListener textChangeListener = null;

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
        this.keysPressed = new HashMap<>();
    }

    abstract void draw(DrawContext2D draw, Theme theme);

    public abstract void attachListener(EventListener listener);

    //-----events-----

    void click(int mx, int my, int btn, int add) {
        if(clickListener == null) return;
        if(GuiUtils.mouseNotInside(mx, my, x, y, width, height)) return;
        clickListener.onCLick(this, btn, add);
    }

    void releaseMouse(int mx, int my, int btn, int add) {
        if(clickListener == null) return;
        if(GuiUtils.mouseNotInside(mx, my, x, y, width, height)) return;
        clickListener.onRelease(this, btn, add);
    }

    void keyPress(int keyCode, char keyChar, int add) {
        if(keyListener == null) return;
        keyListener.onPress(keyCode, keyChar, add);
        if(!keysPressed.get(keyCode)) {
            keyListener.onType(keyCode, keyChar, add);
            keysPressed.put(keyCode, true);
        }
    }

    void keyRelease(int keyCode, char keyChar, int add) {
        if(keyListener == null) return;
        keyListener.onRelease(keyCode, keyChar, add);
        keysPressed.put(keyCode, false);
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
