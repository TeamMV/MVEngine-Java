package dev.mv.engine.gui;

import dev.mv.engine.gui.components.AbstractClickable;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.components.layouts.AbstractLayout;
import dev.mv.engine.gui.components.layouts.LayerSection;
import dev.mv.engine.gui.components.layouts.UpdateSection;
import dev.mv.engine.gui.functions.GuiMethod;
import dev.mv.engine.gui.functions.GuiScript;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.resources.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Gui implements Resource {
    private DrawContext2D drawContext;
    private Theme theme = null;
    private String name;
    private UpdateSection root;
    private List<List<LayerSection>> layers;
    private List<GuiScript> scripts;

    public Gui(String name) {
        this.name = name;
        root = new UpdateSection(null, null);
        root.setGui(this);
        root.setId("root");
        layers = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            layers.add(new ArrayList<>());
        }
        scripts = new ArrayList<>();
    }

    public void applyRenderer(DrawContext2D drawContext2D) {
        this.drawContext = drawContext2D;
        root.setWindow(drawContext.getWindow());
        this.resize(drawContext2D.getWindow().getWidth(), drawContext2D.getWindow().getHeight());
    }

    public String getName() {
        return name;
    }

    public List<List<LayerSection>> getLayers() {
        return layers;
    }

    private void drawLayers(DrawContext2D draw) {
        for (List<LayerSection> layer : layers) {
            for (LayerSection section : layer) {
                section.draw(drawContext);
            }
        }
    }

    public void addElement(Element e) {
        root.addElement(e);
        e.setGui(this);
        if (e instanceof AbstractClickable c) {
            c.findClickMethod();
        }
    }

    public void addElements(Element[] e) {
        root.addElements(e);
        for (Element element : e) {
            element.setGui(this);
        }
    }

    public void removeElement(Element e) {
        root.removeElement(e);
        e.setGui(null);
    }

    public void removeElements(Element[] e) {
        root.removeElements(e);
        for (Element element : e) {
            element.setGui(null);
        }
    }

    public void removeElements(Predicate<? super Element> predicate) {
        root.removeElements(predicate);
    }

    public Element[] elements() {
        return root.elements();
    }

    public void elementsDeep(Consumer<Element> elementConsumer) {
        List<Element> allElements = new ArrayList<>(List.of(elements()));
        root.addAllChildElementsDeep(allElements);
        allElements.forEach(elementConsumer);
    }

    public Element[] elementsDeep() {
        List<Element> allElements = new ArrayList<>(List.of(elements()));
        root.addAllChildElementsDeep(allElements);
        return allElements.toArray(new Element[0]);
    }

    public void disableAllUpdates() {
        root.disable();
        for (Element element : elementsDeep()) {
            if (element instanceof UpdateSection updateSection) {
                updateSection.disable();
            }
        }
    }

    public void enableAllUpdates() {
        root.enable();
        for (Element element : elementsDeep()) {
            if (element instanceof UpdateSection updateSection) {
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
        for (Element element : root) {
            element.setTheme(theme);
        }
    }

    //----- draw -----

    void draw() {
        for (Element element : root) {
            if (element instanceof IgnoreDraw ignoreDraw) {
                if (ignoreDraw instanceof LayerSection layerSection) {
                    getLayers().get(layerSection.getLayerToRenderOn()).add(layerSection);
                    continue;
                }
                for (Element e : ignoreDraw.toRender()) {
                    e.draw(drawContext);
                }
                continue;
            }
            element.draw(drawContext);
        }
        drawLayers(drawContext);
    }

    //----- event calls -----

    public void loop() {

        //mouseButtons
        for (int i = 0; i < Input.buttons.length; i++) {
            if (Input.buttons[i] == Input.State.ONPRESSED) {
                click(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
                dragBegin(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
            } else if (Input.buttons[i] == Input.State.ONRELEASED) {
                release(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
                dragEnd(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
            } else if (Input.buttons[i] == Input.State.PRESSED) {
                drag(Input.mouse[Input.MOUSE_X], Input.mouse[Input.MOUSE_Y], i);
            }
        }

        //scroll
        if (Input.mouse[Input.MOUSE_SCROLL_X] != 0.0) {
            scrollX(Input.mouse[Input.MOUSE_SCROLL_X]);
        }
        if (Input.mouse[Input.MOUSE_SCROLL_Y] != 0.0) {
            scrollY(Input.mouse[Input.MOUSE_SCROLL_Y]);
        }
    }

    public void resize(int width, int height) {
        for (Element e : elementsDeep()) {
            e.resize(width, height);
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

        for (Element element : elements()) {
            if (element instanceof AbstractLayout layout) {
                result.append(layout.toString(indent));
            } else {
                result.append("| ".repeat(indent)).append(element.getClass().getSimpleName()).append(": ").append(element.getId()).append(" ").append(element.getTags() != null ? Arrays.toString(element.getTags()) : "[]").append(System.lineSeparator());
            }
        }

        return result.toString();
    }

    //scripts

    public void addScript(GuiScript script) {
        scripts.add(script);
    }

    public GuiMethod findMethod(String name, Class<?>[] paramTypes, String id) throws NoSuchMethodException {
        GuiMethod method;
        for (GuiScript script : scripts) {
            method = script.findMethod(name, paramTypes, id);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public GuiScript[] getScripts() {
        return scripts.toArray(new GuiScript[0]);
    }

    public List<GuiScript> getScriptsList() {
        return scripts;
    }
}
