package dev.mv.engine.resources;

import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.input.InputCollector;

//Stop making rip off android classes we need to be original not a fucking copycat
//dude
//its good like this
public class R {
    public static GuiRegistry GUIS = null;

    public static void initialize(ResourceBundle bundle) {
        GUIS = bundle.getGuiRegistry();
    }

    public static void sendInputKeyEvent(int keyCode, InputCollector.KeyAction action) {
        if(GUIS != null) {
            if(action == InputCollector.KeyAction.PRESS) GUIS.pressKey(keyCode);
            if(action == InputCollector.KeyAction.TYPE) GUIS.typeKey(keyCode);
            if(action == InputCollector.KeyAction.RELEASE) GUIS.releaseKey(keyCode);
        }
    }
}
