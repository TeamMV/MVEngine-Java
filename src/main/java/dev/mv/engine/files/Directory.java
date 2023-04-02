package dev.mv.engine.files;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Directory {

    protected String name;
    protected File folder;
    protected String path;
    protected String relativePath = "/";

    protected Directory(String name) {
        this.name = name;
        folder = getFolder();
        path = folder.getAbsolutePath();
    }

    protected Directory(String name, File folder) {
        this.name = name;
        this.folder = folder;
        path = folder.getAbsolutePath();
    }

    protected abstract File getFolder();

    public Directory getSubDirectory(String name) {
        return new InnerDirectory(name, relativePath + name + "/", new File(Utils.getPath(path, name)));
    }

    public File getFile(String name) {
        File file = new File(Utils.getPath(path, name));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Exceptions.send(e);
            }
        }
        return file;
    }

    public byte[] getFileAsBytes(String name) throws IOException {
        return Files.readAllBytes(getFile(name).toPath());
    }

    public <T> T getFileAsObject(String name, Saver<T> saver) throws IOException {
        return saver.load(getFileAsBytes(name));
    }

    public <T> T getFileAsObject(String name, DirectorySaver<T> saver) {
        return saver.load(getSubDirectory(name));
    }

    public ConfigFile getConfigFile(String name) {
        return new ConfigFile(name, this).load();
    }

    public void saveFileBytes(String name, byte[] bytes) throws IOException {
        File file = new File(Utils.getPath(path, name));
        if (!file.exists()) file.createNewFile();
        Files.write(file.toPath(), bytes);
    }

    public <T> void saveFileObject(String name, T object, Saver<T> saver) throws IOException {
        saveFileBytes(name, saver.save(object));
    }

    public <T> void saveFileObject(String name, T object, DirectorySaver<T> saver) {
        saver.save(getSubDirectory(name), object);
    }

    public File asFile() {
        return folder;
    }

    public String getName() {
        return name;
    }

    public String getRelativePath() {
        return relativePath;
    }
}
