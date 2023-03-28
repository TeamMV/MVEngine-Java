package dev.mv.engine.gui.components;

import dev.mv.engine.Loopable;
import dev.mv.engine.MVEngine;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.utils.ClipBoardListener;
import dev.mv.utils.Utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputBox extends Element implements Toggle, Text, Clickable, Keyboard {
    protected String displayText = "", actualText = "", placeholderText = "";
    protected int cursorOffset = 0;
    protected int textShift = 0;
    protected BitmapFont font;
    protected boolean enabled = true;
    protected boolean selected = false;
    protected int limit = Integer.MAX_VALUE;
    protected List<Character> blacklist = new ArrayList<>();
    protected List<Character> allowedList = new ArrayList<>();
    protected boolean chroma;

    public InputBox(Window window, Element parent, int width, int height) {
        super(window, -1, -1, width, height, parent);
    }

    public InputBox(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, width, height, parent);
    }

    public InputBox(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    public InputBox(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        if (limit == -1) this.limit = Integer.MAX_VALUE;
    }

    public void setAllowedlist(List<Character> allowedList) {
        this.allowedList = allowedList;
    }

    public List<Character> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Character> blacklist) {
        this.blacklist = blacklist;
    }

    public List<Character> getAllowedList() {
        return allowedList;
    }

    @Override
    public void draw(DrawContext2D draw) {
        checkAnimations();


        if (theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidRoundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius() + thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.roundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidTriangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius() + thickness, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.triangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.SQUARE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.rectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
            }
        }

        if (theme.getText_base() != null) {
            draw.color(theme.getText_base());
        } else if (theme.getText_gradient() != null) {
            draw.color(theme.getText_gradient());
        }
        if (!enabled) {
            draw.color(theme.getDisabledTextColor());
        }
        if (displayText.isEmpty() && !selected) {
            draw.text(chroma, animationState.posX + textDistance(), animationState.posY + textDistance(), animationState.height - textDistance() * 2, placeholderText.substring(0, font.possibleAmountOfChars(placeholderText, animationState.width - textDistance() * 2, animationState.height - textDistance() * 2)), font, animationState.rotation, animationState.originX, animationState.originY);
        } else {
            draw.text(chroma, animationState.posX + textDistance(), animationState.posY + textDistance(), animationState.height - textDistance() * 2, displayText, font, animationState.rotation, animationState.originX, animationState.originY);
        }
        if (selected) {
            draw.rectangle(animationState.posX + textDistance() + font.getWidth(displayText.substring(0, cursorOffset), getHeight() - textDistance() * 2), animationState.posY + textDistance(), 2, getHeight() - textDistance() * 2, animationState.rotation, animationState.originX, animationState.originY);
        }
    }

    protected int textDistance() {
        return getHeight() / 5;
    }

    @Override
    public void attachListener(EventListener listener) {
        if (listener instanceof TextChangeListener textChangeListener) {
            this.textChangeListeners.add(textChangeListener);
        }
        if (listener instanceof ClickListener clickListener) {
            this.clickListeners.add(clickListener);
        }

    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void click(int x, int y, int btn) {
        if (!enabled) return;
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height))
            return;
        else {
            selected = false;
            animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        }
        if (!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onCLick(this, btn));
        }
        if (!selected) {
            animator.animate(theme.getAnimationInTime(), theme.getAnimationFrames());
            selected = true;
        }
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        if (!enabled) return;
        selected = false;
        animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height)) {
            return;
        }
        selected = true;
        if (!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onRelease(this, btn));
        }
    }

    @Override
    public void keyPress(int key) {
        if (selected) {
            if (Input.convertKey(key) == Input.KEY_V && Input.isControl() && Input.keys[Input.KEY_V] == Input.State.ONPRESSED) {
                Utils.async(() -> {
                    try {
                        setText(getText() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                    } catch (UnsupportedFlavorException | IOException e) {
                        Exceptions.send(e);
                    }
                });
                //setText(getText() + ClipBoardListener.getClipboardData());
            }
        }
    }

    @Override
    public void keyType(int key) {
        if (selected) {
            if (Input.convertKey(key) == Input.KEY_DELETE || Input.convertKey(key) == Input.KEY_BACKSPACE) {
                pop();
                return;
            }

            if (Input.convertKey(key) == Input.KEY_ARROW_LEFT) {
                moveCursor(-1);
                return;
            }

            if (Input.convertKey(key) == Input.KEY_ARROW_RIGHT) {
                moveCursor(1);
                return;
            }

            /*if(!Utils.isCharAscii((char) key)) {
                return;
            }*/

            push((char) key);
        }
    }

    @Override
    public void keyRelease(int key) {

    }

    @Override
    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    @Override
    public String getText() {
        return actualText;
    }

    @Override
    public void setText(String text) {
        clearText();
        for (char c : text.toCharArray()) {
            push(c);
        }
    }

    public void clearText() {
        actualText = "";
        displayText = "";
        textShift = 0;
        cursorOffset = 0;
    }

    @Override
    public void setUseChroma(boolean chroma) {
        this.chroma = chroma;
    }

    protected void moveCursor(int amount) {
         cursorOffset += amount;

        if (cursorOffset == 0) {
            return;
        }

        if (cursorOffset < 0) {
            cursorOffset++;
            if (textShift > 0) {
                shiftText(-1);
            }
            return;
        }
        if (cursorOffset > displayText.length()) {
            cursorOffset--;
            return;
        }
        if (textDistance() + font.getWidth(displayText.substring(0, cursorOffset), getHeight() - textDistance() * 2) > getWidth() - textDistance()) {
            cursorOffset--;
            shiftText(1);
        }
    }

    protected void shiftText(int amount) {
        textShift += amount;
        displayText = actualText.substring(textShift, textShift + font.possibleAmountOfChars(actualText.substring(textShift), getWidth() - textDistance() * 2, getHeight() - textDistance() * 2));
    }

    protected void push(String s) {
        for (char c : s.toCharArray()) {
            push(c);
        }
    }

    protected void push(char c) {
        if (displayText.length() >= limit) return;
        if (blacklist.contains(c)) return;
        if (!allowedList.isEmpty()) if (!allowedList.contains(c)) return;
        if (actualText.length() + 1 <= limit) {
            StringBuilder sb = new StringBuilder(actualText);
            sb.insert(cursorOffset + textShift, c);
            actualText = sb.toString();
            int len = displayText.length();
            shiftText(0);
            if (displayText.length() <= len) {
                cursorOffset -= len - displayText.length() + 1;
                shiftText(len - displayText.length() + 1);
            }
            moveCursor(1);
        }
    }

    private void pop(int amount) {
        for (int i = 0; i < amount; i++) {
            pop();
        }
    }

    protected void pop() {
        if (actualText.length() > 0) {
            try {
                StringBuilder sb = new StringBuilder(actualText);
                sb.deleteCharAt(cursorOffset + textShift - 1);
                actualText = sb.toString();
                if (textShift > 0) {
                    int len = displayText.length();
                    shiftText(-1);
                    if (len > displayText.length()) {
                        shiftText(1);
                        cursorOffset--;
                    }
                    while (textShift > 0) {
                        len = displayText.length();
                        shiftText(-1);
                        cursorOffset++;
                        if (len >= displayText.length()) {
                            shiftText(1);
                            cursorOffset--;
                            break;
                        }
                    }
                } else {
                    shiftText(0);
                    moveCursor(-1);
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }
}
