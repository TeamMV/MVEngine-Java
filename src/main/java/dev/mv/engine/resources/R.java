package dev.mv.engine.resources;

import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.render.shared.texture.TextureRegion;
import dev.mv.utils.Utils;

import static dev.mv.engine.input.Input.*;

//Stop making rip off android classes we need to be original not a fucking copycat
//dude
//its good like this
public class R {
    private R() {}

    public static GuiRegistry GUIS = null;

    public static void initialize(ResourceBundle bundle) {
        GUIS = bundle.getGuiRegistry();
    }

    public static void sendInputKeyEvent(int keyCode, InputCollector.KeyAction action, int mods) {
        if(GUIS != null) {
            if(action == InputCollector.KeyAction.TYPE) GUIS.pressKey(keyCode);
            if(action == InputCollector.KeyAction.RELEASE) GUIS.releaseKey(keyCode);
            if (action == InputCollector.KeyAction.TYPE || action == InputCollector.KeyAction.REPEAT) {
                if (Utils.isAnyOf(Input.convertKey(keyCode), KEY_BACKSPACE, KEY_ARROW_UP, KEY_ARROW_LEFT, KEY_ARROW_RIGHT, KEY_ARROW_DOWN, KEY_ARROW_UP, KEY_DELETE)) GUIS.typeKey(keyCode);
            }
        }
    }

    public static void sendCharTypedEvent(int charCode) {
        if (GUIS != null) {
            GUIS.typeKey(charCode);
        }
    }

    public static TextureRegion getTexture(String textId) {
        return null;
    }
}
