package dev.mv.engine.gui.components;

import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.Window;

import java.util.ArrayList;
import java.util.List;

public class NumericInputBox extends InputBox {
    private List<Character> allowedList = new ArrayList<>();

    public NumericInputBox(Window window, Element parent, int width, int height) {
        super(window, parent, width, height);
        prepareList();
        setAllowedlist(allowedList);
    }

    public NumericInputBox(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, parent, width, height);
        prepareList();
        setAllowedlist(allowedList);
    }

    public NumericInputBox(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height);
        prepareList();
        setAllowedlist(allowedList);
    }

    public NumericInputBox(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
        prepareList();
        setAllowedlist(allowedList);
    }

    private void prepareList() {
        allowedList.add('0');
        allowedList.add('1');
        allowedList.add('2');
        allowedList.add('3');
        allowedList.add('4');
        allowedList.add('5');
        allowedList.add('6');
        allowedList.add('7');
        allowedList.add('8');
        allowedList.add('9');
    }
}
