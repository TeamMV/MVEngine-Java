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
import dev.mv.utils.misc.Version;
import org.jetbrains.annotations.NotNull;

public abstract class Game {

    private Directory gameDirectory;
    private ConfigFile config;
    private GameManager manager;
    private boolean modded;

    protected Game() {
        MVEngine.instance().setGame(this);
        manager = new GameManager(this);
    }

    private void setupGameDir() {
        gameDirectory = FileManager.getDirectory(getGameId());
        config = gameDirectory.getConfigFile("game.cfg");
    }

    protected void initialize() {
        setupGameDir();
        if (getModState() == ModState.MODDABLE) config.setBooleanIfAbsent("modded", false);
        config.setStringIfAbsent("lang", "en_us");
        config.setIntIfAbsent("fps", 60);
        config.setBooleanIfAbsent("vsync", true);
        config.save();
        modded = getModState() != ModState.NOT_MODDED && (getModState() == ModState.ALWAYS_MODDED || config.getBoolean("modded"));
        Languages.init(Languages.scanLanguages(getGameId()), config.getString("lang"));
        Events.init();
        if (modded) {
            ModFinder.findMods(gameDirectory.getSubDirectory("mods"));
        }
        Registries.init();
        if (modded) {
            ModLoader.loadAndInitMods();
        }
    }

    public Directory getGameDirectory() {
        return gameDirectory;
    }

    protected GameManager manage() {
        return manager;
    }

    protected ConfigFile config() {
        return config;
    }

    public abstract @NotNull String getGameId();

    public abstract @NotNull Version getVersion();

    public abstract @NotNull String getName();

    public abstract @NotNull ModState getModState();

    protected enum ModState {
        /**
         * The version of the game will not try to load mods no matter what, nothing can change its opinion on that >:D.
         */
        NOT_MODDED,
        /**
         * The version of the game will load mods if the config value 'modded' is set to true (affected in launcher).
         */
        MODDABLE,
        /**
         * The version of the game will always try to load mods.
         */
        ALWAYS_MODDED
    }

}
