package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.ScrollInput;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.Window;

public abstract class IgnoreInput extends Element implements Clickable, Draggable, Keyboard, ScrollInput {
    protected IgnoreInput(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    protected IgnoreInput(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    protected abstract Element[] forInput();

    @Override
    public void click(int x, int y, int btn) {
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
        for (Element element : forInput()) {
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
    public boolean distributeScrollX(int amount) {
        for (Element element : forInput()) {
            if (!GuiUtils.mouseInside(element)) continue;
            if (element instanceof ScrollInput scrollInput) {
                return scrollInput.distributeScrollX(amount);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if (!GuiUtils.mouseInside(e)) continue;
                    if(e instanceof ScrollInput scrollInput) {
                        return scrollInput.distributeScrollX(amount);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean distributeScrollY(int amount) {
        for (Element element : forInput()) {
            if (!GuiUtils.mouseInside(element)) continue;
            if (element instanceof ScrollInput scrollInput) {
                return scrollInput.distributeScrollY(amount);
            }
            if(element instanceof IgnoreDraw ig) {
                for (Element e : ig.toRender()) {
                    if (!GuiUtils.mouseInside(e)) continue;
                    if(e instanceof ScrollInput scrollInput) {
                        return scrollInput.distributeScrollY(amount);
                    }
                }
            }
        }
        return false;
    }
}
