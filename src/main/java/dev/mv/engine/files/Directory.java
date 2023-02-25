package dev.mv.engine.files;

import dev.mv.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Directory {

    @Getter
    private String name;
    private File folder;
    private String path;

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
        return new InnerDirectory(name, new File(Utils.getPath(path, name)));
    }

    public File getFile(String name) {
        return new File(Utils.getPath(path, name));
    }

    public byte[] getFileAsBytes(String name) throws IOException {
        return Files.readAllBytes(new File(Utils.getPath(path, name)).toPath());
    }

    public <T> T getFileAsObject(String name, ObjectSaver<T> saver) throws IOException {
        return saver.load(getFileAsBytes(name));
    }

    public <T> T getFileAsObject(String name, ObjectDirectorySaver<T> saver) {
        return saver.load(getSubDirectory(name));
    }

    public ConfigFile getConfigFile(String name) {
        try {
            return new ConfigFile(name, this, getFileAsBytes(name));
        } catch (IOException e) {
            return new ConfigFile(name, this, new byte[0]);
        }
    }

    public void saveFileBytes(String name, byte[] bytes) throws IOException {
        Files.write(new File(Utils.getPath(path, name)).toPath(), bytes);
    }

    public <T> void saveFileObject(String name, T object, ObjectSaver<T> saver) throws IOException {
        saveFileBytes(name, saver.save(object));
    }

    public <T> void saveFileObject(String name, T object, ObjectDirectorySaver<T> saver) {
        saver.save(getSubDirectory(name), object);
    }

    public File asFile() {
        return folder;
    }

}
