package dev.mv.engine.files;

import dev.mv.utils.Utils;
import dev.mv.utils.buffer.DynamicByteBuffer;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFile {

    private String name;
    private Directory directory;

    private Map<String, String> strings = new HashMap<>();
    private Map<String, Integer> integers = new HashMap<>();
    private Map<String, Float> floats = new HashMap<>();
    private Map<String, Long> longs = new HashMap<>();
    private Map<String, Double> doubles = new HashMap<>();
    private Map<String, Boolean> booleans = new HashMap<>();
    private Map<String, byte[]> bytes = new HashMap<>();

    ConfigFile(String name, Directory directory) {
        this.name = name;
        this.directory = directory;
    }

    public ConfigFile load() {
        byte[] bytes;
        try {
            bytes = directory.getFileAsBytes(name);
        } catch (IOException e) {
            clear();
            return this;
        }

        return this;
    }

    @SneakyThrows
    public void save() {
        DynamicByteBuffer buffer = new DynamicByteBuffer();

        buffer.pushRaw(".MVCONFIG");

        saveStrings(buffer);
        saveNumbers(buffer, integers, new Integer[0]);
        saveNumbers(buffer, floats, new Float[0]);
        saveNumbers(buffer, longs, new Long[0]);
        saveNumbers(buffer, doubles, new Double[0]);
        saveBooleans(buffer);
        saveBytes(buffer);

        directory.saveFileBytes(name, null);
    }

    private void saveStrings(DynamicByteBuffer buffer) {
        String[] keys = strings.keySet().toArray(new String[0]);
        String[] values = strings.values().toArray(new String[0]);

        for (int i = 0; i < keys.length; i++) {
            buffer.push(keys[i]);
            buffer.push(values[i]);
        }

        buffer.push((byte) 0);
    }

    private <T extends Number> void saveNumbers(DynamicByteBuffer buffer, Map<String, T> numbers, T[] inst) {
        String[] keys = numbers.keySet().toArray(new String[0]);
        T[] values = numbers.values().toArray(inst);

        buffer.push(keys.length);

        for (int i = 0; i < keys.length; i++) {
            buffer.push(keys[i]);
        }

        buffer.push(values);
        buffer.push((byte) 0);
    }

    private void saveBooleans(DynamicByteBuffer buffer) {
        String[] keys = booleans.keySet().toArray(new String[0]);
        Boolean[] values = booleans.values().toArray(new Boolean[0]);

        buffer.push(keys.length);

        for (int i = 0; i < keys.length; i++) {
            buffer.push(keys[i]);
        }

        buffer.push(Utils.toPrimitive(values));
        buffer.push((byte) 0);
    }

    private void saveBytes(DynamicByteBuffer buffer) {
        String[] keys = bytes.keySet().toArray(new String[0]);
        byte[][] values = bytes.values().toArray(new byte[0][]);

        buffer.push(keys.length);

        for (int i = 0; i < keys.length; i++) {
            buffer.push(keys[i]);
        }

        for (int i = 0; i < values.length; i++) {
            buffer.push(values[i]);
            buffer.push((byte) 0);
        }
        buffer.push((byte) 0);
    }

    private void clear() {
        strings.clear();
        integers.clear();
        floats.clear();
        longs.clear();
        doubles.clear();
        booleans.clear();
        bytes.clear();
    }

}
