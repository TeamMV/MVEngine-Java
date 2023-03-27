package dev.mv.engine.game;

import dev.mv.engine.files.ConfigFile;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.game.event.Events;
import dev.mv.engine.game.mod.loader.ModFinder;
import dev.mv.engine.game.mod.loader.ModLoader;

public abstract class Game {

    private Directory gameDirectory;
    private ConfigFile config;

    public abstract String getGameId();

    private void setupGameDir() {
        gameDirectory = FileManager.getDirectory(getGameId());
        config = gameDirectory.getConfigFile("game.cfg");
    }

    protected void initialize() {
        config.setBooleanIfAbsent("modded", false);
        Events.init();
        if (config.getBoolean("modded")) {
            ModFinder.findMods(gameDirectory, gameDirectory.getSubDirectory("mods"));
        }
        //init registries
        if (config.getBoolean("modded")) {
            ModLoader.loadAndInitMods();
        }
    }

    private void loadMods() {

    }

    public Directory getGameDirectory() {
        return gameDirectory;
    }

}
