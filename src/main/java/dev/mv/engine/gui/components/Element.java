package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.animations.ElementAnimation;
import dev.mv.engine.gui.components.animations.ElementAnimator;
import dev.mv.engine.gui.event.*;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

import java.util.List;

public abstract class Element {
    protected Element parent;
    protected Window window;
    protected List<String> tags;
    protected String id;
    protected ElementAnimation.AnimationState animationState;
    protected ElementAnimation.AnimationState initialState;
    protected ElementAnimator animator;
    protected Theme theme;

    protected ClickListener clickListener = null;
    protected KeyListener keyListener = null;
    protected TextChangeListener textChangeListener = null;
    protected HoverListener hoverListener = null;
    protected ScrollListener scrollListener = null;

    protected Element(Window window, int x, int y, int width, int height, Element parent) {
        this.window = window;
        animationState = new ElementAnimation.AnimationState();
        animationState.baseColor = new Color(0, 0, 0, 0);
        animationState.outlineColor = new Color(0, 0, 0, 0);
        animationState.textColor = new Color(0, 0, 0, 0);
        initialState = new ElementAnimation.AnimationState();
        initialState.posX = x;
        initialState.posY = y;
        initialState.width = width;
        initialState.height = height;
        initialState.rotation = 0;
        initialState.originX = x + width / 2;
        initialState.originY = y + height / 2;
        initialState.baseColor = new Color(0, 0, 0, 0);
        initialState.outlineColor = new Color(0, 0, 0, 0);
        initialState.textColor = new Color(0, 0, 0, 0);
        initialState.copyValuesTo(animationState);

        animator = new ElementAnimator();
        animator.setOnFinish(() -> {
            initialState.copyValuesTo(animationState);
        });
    }

    public abstract void draw(DrawContext2D draw);

    public void checkAnimations() {
        if(!animator.isAnimating()) {
            initialState.copyValuesTo(animationState);
        }
        animator.setState(animationState);
        animator.loop(theme.getAnimationFrames());
        animationState = animator.getState();
    }

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
        return initialState.posX;
    }

    public void setX(int x) {
        initialState.posX = x;
        initialState.originX = x + initialState.width / 2;
    }

    public int getY() {
        return initialState.posY;
    }

    public void setY(int y) {
        initialState.posY = y;
        initialState.originY = y + initialState.height / 2;
    }

    public int getWidth() {
        return initialState.width;
    }

    public void setWidth(int width) {
        initialState.width = width;
        initialState.originX = initialState.posX + width / 2;
    }

    public int getHeight() {
        return initialState.height;
    }

    public void setHeight(int height) {
        initialState.height = height;
        initialState.originY = initialState.posY + height / 2;
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

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        this.animator.setAnimation(theme.getButtonAnimator());

        theme.getBaseColor().copyValuesTo(initialState.baseColor);
        theme.getText_base().copyValuesTo(initialState.textColor);
        if(theme.hasOutline()) {
            theme.getOutlineColor().copyValuesTo(initialState.outlineColor);
        }

        initialState.copyValuesTo(animationState);
    }
}
