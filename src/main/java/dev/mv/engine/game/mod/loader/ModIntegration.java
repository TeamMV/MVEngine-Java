package dev.mv.engine.game.mod.loader;

import dev.mv.utils.Utils;
import dev.mv.utils.collection.Vec;

import java.io.InputStream;

public class ModIntegration {

    private static final Vec<Class<?>> baseClasses = Utils.getAllClasses(i -> !Utils.containsAny(i, "dev.mv.engine",
        "dev.mv.utils", "org.lwjgl", "de.fabmax.physxjni", "physx.",
        "org.joml", "com.codedisaster.steamworks", "javax.annotation",
        "org.jetbrains.annotations", "org.intellij"));

    private static final Vec<Class<?>> classes = baseClasses.clone();

    private static final Vec<String> assets = new Vec<>();

    public static Class<?> loadClass(String name) {
        try {
            return ModFinder.getLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream getResourceAsStream(String name) {
        return ModFinder.getLoader().getResourceAsStream(name);
    }

    public static Vec<Class<?>> getClasses() {
        return classes;
    }

    public static Vec<Class<?>> getBaseClasses() {
        return baseClasses;
    }

    public static Vec<String> getAssets() {
        return assets;
    }

    static void addClasses(Vec<Class<?>> extra) {
        classes.append(extra);
    }

    static void addAssets(Vec<String> extra) {
        assets.append(extra);
    }

}
