package dev.mv.engine.gui.parsing.gui;

import dev.mv.engine.gui.GuiRegistry;

import java.io.File;

public class GuiParser {
    public static GuiRegistry parse(GuiConfig guiConfig) {
        File layoutDir = new File(guiConfig.getLayoutPath());
        if(!layoutDir.isDirectory()) {
            throw new RuntimeException(new InvalidLayoutPathException("Specified gui layout path is not a valid directory!"));
        }

        File[] layoutFiles = layoutDir.listFiles(((dir, name) -> dir.isFile() && name.endsWith(".xml")));
        for(File layout : layoutFiles) {

        }

        return new GuiRegistry();
    }
}
