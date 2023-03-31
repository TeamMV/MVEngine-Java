package dev.mv.engine.resources;

import dev.mv.engine.audio.Sound;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.pages.Page;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.texture.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class R {
    public static class Res<T extends Resource>{
        private Map<String, T> map = new HashMap<>();

        Res() {}

        public T get(String id) {
            try {
                return map.get(id);
            } catch (Exception e) {
                Exceptions.send(new ResourceNotFoundException("There is no resource with resource-id of \"" + id + "\"!"));
                return null;
            }
        }

        public T[] all() {
            return (T[]) map.values().toArray(new Object[0]);
        }

        void register(String id, T res) {
            map.put(id, res);
        }
    }

    public static Res<TextureRegion> textures = new Res<>();
    public static Res<Color> colors = new Res<>();
    public static Res<Model> models = new Res<>();
    public static Res<Gui> layouts = new Res<>();
    public static Res<GuiRegistry> guis = new Res<>();
    public static Res<Theme> themes = new Res<>();
    public static Res<BitmapFont> fonts = new Res<>();
    public static Res<Page> pages = new Res<>();
    public static Res<Sound> sounds = new Res<>();

    static {
        guis.register("default", new GuiRegistry());
    }

    private static boolean isReady = false;

    public static boolean isReady() {
        return isReady;
    }

    static void setIsReady(boolean isReady) {
        R.isReady = isReady;
    }
}