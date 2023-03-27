package dev.mv.engine.gui.components;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.functions.GuiMethod;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;

public abstract class AbstractClickable extends Element implements Clickable {
    protected String clickMethodName;
    protected Class<?>[] clickMethodParamTypes;
    protected Object[] clickMethodParams;
    protected GuiMethod clickMethod;

    protected AbstractClickable(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    protected AbstractClickable(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    public void setClickMethod(String name, Class<?>[] paramTypes, Object[] params) {
        clickMethodName = name;
        clickMethodParamTypes = paramTypes;
        clickMethodParams = params;
    }

    public void findClickMethod() {
        if (clickMethod != null) return;
        if (Utils.isAnyNull(clickMethodName, clickMethodParamTypes, clickMethodParams)) return;
        try {
            clickMethod = gui.findMethod(clickMethodName, clickMethodParamTypes, this.id);
        } catch (NoSuchMethodException e) {
            Exceptions.send(e);
        }
        clickListeners.add(new ClickListener() {
            @Override
            public void onCLick(Element element, int button) {
            }

            @Override
            public void onRelease(Element element, int button) {
                clickMethod.invoke(clickMethodParams);
            }
        });
    }
}
