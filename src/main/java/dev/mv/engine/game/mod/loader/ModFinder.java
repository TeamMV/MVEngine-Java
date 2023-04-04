package dev.mv.engine.game.mod.loader;

import dev.mv.engine.MVEngine;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.files.Directory;
import dev.mv.engine.game.mod.api.Mod;
import dev.mv.utils.Utils;
import dev.mv.utils.collection.Vec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModFinder {
    private static final Vec<String> mods = new Vec<>();
    private static final Map<String, Vec<Class<?>>> specificModClasses = new HashMap<>();
    private static final Map<String, Vec<String>> specificModAssets = new HashMap<>();
    private static final Map<String, URL> modUrls = new HashMap<>();
    private static URLClassLoader modClassLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());

    public static void findMods(Directory modDir) {
        File[] files = modDir.asFile().listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) return;
        Vec<File> jarFiles = new Vec<>();
        for (File file : files) {
            try (JarFile jar = new JarFile(file)) {
                jarFiles.push(file);
            } catch (Exception ignore) {
            }
        }
        try {
            URL[] urls = jarFiles.fastIter().map(f -> {
                try {
                    return f.toURI().toURL();
                } catch (MalformedURLException e) {
                    Exceptions.send(e);
                    return null;
                }
            }).toArray();
            if (modClassLoader != null) {
                URL[] extraUrls = modClassLoader.getURLs();
                urls = Arrays.copyOf(urls, urls.length + extraUrls.length);
                System.arraycopy(extraUrls, 0, urls, urls.length - extraUrls.length, extraUrls.length);
            }
            modClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
            for (File file : jarFiles) {
                modUrls.put(load(file), file.toURI().toURL());
            }
        } catch (Exception ignore) {
        }
    }

    private static String load(File file) {
        try (JarFile jar = new JarFile(file)) {
            Vec<Class<?>> classes = new Vec<>();
            Vec<String> assets = new Vec<>();
            String id = null;
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                if (Utils.containsAny(entry.getName(), "META-INF", "module-info", "package-info")) continue;
                if (entry.getName().equals(JarFile.MANIFEST_NAME)) continue;
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
                    Class<?> clazz = modClassLoader.loadClass(className);
                    classes.push(clazz);
                    if (clazz.isAnnotationPresent(Mod.class)) {
                        Mod mod = clazz.getAnnotation(Mod.class);
                        id = mod.value();
                        if (mods.contains(id)) {
                            Exceptions.send("MOD_ID_DUPLICATE", id);
                        }
                        checkId(id);
                    }
                } else if (entry.getName().startsWith("assets/")) {
                    assets.push(entry.getName());
                }
            }
            if (id != null) {
                specificModClasses.put(id, classes);
                specificModAssets.put(id, assets);
                ModIntegration.addClasses(classes);
                ModIntegration.addAssets(assets);
                mods.push(id);
            }
            return id;
        } catch (ClassNotFoundException | IOException e) {
            Exceptions.send("MOD_LOAD_FAIL");
            return null;
        }
    }

    private static String loadExtra(File file) {
        try (JarFile jar = new JarFile(file)) {
            if (modClassLoader != null) {
                URL[] urls = modClassLoader.getURLs();
                urls = Arrays.copyOf(urls, urls.length + 1);
                urls[urls.length - 1] = file.toURI().toURL();
                modClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
            } else {
                modClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader());
            }
            String id = load(file);
            modUrls.put(id, file.toURI().toURL());
            return id;
        } catch (Exception e) {
            Exceptions.send("MOD_LOAD_EXTRA_FAIL");
            return null;
        }
    }

    private static void checkId(String id) {
        if (id.equals(MVEngine.instance().getGame().getGameId())) {
            Exceptions.send("MOD_ID_GAME");
        }
        if (!id.matches("[a-zA-Z0-9_\\-]+")) {
            Exceptions.send("MOD_ID_INVALID", id);
        }
    }

    static Vec<String> getMods() {
        return mods.clone();
    }

    static Vec<Class<?>> getModClasses(String mod) {
        return specificModClasses.get(mod).clone();
    }

    static Vec<String> getModAssets(String mod) {
        return specificModAssets.get(mod).clone();
    }

    static URL getModUrl(String mod) {
        return modUrls.get(mod);
    }

    static URLClassLoader getLoader() {
        return modClassLoader;
    }

    public static InputStream getModAsset(String name) {
        return modClassLoader.getResourceAsStream(name);
    }

}
