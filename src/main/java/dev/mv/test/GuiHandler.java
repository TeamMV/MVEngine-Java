package dev.mv.test;

import dev.mv.engine.gui.components.Button;
import dev.mv.engine.gui.components.InputBox;
import dev.mv.engine.gui.functions.GuiFunction;

public class GuiHandler {

    @GuiFunction
    public static void chromaButtonClick(Button button) {
        System.out.println(button.getGui().getRoot().<InputBox>findElementById("email").getText());
    }
}
