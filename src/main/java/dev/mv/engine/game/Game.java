package dev.mv.engine.game;

import dev.mv.engine.MVEngine;
import dev.mv.engine.files.ConfigFile;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.game.event.Events;
import dev.mv.engine.game.language.Languages;
import dev.mv.engine.game.mod.loader.ModFinder;
import dev.mv.engine.game.mod.loader.ModLoader;
import dev.mv.engine.game.registry.Registries;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.misc.Version;
import org.jetbrains.annotations.NotNull;

public abstract class Game {

    private Directory gameDirectory;
    private ConfigFile config;

    protected Game() {
        MVEngine.instance().setGame(this);
    }

    private void setupGameDir() {
        gameDirectory = FileManager.getDirectory(getGameId());
        config = gameDirectory.getConfigFile("game.cfg");
    }

    protected void initialize() {
        config.setBooleanIfAbsent("modded", false);
        config.setStringIfAbsent("lang", "en_us");
        Languages.init(Languages.scanLanguages(getGameId()), config.getString("lang"));
        Events.init();
        if (config.getBoolean("modded")) {
            ModFinder.findMods(gameDirectory.getSubDirectory("mods"));
        }
        Registries.init();
        if (config.getBoolean("modded")) {
            ModLoader.loadAndInitMods();
        }
    }

    public Directory getGameDirectory() {
        return gameDirectory;
    }

    @Override
    public void start(MVEngine engine, Window window) {

    }

    @Override
    public void update(MVEngine engine, Window window) {

    }

    @Override
    public void draw(MVEngine engine, Window window) {

    }

    public abstract @NotNull String getGameId();

    public abstract @NotNull Version getVersion();

    public abstract @NotNull String getName();

}
