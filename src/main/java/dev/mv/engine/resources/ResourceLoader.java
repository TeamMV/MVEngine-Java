package dev.mv.engine.resources;

import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiManager;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.parsing.GuiConfig;
import dev.mv.engine.gui.parsing.gui.GuiParser;
import dev.mv.engine.gui.parsing.theme.ThemeParser;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.texture.TextureRegion;
import dev.mv.utils.Utils;

import java.io.IOException;
import java.util.*;

public class ResourceLoader {
    private static class ResourceReference {
        private String path;
        private String id;
        private Resource.Type type;

        public ResourceReference(String path, String id, Resource.Type type) {
            this.path = path;
            this.id = id;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public String getId() {
            return id;
        }

        public Resource.Type getType() {
            return type;
        }
    }

    private static Queue<ResourceReference> refs = new LinkedList<>();
    private static Queue<ResourceReference> dependentRefs = new LinkedList<>();

    public static void markColor(String resourceId, String colorString) {
        refs.add(new ResourceReference(colorString, resourceId, Resource.Type.COLOR));
    }

    public static void markTexture(String resourceId, String path) {
        refs.add(new ResourceReference(path, resourceId, Resource.Type.TEXTURE));
    }

    public static void markTextureRegion(String resourceId, String targetResourceId, int x, int y, int width, int height) {
        refs.add(new ResourceReference(Utils.concat("", targetResourceId, x, y, width, height), resourceId, Resource.Type.TEXTURE_REGION));
    }

    public static void markModel(String resourceId, String path) {
        refs.add(new ResourceReference(path, resourceId, Resource.Type.MESH));
    }

    public static void markFont(String resourceId, String pngFile, String fntFile) {
        refs.add(new ResourceReference(Utils.concat(":", pngFile, fntFile), resourceId, Resource.Type.FONT));
    }

    public static void markLayout(String resourceId, String path) {
        dependentRefs.add(new ResourceReference(path, resourceId, Resource.Type.GUI_LAYOUT));
    }

    public static void markTheme(String resourceId, String path) {
        dependentRefs.add(new ResourceReference(path, resourceId, Resource.Type.GUI_THEME));
    }

    public static void load(MVEngine engine, GuiConfig config) throws IOException {
        load(engine, config, (t, c, p) -> {});
    }

    public static void load(MVEngine engine, GuiConfig config, ProgressAction progressAction) throws IOException {
        GuiParser guiParser = new GuiParser(config);
        ThemeParser themeParser = new ThemeParser(config);
        int totalRefs = refs.size() + dependentRefs.size();
        final int[] current = {0};

        while (!refs.isEmpty()) {
            ResourceReference ref = refs.poll();
            switch (ref.getType()) {
                case COLOR -> register(ref.getId(), Color.parse(ref.getPath()));
                case TEXTURE -> register(ref.getId(), RenderBuilder.newTexture(ref.getPath()).convertToRegion());
                case TEXTURE_REGION -> register(ref.getId(), R.textures.get(ref.getPath().split(":")[0]).getParentTexture().cutRegion(Integer.parseInt(ref.getPath().split(":")[1]), Integer.parseInt(ref.getPath().split(":")[2]), Integer.parseInt(ref.getPath().split(":")[3]), Integer.parseInt(ref.getPath().split(":")[4])));
                case MESH -> register(ref.getId(), engine.getObjectLoader().loadExternalModel(ref.getPath()));
                case FONT -> register(ref.getId(), new BitmapFont(ref.getPath().split(":")[0], ref.getPath().split(":")[1]));
            }
            Utils.ifNotNull(progressAction).then(p -> p.update(totalRefs, current[0], (int) Utils.getPercent(current[0]++, totalRefs)));
        }

        while (!dependentRefs.isEmpty()) {
            ResourceReference ref = dependentRefs.poll();
            switch (ref.getType()) {
                case GUI_LAYOUT -> register(ref.getId(), guiParser.parse(null)); //TODO: provide InputStream
                case GUI_THEME -> register(ref.getId(), themeParser.parse(ref.getPath()));
            }
            Utils.ifNotNull(progressAction).then(p -> p.update(totalRefs, current[0], (int) Utils.getPercent(current[0]++, totalRefs)));
        }

        register("default", guiParser.parse());

        R.setIsReady(true);

        GuiManager.manage(R.guis.get("default"));
    }

    private static void register(String id, Resource resource) {
        if(resource instanceof TextureRegion t)     R.textures.register(id, t);
        if(resource instanceof Color t)             R.colors.register(id, t);
        if(resource instanceof Model t)             R.models.register(id, t);
        if(resource instanceof Theme t)             R.themes.register(id, t);
        if(resource instanceof Gui t)               R.layouts.register(id, t);
        if(resource instanceof GuiRegistry t)       R.guis.register(id, t);
        if(resource instanceof BitmapFont t)        R.fonts.register(id, t);
    }
}
