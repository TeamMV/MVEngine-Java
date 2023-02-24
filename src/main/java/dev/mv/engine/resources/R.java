package dev.mv.engine.resources;

import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.render.shared.texture.TextureRegion;

import static org.lwjgl.glfw.GLFW.*;

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
            if(action == InputCollector.KeyAction.TYPE || action == InputCollector.KeyAction.REPEAT) {
                int key = keyCode;
                System.out.println(mods);
                if (Character.isAlphabetic(key) && (((mods & GLFW_MOD_SHIFT) != 0) ^ ((mods & GLFW_MOD_CAPS_LOCK) != 0))) {
                    key = Character.toUpperCase(key);
                }
                else if (Character.isAlphabetic(key)) {
                    key = Character.toLowerCase(key);
                }
                GUIS.typeKey(key);
            }
            if(action == InputCollector.KeyAction.RELEASE) GUIS.releaseKey(keyCode);
        }
    }

    public static TextureRegion getTexture(String textId) {
        return null;
    }
}
