package dev.mv.engine.gui;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.input.*;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Gui {
    private DrawContext2D drawContext;
    private Theme theme = null;
    private String name;
    private List<Element> elements;

    public Gui(@NonNull DrawContext2D drawContext, String name) {
        this(drawContext, null, name);
    }

    public Gui(@NonNull DrawContext2D drawContext, Theme theme, String name) {
        this.drawContext = drawContext;
        this.theme = theme;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addElement(Element e) {
        elements.add(e);
    }

    public void addElements(Element[] e) {
        elements.addAll(Arrays.asList(e));
    }

    public void removeElement(Element e) {
        elements.remove(e);
    }

    public void removeElements(Element[] e) {
        elements.removeAll(Arrays.asList(e));
    }

    public void removeElements(Predicate<? super Element> predicate) {
        elements.removeIf(predicate);
    }

    public Element[] elements() {
        return elements.toArray(new Element[0]);
    }

    public void drawAll() {
        for(Element e : elements) {
            e.draw(drawContext, theme);
        }
    }

    public void drawSpecific(Predicate<? super Element> predicate) {
        for(Element e : elements) {
            if(predicate.test(e)) {
                e.draw(drawContext, theme);
            }
        }
    }

    //----- event calls -----

    public void loop() {
        //keys
        for(int i = 0; i < Input.keys.length; i++) {
            if(Input.keys[i] == Input.State.ONPRESSED) {
                keyType(i);
            } else if(Input.keys[i] == Input.State.PRESSED) {
                keyPress(i);
            } else if(Input.keys[i] == Input.State.ONRELEASED) {
                keyRelease(i);
            }
        }

        //mouseButtons
        for(int i = 0; i < Input.buttons.length; i++) {
            if(Input.buttons[i] == Input.State.ONPRESSED) {
                click(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
                dragBegin(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
            } else if(Input.buttons[i] == Input.State.ONRELEASED) {
                release(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
                dragEnd(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
            } else if(Input.buttons[i] == Input.State.PRESSED) {
                drag(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
            }
        }

        //scroll
        if(Input.mouse[Input.MOUSE_SCROLL_X] != 0.0) {
            scrollX(Input.mouse[Input.MOUSE_SCROLL_X]);
        }
        if(Input.mouse[Input.MOUSE_SCROLL_Y] != 0.0) {
            scrollY(Input.mouse[Input.MOUSE_SCROLL_Y]);
        }
    }

    private void click(int x, int y, int btn) {
        for(Element e : elements) {
            if(e instanceof Clickable clickable) {
                clickable.click(x, y, btn);
            }
        }
    }

    private void release(int x, int y, int btn) {
        for(Element e : elements) {
            if(e instanceof Clickable clickable) {
                clickable.clickRelease(x, y, btn);
            }
        }
    }

    private void dragBegin(int x, int y, int btn) {
        for(Element e : elements) {
            if(e instanceof Draggable draggable) {
                draggable.dragBegin(x, y, btn);
            }
        }
    }

    private void drag(int x, int y, int btn) {
        for(Element e : elements) {
            if(e instanceof Draggable draggable) {
                draggable.drag(x, y, btn);
            }
        }
    }

    private void dragEnd(int x, int y, int btn) {
        for(Element e : elements) {
            if(e instanceof Draggable draggable) {
                draggable.dragLeave(x, y, btn);
            }
        }
    }

    private void keyPress(int key) {
        for(Element e : elements) {
            if(e instanceof Keyboard keyboard) {
                keyboard.keyPress(key);
            }
        }
    }

    private void keyType(int key) {
        for(Element e : elements) {
            if(e instanceof Keyboard keyboard) {
                keyboard.keyType(key);
            }
        }
    }

    private void keyRelease(int key) {
        for(Element e : elements) {
            if(e instanceof Keyboard keyboard) {
                keyboard.keyRelease(key);
            }
        }
    }

    private void scrollX(int amt) {
        for(Element e : elements) {
            if(e instanceof Scrollable scrollable) {
                scrollable.scrollX(amt);
            }
        }
    }

    private void scrollY(int amt) {
        for(Element e : elements) {
            if(e instanceof Scrollable scrollable) {
                scrollable.scrollY(amt);
            }
        }
    }
}
