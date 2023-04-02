package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class PasswordInputBox extends InputBox {
    private boolean isHidden = false;
    private boolean hiding = false;
    private String realText = "";
    private ImageButton visibilityButton;

    public PasswordInputBox(Window window, Element parent, int width, int height) {
        super(window, parent, width - height - 5, height);
        prepareButton(window, -1, -1, width, height);
    }

    public PasswordInputBox(Window window, Element parent, int x, int y, int width, int height) {
        super(window, x, y, parent, width - height - 5, height);
        prepareButton(window, x, y, width, height);
    }

    public PasswordInputBox(Window window, int x, int y, int width, int height) {
        super(window, x, y, width - height - 5, height);
        prepareButton(window, x, y, width, height);
    }

    public PasswordInputBox(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
        prepareButton(window, position.getX(), position.getY(), position.getWidth(), position.getHeight());
    }

    private void prepareButton(Window window, int x, int y, int width, int height) {
        visibilityButton = new ImageButton(window, x + width - height, y, this, height, height);
        visibilityButton.attachListener(new ClickListener() {
            @Override
            public void onCLick(Element element, int button) {

            }

            @Override
            public void onRelease(Element element, int button) {
                if (button == Input.BUTTON_LEFT) {
                    toggleVisibility();
                }
            }
        });
    }

    @Override
    public void setText(String text) {
        hiding = true;
        realText = text;
        if (isHidden) {
            super.setText("*".repeat(text.length()));
        } else {
            super.setText(text);
        }
        hiding = false;
    }

    private void setPrivateText(String text) {
        hiding = true;
        int oldCursor = cursorOffset;
        int oldShift = textShift;
        super.setText(text);
        while (cursorOffset + textShift < oldCursor + oldShift) {
            moveCursor(1);
        }
        while (cursorOffset + textShift > oldCursor + oldShift) {
            moveCursor(-1);
        }
        hiding = false;
    }

    public void show() {
        isHidden = false;
        setPrivateText(realText);
        visibilityButton.setTexture(GuiAssets.EYE_CLOSED);
    }

    public void hide() {
        isHidden = true;
        setPrivateText("*".repeat(realText.length()));
        visibilityButton.setTexture(GuiAssets.EYE_OPEN);
    }

    public void toggleVisibility() {
        isHidden = !isHidden;
        if (isHidden) hide();
        else show();
    }

    @Override
    public void disable() {
        enabled = false;
        visibilityButton.disable();
    }

    @Override
    public void enable() {
        enabled = true;
        visibilityButton.enable();
    }

    @Override
    public void toggle() {
        enabled = !enabled;
        visibilityButton.toggle();
    }

    @Override
    public void setX(int x) {
        initialState.posX = x;
        initialState.originX = x + initialState.width / 2;
        visibilityButton.setX(x + getWidth() - getHeight());
    }

    @Override
    public void setY(int y) {
        initialState.posY = y;
        initialState.originY = y + initialState.height / 2;
        visibilityButton.setY(y);
    }

    @Override
    public void setHeight(int height) {
        initialState.height = height;
        initialState.originY = initialState.posY + height / 2;
        visibilityButton.setHeight(height);
        visibilityButton.setWidth(height);
        visibilityButton.setX(getX() + getWidth() - height + 5);
    }

    @Override
    public int getWidth() {
        return initialState.width + 5 + visibilityButton.getWidth();
    }

    @Override
    public void setWidth(int width) {
        initialState.width = width;
        initialState.originX = initialState.posX + width / 2;
        visibilityButton.setX(getX() + width - getHeight() + 5);
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

        visibilityButton.draw(draw);
    }

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);

        visibilityButton.setTheme(theme);
        visibilityButton.setTexture(GuiAssets.EYE_CLOSED);
        visibilityButton.setUseTextColor(theme.isShouldPasswordInputBoxButtonUseTextColor());

        setFont(theme.getFont());
    }

    @Override
    public void click(int x, int y, int btn) {
        if (!enabled) return;
        visibilityButton.click(x, y, btn);
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height, theme))
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
        visibilityButton.clickRelease(x, y, btn);
        animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height, theme)) {
            return;
        }
        selected = true;
        if (!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onRelease(this, btn));
        }
    }

    @Override
    protected void push(char c) {
        if (hiding) {
            super.push(c);
            return;
        }
        if (displayText.length() >= limit) return;
        if (blacklist.contains(c)) return;
        if (!allowedList.isEmpty()) if (!allowedList.contains(c)) return;
        if (realText.length() + 1 <= limit) {
            StringBuilder sb = new StringBuilder(realText);
            sb.insert(cursorOffset + textShift, c);
            realText = sb.toString();
            actualText = isHidden ? "*".repeat(realText.length()) : realText;
            int len = displayText.length();
            shiftText(0);
            if (displayText.length() <= len) {
                cursorOffset -= len - displayText.length() + 1;
                shiftText(len - displayText.length() + 1);
            }
            moveCursor(1);
        }
    }

    @Override
    protected void pop() {
        if (hiding) {
            super.pop();
            return;
        }
        if (actualText.length() > 0) {
            try {
                StringBuilder sb = new StringBuilder(realText);
                sb.deleteCharAt(cursorOffset + textShift - 1);
                realText = sb.toString();
                actualText = isHidden ? "*".repeat(realText.length()) : realText;
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
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }

    @Override
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
        if (textDistance() + font.getWidth(displayText.substring(0, cursorOffset), getHeight() - textDistance() * 2) > getWidth() - getHeight() - 5 - textDistance()) {
            cursorOffset--;
            shiftText(1);
        }
    }

    @Override
    protected void shiftText(int amount) {
        textShift += amount;
        displayText = actualText.substring(textShift, textShift + font.possibleAmountOfChars(actualText.substring(textShift), getWidth() - getHeight() - 5 - textDistance() * 2, getHeight() - textDistance() * 2));
    }
}
