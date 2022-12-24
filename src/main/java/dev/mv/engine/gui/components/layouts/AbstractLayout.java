package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractLayout extends Element implements Clickable, Draggable, Keyboard, Scrollable{
    protected List<Element> elements;
    protected int maxWidth, maxHeight;
    protected int spacing = 0;

    protected AbstractLayout(Window window, Element parent) {
        super(window, -1, -1, -1, -1, parent);
        elements = new ArrayList<>();
    }

    protected AbstractLayout(Window window, int width, int height, Element parent) {
        super(window, -1, -1, width, height, parent);
        elements = new ArrayList<>();
    }

    protected AbstractLayout(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
        elements = new ArrayList<>();
    }

    @Override
    public void attachListener(EventListener listener) {

    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    protected void measureMaxSize() {
        for(Element e : elements) {
            maxWidth = Math.max(maxWidth, e.getWidth());
            maxHeight = Math.max(maxHeight, e.getHeight());
        }
    }

    public void addElement(Element e) {
        elements.add(e);
        measureMaxSize();
        e.setParent(this);
    }

    public void addElements(Element[] e) {
        elements.addAll(Arrays.asList(e));
        measureMaxSize();
        for(Element element : e) {
            element.setParent(this);
        }
    }

    public void removeElement(Element e) {
        elements.remove(e);
        measureMaxSize();
        e.setParent(null);
    }

    public void removeElements(Element[] e) {
        elements.removeAll(Arrays.asList(e));
        measureMaxSize();
        for(Element element : e) {
            element.setParent(null);
        }
    }

    public void removeElements(Predicate<? super Element> predicate) {
        for(Element element : elements) {
            if(predicate.test(element)) {
                elements.remove(element);
                element.setParent(null);
            }
        }
        measureMaxSize();
    }

    public Element[] elements() {
        return elements.toArray(new Element[0]);
    }

    @Override
    public void click(int x, int y, int btn) {
        for(Element element : elements) {
            if(element instanceof Clickable clickable) {
                clickable.click(x, y, btn);
            }
        }
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        for(Element element : elements) {
            if(element instanceof Clickable clickable) {
                clickable.clickRelease(x, y, btn);
            }
        }
    }

    @Override
    public void dragBegin(int x, int y, int btn) {
        for(Element element : elements) {
            if(element instanceof Draggable draggable) {
                draggable.dragBegin(x, y, btn);
            }
        }
    }

    @Override
    public void drag(int x, int y, int btn) {
        for(Element element : elements) {
            if(element instanceof Draggable draggable) {
                draggable.drag(x, y, btn);
            }
        }
    }

    @Override
    public void dragLeave(int x, int y, int btn) {
        for(Element element : elements) {
            if(element instanceof Draggable draggable) {
                draggable.dragLeave(x, y, btn);
            }
        }
    }

    @Override
    public void keyPress(int key) {
        for(Element element : elements) {
            if(element instanceof Keyboard keyboard) {
                keyboard.keyPress(key);
            }
        }
    }

    @Override
    public void keyType(int key) {
        for(Element element : elements) {
            if(element instanceof Keyboard keyboard) {
                keyboard.keyType(key);
            }
        }
    }

    @Override
    public void keyRelease(int key) {
        for(Element element : elements) {
            if(element instanceof Keyboard keyboard) {
                keyboard.keyRelease(key);
            }
        }
    }

    @Override
    public void scrollX(int amount) {
        for(Element element : elements) {
            if(element instanceof Scrollable scrollable) {
                scrollable.scrollX(amount);
            }
        }
    }

    @Override
    public void scrollY(int amount) {
        for(Element element : elements) {
            if(element instanceof Scrollable scrollable) {
                scrollable.scrollY(amount);
            }
        }
    }

    @Override
    public void setTheme(Theme theme) {
        for (Element element : elements) {
            element.setTheme(theme);
        }
    }
}
