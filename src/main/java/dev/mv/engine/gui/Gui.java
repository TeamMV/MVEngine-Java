package dev.mv.engine.gui;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.components.layouts.AbstractLayout;
import dev.mv.engine.gui.components.layouts.UpdateSection;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Gui {
    private DrawContext2D drawContext;
    private Theme theme = null;
    private String name;
    private UpdateSection root;

    public Gui(@NonNull DrawContext2D drawContext, Window window, String name) {
        this.drawContext = drawContext;
        this.name = name;
        root = new UpdateSection(window, null);
        root.setGui(this);
    }

    public String getName() {
        return name;
    }

    public void addElement(Element e) {
        root.addElement(e);
        e.setGui(this);
    }

    public void addElements(Element[] e) {
        root.addElements(e);
        for(Element element : e) {
            element.setGui(this);
        }
    }

    public void removeElement(Element e) {
        root.removeElement(e);
        e.setGui(null);
    }

    public void removeElements(Element[] e) {
        root.removeElements(e);
        for(Element element : e) {
            element.setGui(null);
        }
    }

    public void removeElements(Predicate<? super Element> predicate) {
        root.removeElements(predicate);
    }

    public Element[] elements() {
        return root.elements();
    }

    public void deepLoop(Consumer<Element> elementConsumer) {
        List<Element> allElements = new ArrayList<>(List.of(elements()));
        root.addAllChildElementsDeep(allElements);
        allElements.forEach(elementConsumer);
    }

    public Element[] deepLoop() {
        List<Element> allElements = new ArrayList<>(List.of(elements()));
        root.addAllChildElementsDeep(allElements);
        return allElements.toArray(new Element[0]);
    }

    public void disableAllUpdates() {
        root.disable();
        for(Element element : deepLoop()) {
            if(element instanceof UpdateSection updateSection) {
                updateSection.disable();
            }
        }
    }

    public void enableAllUpdates() {
        root.enable();
        for(Element element : deepLoop()) {
            if(element instanceof UpdateSection updateSection) {
                updateSection.enable();
            }
        }
    }

    public UpdateSection getRoot() {
        return root;
    }

    public void applyTheme(Theme theme) throws IOException {
        GuiAssets.init(theme);
        this.theme = theme;
        for(Element element : root) {
            element.setTheme(theme);
        }
    }

    //----- draw -----

    void draw() {
        for(Element element : root) {
            element.draw(drawContext);
        }
    }

    public void drawSpecific(Predicate<? super Element> predicate) {
        for(Element e : root) {
            if(predicate.test(e)) {
                e.draw(drawContext);
            }
        }
    }

    //----- event calls -----

    public void loop() {

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

    public void pressKey(int keyCode) {
        keyPress(keyCode);
    }

    public void typeKey(int keyCode) {
        keyType(keyCode);
    }

    public void releaseKey(int keyCode) {
        keyRelease(keyCode);
    }

    private void click(int x, int y, int btn) {
        root.click(x, y, btn);
    }

    private void release(int x, int y, int btn) {
        root.clickRelease(x, y, btn);
    }

    private void dragBegin(int x, int y, int btn) {
        root.dragBegin(x, y, btn);
    }

    private void drag(int x, int y, int btn) {
        root.drag(x, y, btn);
    }

    private void dragEnd(int x, int y, int btn) {
        root.dragBegin(x, y, btn);
    }

    private void keyPress(int key) {
        root.keyPress(key);
    }

    private void keyType(int key) {
        root.keyType(key);
    }

    private void keyRelease(int key) {
        root.keyRelease(key);
    }

    private void scrollX(int amt) {
        root.scrollX(amt);
    }

    private void scrollY(int amt) {
        root.scrollY(amt);
    }

    @Override
    public String toString() {
        int indent = 0;
        StringBuilder result = new StringBuilder("Root").append(System.lineSeparator());
        indent += 1;

        for(Element element : elements()) {
            if(element instanceof AbstractLayout layout) {
                result.append(layout.toString(indent));
            } else {
                result.append("| ".repeat(indent)).append(element.getClass().getSimpleName()).append(": ").append(element.getId()).append(" ").append(element.getTags() != null ? Arrays.toString(element.getTags()) : "[]").append(System.lineSeparator());
            }
        }

        return result.toString();
    }
}
