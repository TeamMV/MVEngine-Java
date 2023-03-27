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
import org.jetbrains.annotations.NotNull;

public abstract class Game {

    private Directory gameDirectory;
    private ConfigFile config;

    protected Game() {
        MVEngine.instance().setGame(this);
    }

    public abstract @NotNull String getGameId();

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

}
