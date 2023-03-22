package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.components.AbstractClickable;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.Window;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractLayout extends Element implements Clickable, Draggable, Keyboard, Scrollable, Iterable<Element> {
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

    protected AbstractLayout(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
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
        maxHeight = 0;
        maxWidth = 0;
        for (Element e : elements) {
            maxWidth = Math.max(maxWidth, e.getWidth());
            maxHeight = Math.max(maxHeight, e.getHeight());
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        for (Element e : elements) {
            e.resize(width, height);
        }
        measureMaxSize();
    }

    public void addElement(Element e) {
        elements.add(e);
        measureMaxSize();
        e.setParent(this);
        if (gui != null) {
            e.setGui(gui);
            if (e instanceof AbstractClickable c) {
                c.findClickMethod();
            }
        }
    }

    public void addElements(Element[] e) {
        elements.addAll(Arrays.asList(e));
        measureMaxSize();
        for (Element element : e) {
            element.setParent(this);
            if (gui != null) {
                element.setGui(gui);
                if (element instanceof AbstractClickable c) {
                    c.findClickMethod();
                }
            }
        }
    }

    public void removeElement(Element e) {
        elements.remove(e);
        measureMaxSize();
        e.setParent(null);
        e.setGui(null);
    }

    public void removeElements(Element[] e) {
        elements.removeAll(Arrays.asList(e));
        measureMaxSize();
        for (Element element : e) {
            element.setParent(null);
            element.setGui(null);
        }
    }

    public void removeElements(Predicate<? super Element> predicate) {
        for (Element element : elements) {
            if (predicate.test(element)) {
                elements.remove(element);
                element.setParent(null);
                element.setGui(null);
            }
        }
        measureMaxSize();
    }

    public Element[] elements() {
        return elements.toArray(new Element[0]);
    }

    @Override
    public void setGui(Gui gui) {
        this.gui = gui;
        for (Element element : this) {
            element.setGui(gui);
            if (element instanceof AbstractClickable c) {
                c.findClickMethod();
            }
        }
    }

    public void addAllChildElementsDeep(List<Element> list) {
        list.addAll(List.of(elements()));
        for (Element element : this) {
            if (element instanceof AbstractLayout abstractLayout) {
                abstractLayout.addAllChildElementsDeep(list);
            }
        }
    }

    public Element[] allElementsDeep() {
        List<Element> allElements = new ArrayList<>();
        addAllChildElementsDeep(allElements);
        return allElements.toArray(new Element[0]);
    }

    @Override
    public void click(int x, int y, int btn) {
        for (Element element : this) {
            if (element instanceof Clickable clickable) {
                clickable.click(x, y, btn);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Clickable clickable) {
                        clickable.click(x, y, btn);
                    }
                }
            }
        }
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        for (Element element : this) {
            if (element instanceof Clickable clickable) {
                clickable.clickRelease(x, y, btn);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Clickable clickable) {
                        clickable.clickRelease(x, y, btn);
                    }
                }
            }
        }
    }

    @Override
    public void dragBegin(int x, int y, int btn) {
        for (Element element : this) {
            if (element instanceof Draggable draggable) {
                draggable.dragBegin(x, y, btn);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Draggable draggable) {
                        draggable.dragBegin(x, y, btn);
                    }
                }
            }
        }
    }

    @Override
    public void drag(int x, int y, int btn) {
        for (Element element : this) {
            if (element instanceof Draggable draggable) {
                draggable.drag(x, y, btn);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Draggable draggable) {
                        draggable.drag(x, y, btn);
                    }
                }
            }
        }
    }

    @Override
    public void dragLeave(int x, int y, int btn) {
        for (Element element : this) {
            if (element instanceof Draggable draggable) {
                draggable.dragLeave(x, y, btn);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Draggable draggable) {
                        draggable.dragLeave(x, y, btn);
                    }
                }
            }
        }
    }

    @Override
    public void keyPress(int key) {
        for (Element element : this) {
            if (element instanceof Keyboard keyboard) {
                keyboard.keyPress(key);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Keyboard keyboard) {
                        keyboard.keyPress(key);
                    }
                }
            }
        }
    }

    @Override
    public void keyType(int key) {
        for (Element element : this) {
            if (element instanceof Keyboard keyboard) {
                keyboard.keyType(key);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Keyboard keyboard) {
                        keyboard.keyType(key);
                    }
                }
            }
        }
    }

    @Override
    public void keyRelease(int key) {
        for (Element element : this) {
            if (element instanceof Keyboard keyboard) {
                keyboard.keyRelease(key);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Keyboard keyboard) {
                        keyboard.keyRelease(key);
                    }
                }
            }
        }
    }

    @Override
    public void scrollX(int amount) {
        for (Element element : this) {
            if (element instanceof Scrollable scrollable) {
                scrollable.scrollX(amount);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Scrollable scrollable) {
                        scrollable.scrollX(amount);
                    }
                }
            }
        }
    }

    @Override
    public void scrollY(int amount) {
        for (Element element : this) {
            if (element instanceof Scrollable scrollable) {
                scrollable.scrollY(amount);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if(e instanceof Scrollable scrollable) {
                        scrollable.scrollY(amount);
                    }
                }
            }
        }
    }

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        this.theme = theme;
        for (Element element : this) {
            element.setTheme(theme);
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    e.setTheme(theme);
                }
            }
        }
        measureMaxSize();
    }

    @Override
    public Iterator<Element> iterator() {
        return new ElementIterator();
    }

    @Override
    public void forEach(Consumer<? super Element> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Element> spliterator() {
        return Iterable.super.spliterator();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        StringBuilder result = new StringBuilder();
        result.append("| ".repeat(indent)).append(this.getClass().getSimpleName()).append(": ").append(getId()).append(" ").append(getTags() != null ? Arrays.toString(getTags()) : "[]").append(System.lineSeparator());
        indent += 1;

        for (Element element : elements()) {
            if (element instanceof AbstractLayout layout) {
                result.append(layout.toString(indent));
            } else {
                result.append("| ".repeat(indent)).append(element.getClass().getSimpleName()).append(": ").append(element.getId()).append(" ").append(element.getTags() != null ? Arrays.toString(element.getTags()) : "[]").append(System.lineSeparator());
            }
        }

        return result.toString();
    }

    public <T> T findElementById(String id) {
        for (Element e : allElementsDeep()) {
            if (id.equals(e.getId())) return (T) e;
        }
        return null;
    }

    public <T> List<T> findElementsByTag(String tag) {
        List<T> elementList = new ArrayList<>();
        for (Element e : allElementsDeep()) {
            for (String elementTag : e.getTags()) {
                if (elementTag.equals(tag) && !elementList.contains(e)) elementList.add((T) e);
            }
        }
        return elementList;
    }

    public <T> List<T> findElementsByType(Class<? extends T> type) {
        List<T> elementList = new ArrayList<>();
        for (Element e : allElementsDeep()) {
            if (e.getClass().equals(type) && !elementList.contains(e)) elementList.add((T) e);
        }
        return elementList;
    }

    public <T> List<T> findElementsBySuperType(Class<? extends T> type) {
        List<T> elementList = new ArrayList<>();
        for (Element e : allElementsDeep()) {
            if (type.isAssignableFrom(e.getClass()) && !elementList.contains(e)) elementList.add((T) e);
        }
        return elementList;
    }

    @Override
    public void setWindow(Window window) {
        super.setWindow(window);
        for (Element element : this) {
            element.setWindow(window);
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        //for(Element e : allElementsDeep()) {
        //    if (!(e instanceof AbstractLayout)) {
        //        e.setWidth(e.getWidth() + width);
        //    }
        //}
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        //for(Element e : allElementsDeep()) {
        //    if (!(e instanceof AbstractLayout)) {
        //        e.setHeight(e.getHeight() + height);
        //        System.out.println(e.getHeight() + ": " + e);
        //    }
        //}
    }

    public class ElementIterator implements Iterator<Element> {
        private boolean hasNext = true;
        private int index = 0;
        private List<Element> collection;

        public ElementIterator() {
            collection = List.copyOf(elements);
        }

        @Override
        public boolean hasNext() {
            return index != collection.size();
        }

        @Override
        public Element next() {
            return collection.get(index++);
        }

        @Override
        public void remove() {
            collection.remove(index - 1);
        }

        @Override
        public void forEachRemaining(Consumer<? super Element> action) {
            Iterator.super.forEachRemaining(action);
        }
    }
}
