package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class InputBox extends Element implements Toggle, Text, Clickable, Keyboard {
    protected String displayText = "", actualText = "";
    protected int cursorOffset = 0;
    protected int textShift = 0;
    protected BitmapFont font;
    protected boolean enabled = true;
    protected boolean selected = false;
    protected int limit = 10;
    protected List<Character> blacklist = new ArrayList<>();
    protected List<Character> allowedList = new ArrayList<>();

    public InputBox(Window window, Element parent, int width, int height) {
        super(window, -1, -1, width, height, parent);
    }

    public InputBox(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, width, height, parent);
    }

    public InputBox(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setBlacklist(List<Character> blacklist) {
        this.blacklist = blacklist;
    }

    public void setAllowedlist(List<Character> allowedList) {
        this.allowedList = allowedList;
    }

    public List<Character> getBlacklist() {
        return blacklist;
    }

    public List<Character> getAllowedList() {
        return allowedList;
    }

    @Override
    public void draw(DrawContext2D draw) {
        checkAnimations();


        if(theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            if(theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if(!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.roundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if(!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.roundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if(theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            if(theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if(!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.triangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if(!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.triangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if(theme.getEdgeStyle() == Theme.EdgeStyle.SQUARE) {
            if(theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if(!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if(!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.rectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
            }
        }

        if(theme.getText_base() != null) {
            draw.color(theme.getText_base());
        } else if(theme.getText_gradient() != null) {
            draw.color(theme.getText_gradient());
        }
        if(!enabled) {
            draw.color(theme.getDisabledTextColor());
        }
        draw.text(animationState.posX + textDistance(), animationState.posY + textDistance(), animationState.height - textDistance() * 2, displayText, font, animationState.rotation, animationState.originX, animationState.originY);

        if(selected) {
            draw.rectangle(animationState.posX + textDistance() + font.getWidth(displayText.substring(0, cursorOffset), getHeight() - textDistance() * 2), animationState.posY + textDistance(), 2, getHeight() - textDistance() * 2, animationState.rotation, animationState.originX, animationState.originY);
        }
    }

    protected int textDistance() {
        return getHeight() / 5;
    }

    @Override
    public void attachListener(EventListener listener) {
        if(listener instanceof TextChangeListener textChangeListener) {
            this.textChangeListener = textChangeListener;
        }
        if(listener instanceof ClickListener clickListener) {
            this.clickListener = clickListener;
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
        if(!enabled) return;
        if(GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height)) return;
        else {
            selected = false;
            animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        }
        if(clickListener != null) {
            clickListener.onCLick(this, btn);
        }
        if(!selected) {
            animator.animate(theme.getAnimationInTime(), theme.getAnimationFrames());
            selected = true;
        }}

    @Override
    public void clickRelease(int x, int y, int btn) {
        if(!enabled) return;
        selected = false;
        animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        if(GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height)) {
            return;
        }
        selected = true;
        if(clickListener != null) {
            clickListener.onRelease(this, btn);
        }
    }

    @Override
    public void keyPress(int key) {

    }

    @Override
    public void keyType(int key) {
        if(selected) {
            if (Input.convertKey(key) == Input.KEY_BACKSPACE) {
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

            if(Input.convertKey(key) == Input.KEY_DELETE) {
                moveCursor(1);
                pop(1);
                return;
            }

            if(Input.convertKey(key) == Input.KEY_SPACE) {
                push(' ');
                return;
            }

            if(!Utils.isCharAscii((char) key)) {
                return;
            }

            push((char) key);
        }
    }

    @Override
    public void keyRelease(int key) {

    }

    @Override
    public void setFont(BitmapFont font) {
        this.font = font;
    }

    @Override
    public BitmapFont getFont(BitmapFont font) {
        return font;
    }

    @Override
    public void setText(String text) {
        actualText = text;
    }

    @Override
    public String getText() {
        return actualText;
    }

    protected void moveCursor(int amount) {
        cursorOffset += amount;

        if(cursorOffset == 0) {
            return;
        }

        if(cursorOffset < 0) {
            cursorOffset++;
            if(textShift > 0) {
                shiftText(-1);
            }
            return;
        }
        if(cursorOffset > displayText.length()) {
            cursorOffset--;
            return;
        }
        if(textDistance() + font.getWidth(displayText.substring(0, cursorOffset), getHeight() - textDistance() * 2) > getWidth() - textDistance()) {
            cursorOffset--;
            shiftText(1);
        }
    }

    protected void shiftText(int amount) {
        textShift += amount;
        displayText = actualText.substring(textShift, textShift + font.possibleAmountOfChars(actualText.substring(textShift), getWidth() - textDistance() * 2, getHeight() - textDistance() * 2));
        System.out.println(getWidth() - textDistance() * 2);
    }

    private void push(String s) {
        for(char c : s.toCharArray()) {
            push(c);
        }
    }

    private void push(char c) {
        if(displayText.length() >= limit) return;
        if(blacklist.contains(c)) return;
        if(!allowedList.isEmpty()) if(!allowedList.contains(c)) return;
        if(actualText.length() + 1 <= limit) {
            StringBuilder sb = new StringBuilder(actualText);
            sb.insert(cursorOffset + textShift, c);
            actualText = sb.toString();
            shiftText(0);
            moveCursor(1);
        }
    }

    private void pop(int amount) {
        for (int i = 0; i < amount; i++) {
            pop();
        }
    }

    private void pop() {
        if(actualText.length() > 0) {
            try {
                StringBuilder sb = new StringBuilder(actualText);
                sb.deleteCharAt(cursorOffset + textShift - 1);
                actualText = sb.toString();
                if (textShift > 0) {
                    shiftText(-1);
                } else {
                    shiftText(0);
                    moveCursor(-1);
                }
            } catch (IndexOutOfBoundsException e) {}
        }
    }
}
