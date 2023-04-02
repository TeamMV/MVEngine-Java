package dev.mv.engine.resources;

public interface Resource {
    enum Type {
        COLOR,
        TEXTURE,
        TEXTURE_REGION,
        MESH,
        GUI_LAYOUT,
        GUI_REGISTRY,
        GUI_THEME,
        FONT,
        SOUND,
        MUSIC,
        GUI_PAGE
    }
}
