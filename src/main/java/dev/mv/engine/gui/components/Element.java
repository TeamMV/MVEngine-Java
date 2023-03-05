package dev.mv.engine.gui.components;

import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.components.animations.ElementAnimation;
import dev.mv.engine.gui.components.animations.ElementAnimator;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.event.*;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    protected Element parent;
    protected Window window;
    protected List<String> tags;
    protected String id = "";
    protected VariablePosition position;
    protected ElementAnimation.AnimationState animationState;
    protected ElementAnimation.AnimationState initialState;
    protected ElementAnimator animator;
    protected Theme theme;
    protected Gui gui;

    protected List<ClickListener> clickListeners;
    protected List<KeyListener> keyListeners;
    protected List<TextChangeListener> textChangeListeners;
    protected List<ScrollListener> scrollListeners;
    protected List<ProgressListener> progressListeners;

    protected Element(Window window, int x, int y, int width, int height, Element parent) {
        this.window = window;
        this.parent = parent;
        animationState = new ElementAnimation.AnimationState();
        animationState.baseColor = new Color(0, 0, 0, 0);
        animationState.outlineColor = new Color(0, 0, 0, 0);
        animationState.textColor = new Color(0, 0, 0, 0);
        animationState.extraColor = new Color(0, 0, 0, 0);
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
        initialState.extraColor = new Color(0, 0, 0, 0);
        initialState.copyValuesTo(animationState);

        animator = new ElementAnimator(this);
        animator.setOnFinish(() -> {
            initialState.copyValuesTo(animationState);
        });

        clickListeners = new ArrayList<>();
        keyListeners = new ArrayList<>();
        textChangeListeners = new ArrayList<>();
        scrollListeners = new ArrayList<>();
        progressListeners = new ArrayList<>();
    }

    protected Element(Window window, VariablePosition position, Element parent) {
        this.position = position;
        this.window = window;
        animationState = new ElementAnimation.AnimationState();
        animationState.baseColor = new Color(0, 0, 0, 0);
        animationState.outlineColor = new Color(0, 0, 0, 0);
        animationState.textColor = new Color(0, 0, 0, 0);
        animationState.extraColor = new Color(0, 0, 0, 0);
        initialState = new ElementAnimation.AnimationState();
        initialState.posX = position.getX();
        initialState.posY = position.getY();
        initialState.width = position.getWidth();
        initialState.height = position.getHeight();
        initialState.rotation = 0;
        initialState.originX = position.getX() + position.getWidth() / 2;
        initialState.originY = position.getY() + position.getHeight() / 2;
        initialState.baseColor = new Color(0, 0, 0, 0);
        initialState.outlineColor = new Color(0, 0, 0, 0);
        initialState.textColor = new Color(0, 0, 0, 0);
        initialState.extraColor = new Color(0, 0, 0, 0);
        initialState.copyValuesTo(animationState);

        animator = new ElementAnimator(this);
        animator.setOnFinish(() -> {
            initialState.copyValuesTo(animationState);
        });

        clickListeners = new ArrayList<>();
        keyListeners = new ArrayList<>();
        textChangeListeners = new ArrayList<>();
        scrollListeners = new ArrayList<>();
        progressListeners = new ArrayList<>();
    }

    public ElementAnimation.AnimationState getInitialState() {
        return initialState;
    }

    public abstract void draw(DrawContext2D draw);

    public void checkAnimations() {
        if (!animator.isAnimating()) {
            initialState.copyValuesTo(animationState);
        }
        animator.setState(animationState);
        animator.loop(theme.getAnimationFrames());
        animationState = animator.getState();
    }

    public abstract void attachListener(EventListener listener);

    public void addTag(String tag) {
        if (tags == null) tags = new ArrayList<>();
        if (tags.contains(tag)) return;
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public String[] getTags() {
        return tags != null ? tags.toArray(new String[0]) : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void resize(int width, int height) {
        if (position != null) {
            position.resize(width, height);
            setX(position.getX());
            setY(position.getY());
            setWidth(position.getWidth());
            setHeight(position.getHeight());
        }
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
        if (theme.hasOutline()) {
            theme.getOutlineColor().copyValuesTo(initialState.outlineColor);
        }
        theme.getExtraColor().copyValuesTo(initialState.extraColor);

        initialState.copyValuesTo(animationState);

        if (this instanceof Text textInstance) {
            textInstance.setFont(theme.getFont());
        }
    }

    public ElementAnimator getAnimator() {
        return animator;
    }

    public void setAnimator(ElementAnimator animator) {
        this.animator = animator;
    }

    public Color getBaseColor() {
        return initialState.baseColor;
    }

    public void setBaseColor(Color color) {
        initialState.baseColor = color;
        animationState.baseColor = color;
    }

    public Color getOutlineColor() {
        return initialState.outlineColor;
    }

    public void setOutlineColor(Color color) {
        initialState.outlineColor = color;
        animationState.outlineColor = color;
    }

    public Color getTextColor() {
        return initialState.textColor;
    }

    public void setTextColor(Color color) {
        initialState.textColor = color;
        animationState.textColor = color;
    }

    public Color getExtraColor() {
        return initialState.extraColor;
    }

    public void setExtraColor(Color color) {
        initialState.extraColor = color;
        animationState.extraColor = color;
    }

    public Gui getGui() {
        return gui;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }
}
