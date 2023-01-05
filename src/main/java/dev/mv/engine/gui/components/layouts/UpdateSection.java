package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class UpdateSection extends AbstractLayout implements Toggle, IgnoreDraw {
    private boolean enabled = true;

    public UpdateSection(Window window, Element parent) {
        super(window, parent);
    }

    public UpdateSection(Window window, int width, int height, Element parent) {
        super(window, width, height, parent);
    }

    public UpdateSection(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        elements.forEach(e -> e.draw(draw));
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
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.click(x, y, btn);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Clickable clickable) {
                clickable.click(x, y, btn);
            }
        }
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.clickRelease(x, y, btn);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Clickable clickable) {
                clickable.clickRelease(x, y, btn);
            }
        }
    }

    @Override
    public void dragBegin(int x, int y, int btn) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.dragBegin(x, y, btn);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Draggable draggable) {
                draggable.dragBegin(x, y, btn);
            }
        }
    }

    @Override
    public void drag(int x, int y, int btn) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.drag(x, y, btn);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Draggable draggable) {
                draggable.drag(x, y, btn);
            }
        }
    }

    @Override
    public void dragLeave(int x, int y, int btn) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.dragLeave(x, y, btn);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Draggable draggable) {
                draggable.dragLeave(x, y, btn);
            }
        }
    }

    @Override
    public void keyPress(int key) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.keyPress(key);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Keyboard keyboard) {
                keyboard.keyPress(key);
            }
        }
    }

    @Override
    public void keyType(int key) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.keyType(key);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Keyboard keyboard) {
                keyboard.keyType(key);
            }
        }
    }

    @Override
    public void keyRelease(int key) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.keyRelease(key);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Keyboard keyboard) {
                keyboard.keyRelease(key);
            }
        }
    }

    @Override
    public void scrollX(int amount) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.scrollX(amount);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Scrollable scrollable) {
                scrollable.scrollX(amount);
            }
        }
    }

    @Override
    public void scrollY(int amount) {
        if(!enabled) {
            for(Element element : allElementsDeep()) {
                if(element instanceof UpdateSection updateSection) {
                    updateSection.scrollY(amount);
                }
            }
            return;
        }
        for(Element element : elements) {
            if(element instanceof Scrollable scrollable) {
                scrollable.scrollY(amount);
            }
        }
    }

    @Override
    public Element[] toRender() {
        return elements();
    }
}
