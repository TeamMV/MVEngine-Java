package dev.mv.engine.game.mod.loader;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.utils.collection.Vec;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ModLoader {

    private static final Map<String, Mod> mods = new HashMap<>();
    private static final Vec<String> initialized = new Vec<>();

    public static void loadAndInitMods() {
        loadMods();
        initMods();
    }

    public static void loadMods() {
        for (String id : ModFinder.getMods()) {
            loadMod(id);
        }
    }

    public static void loadMod(String id) {
        for (Class<?> clazz : ModFinder.getModClasses(id)) {
            try {
                if (clazz.isAnnotationPresent(dev.mv.engine.game.mod.api.Mod.class)) {
                    dev.mv.engine.game.mod.api.Mod annotation = clazz.getAnnotation(dev.mv.engine.game.mod.api.Mod.class);
                    if (annotation.value().equals(id)) {
                        Object instance = clazz.getConstructor().newInstance();
                        Mod mod = new Mod(id, annotation.dependencies(), clazz, instance, ModFinder.getModClasses(id), ModFinder.getModAssets(id), ModFinder.getModUrl(id));
                        mods.put(id, mod);
                    }
                }
            } catch (InvocationTargetException e) {
                Exceptions.send("MOD_LOAD_MAIN_ERROR", id);
            } catch (InstantiationException e) {
                Exceptions.send("MOD_LOAD_MAIN_INSTANCE", id);
            } catch (IllegalAccessException e) {
                Exceptions.send("MOD_LOAD_MAIN_ACCESS", id);
            } catch (NoSuchMethodException e) {
                Exceptions.send("MOD_LOAD_MAIN_ABSENT", id);
            }
        }
    }

    public static void initMods() {
        Vec<Mod> needsInit = new Vec<>();
        outer:
        for (Mod mod : mods.values()) {
            if (!initialized.contains(mod.id)) {
                if (mod.dependencies != null) {
                    for (String dependency : mod.dependencies) {
                        if (!mods.containsKey(dependency)) {
                            Exceptions.send("MOD_LOAD_UNMET_DEPENDENCY", mod.id, dependency);
                        }
                        if (!initialized.contains(dependency)) {
                            needsInit.push(mod);
                            continue outer;
                        }
                    }
                }
                initialized.push(mod.id);
                initMod(mod);
            }
        }
        while (!needsInit.isEmpty()) {
            for (Mod mod : needsInit) {
                if (!initialized.contains(mod.id)) {
                    if (mod.dependencies != null && mod.dependencies.length > 0) {
                        if (!initialized.containsAll(mod.dependencies)) continue;
                    }
                    needsInit.remove(mod);
                    initMod(mod);
                }
            }
        }
    }

    private static void initMod(Mod mod) {
        mod.init();
        mod.initRegistries();
        mod.initListeners();
        mod.initAssets();
    }

    public static Mod getMod(String id) {
        return mods.get(id);
    }

    public static String[] getLoadedMods() {
        return mods.keySet().toArray(new String[0]);
    }
}
