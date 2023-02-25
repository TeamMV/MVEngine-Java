package dev.mv.engine.files;

import dev.mv.utils.Utils;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFile {

    private String name;
    private Directory directory;
    private byte[] rawData;

    private Map<String, String> strings = new HashMap<>();
    private Map<String, Integer> integers = new HashMap<>();
    private Map<String, Float> floats = new HashMap<>();
    private Map<String, Long> longs = new HashMap<>();
    private Map<String, Double> doubles = new HashMap<>();
    private Map<String, Boolean> booleans = new HashMap<>();

    ConfigFile(String name, Directory directory, byte[] bytes) {
        this.name = name;
        this.directory = directory;
        rawData = bytes;
    }

    @SneakyThrows
    public void reload() {
        byte[] bytes = directory.getFileAsBytes(name);
    }

    @SneakyThrows
    public void save() {
        directory.saveFileBytes(name, null);
    }

}
