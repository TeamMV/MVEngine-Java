package dev.mv.engine.game.mod.loader;

import dev.mv.engine.MVEngine;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.files.Directory;
import dev.mv.engine.game.event.*;
import dev.mv.engine.game.language.Languages;
import dev.mv.engine.game.mod.api.ModManager;
import dev.mv.engine.game.registry.Registries;
import dev.mv.engine.game.registry.Registry;
import dev.mv.engine.game.registry.RegistryLoader;
import dev.mv.engine.game.registry.RegistryType;
import dev.mv.utils.Utils;
import dev.mv.utils.collection.Vec;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class Mod {

    String id;
    String[] dependencies;
    Manager manager;
    Class<?> mainClass;
    Object instance;
    Vec<Class<?>> classes;
    Vec<ModListener> staticListeners = new Vec<>();
    Vec<ModListener> instanceListeners = new Vec<>();
    Vec<String> assets;
    URL url;
    URLClassLoader loader;
    Map<RegistryType, Registry<?>> registries;

    Mod(String id, String[] dependencies, Class<?> mainClass, Object instance, Vec<Class<?>> classes, Vec<String> assets, URL url) {
        this.id = id;
        this.dependencies = dependencies;
        this.mainClass = mainClass;
        this.instance = instance;
        this.classes = classes;
        this.assets = assets.fastIter().filter(s -> s.startsWith("assets/" + this.id + "/")).collect();
        this.url = url;
        loader = new URLClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader());
        manager = new Manager(this);
        registries = new HashMap<>();
    }

    public void init() {
        try {
            mainClass.getMethod("init", ModManager.class).invoke(instance, manager);
        } catch (InvocationTargetException e) {
            Exceptions.send("MOD_INIT_THREW", id);
        } catch (IllegalAccessException e) {
            Exceptions.send("MOD_INIT_ACCESS", id);
        } catch (NoSuchMethodException e) {
            Exceptions.send("MOD_INIT_ABSENT", id);
        }
    }

    public void initRegistries() {
        Registries.getResourceTypes().forEach(clazz -> RegistryLoader.registerResources(classes, clazz, id));
    }

    public void initListeners() {
        for (Class<?> clazz : classes) {
            try {
                if (clazz.isAnnotationPresent(EventBusListener.class)) {
                    EventBusListener listener = clazz.getAnnotation(EventBusListener.class);
                    if (listener.type() == ListenerType.STATIC) {
                        Object instance = clazz.getConstructor().newInstance();
                        Events.bus(listener.bus()).register(instance);
                        staticListeners.push(new ModListener(clazz, instance));
                    }
                }
            } catch (Exception e) {
                Exceptions.send("MOD_INIT_STATIC_LISTENER", id, clazz.getName());
            }
        }
    }

    public void initAssets() {
        Utils.ifNotNull(getModAsset("exceptions.ini")).then(Exceptions::readExceptionINI);
        for (String asset : assets) {
            if (asset.matches("assets/" + id + "/lang/[a-zA-Z_]+.((json)|(lang))]")) {
                Languages.addLanguage(Languages.load(getModResource(asset)));
            }
        }
    }

    public InputStream getModResource(String path) {
        return loader.getResourceAsStream(path);
    }

    public InputStream getModAsset(String assetPath) {
        return loader.getResourceAsStream("assets/" + id + "/" + assetPath);
    }

    public static class ModListener {
        Class<?> clazz;
        Object instance;

        ModListener(Class<?> clazz, Object instance) {
            this.clazz = clazz;
            this.instance = instance;
        }
    }

    public class Manager implements ModManager {

        Mod mod;

        Manager(Mod mod) {
            this.mod = mod;
        }

        @Override
        public void registerListener(Object instance) {
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(EventBusListener.class)) {
                EventBusListener listener = clazz.getAnnotation(EventBusListener.class);
                if (listener.type() == ListenerType.INSTANCE) {
                    Events.bus(listener.bus()).register(instance);
                    mod.instanceListeners.push(new ModListener(clazz, instance));
                }
            }
        }

        @Override
        public void dispatchEvent(Event event) {
            Events.bus(Bus.MOD).dispatch(event);
        }

        @Override
        public Directory getGameDirectory() {
            return MVEngine.instance().getGame().getGameDirectory();
        }

        @Override
        public Directory getModConfigDirectory() {
            return MVEngine.instance().getGame().getGameDirectory().getSubDirectory("config").getSubDirectory(id);
        }
    }
}
