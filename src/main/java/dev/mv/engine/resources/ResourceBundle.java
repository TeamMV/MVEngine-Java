package dev.mv.engine.resources;

import dev.mv.engine.gui.GuiRegistry;

public class ResourceBundle {
    private GuiRegistry guis;

    public GuiRegistry getGuiRegistry() {
        return guis;
    }

    public ResourceBundle setGuiRegistry(GuiRegistry guis) {
        this.guis = guis;
        return this;
    }
}
