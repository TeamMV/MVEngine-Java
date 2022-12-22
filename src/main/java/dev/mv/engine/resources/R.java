package dev.mv.engine.resources;

import dev.mv.engine.gui.GuiRegistry;

//Stop making rip off android classes we need to be original not a fucking copycat
//dude
//its good like this
public class R {
    public static GuiRegistry GUIS = null;

    public static void initialize(ResourceBundle bundle) {
        GUIS = bundle.getGuiRegistry();
    }
}
