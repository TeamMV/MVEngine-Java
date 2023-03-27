package dev.mv.engine.files;

import dev.mv.utils.Utils;
import dev.mv.utils.buffer.DynamicByteBuffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static dev.mv.utils.Utils.toObject;

public class ConfigFile {

    private static final String FILE_CODE = ".MVECONFIG";

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

        DynamicByteBuffer buffer = new DynamicByteBuffer(bytes).flip();

        String code = buffer.popStringRaw(FILE_CODE.length());
        if (!code.equals(FILE_CODE)) {
            clear();
            return this;
        }

        loadStrings(buffer);
        loadValues(buffer, integers, (buf, len) -> toObject(buf.popInts(len)));
        loadValues(buffer, floats, (buf, len) -> toObject(buf.popFloats(len)));
        loadValues(buffer, longs, (buf, len) -> toObject(buf.popLongs(len)));
        loadValues(buffer, doubles, (buf, len) -> toObject(buf.popDoubles(len)));
        loadValues(buffer, booleans, (buf, len) -> {
            int byteAmount = (int) Math.ceil(len / 8f);
            Boolean[] ret = new Boolean[len + 8 - (len % 8)];
            for (int i = 0; i < byteAmount; i++) {
                System.arraycopy(toObject(buf.popBooleans()), 0, ret, i * 8, 8);
            }
            return ret;
        });
        loadBytes(buffer);

        return this;
    }

    private void loadStrings(DynamicByteBuffer buffer) {
        while (buffer.peek() != 0) {
            String keyStr = buffer.popEscapedString();
            String valueStr = buffer.popEscapedString();

            strings.put(keyStr, valueStr);
        }
        buffer.pop();
    }

    private <T> void loadValues(DynamicByteBuffer buffer, Map<String, T> map, BiFunction<DynamicByteBuffer, Integer, T[]> finder) {
        int len = buffer.popInt();
        if (len == 0) return;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = buffer.popEscapedString();
        }
        T[] values = finder.apply(buffer, len);
        for (int i = 0; i < len; i++) {
            map.put(keys[i], values[i]);
        }
    }

    private void loadBytes(DynamicByteBuffer buffer) {
        int len = buffer.popInt();
        if (len == 0) return;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = buffer.popEscapedString();
        }
        for (int i = 0; i < len; i++) {
            int arrLen = buffer.popInt();
            bytes.put(keys[i], buffer.pop(arrLen));
        }
    }

    public void save() {
        try {
            DynamicByteBuffer buffer = new DynamicByteBuffer();

            buffer.pushRaw(FILE_CODE);

            saveStrings(buffer);
            saveNumbers(buffer, integers, new Integer[0]);
            saveNumbers(buffer, floats, new Float[0]);
            saveNumbers(buffer, longs, new Long[0]);
            saveNumbers(buffer, doubles, new Double[0]);
            saveBooleans(buffer);
            saveBytes(buffer);

            directory.saveFileBytes(name, buffer.array());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        buffer.push(keys);
        buffer.push(values);
    }

    private void saveBooleans(DynamicByteBuffer buffer) {
        String[] keys = booleans.keySet().toArray(new String[0]);
        Boolean[] values = booleans.values().toArray(new Boolean[0]);

        buffer.push(keys.length);
        buffer.push(keys);
        buffer.push(Utils.toPrimitive(values));
    }

    private void saveBytes(DynamicByteBuffer buffer) {
        String[] keys = bytes.keySet().toArray(new String[0]);
        byte[][] values = bytes.values().toArray(new byte[0][]);

        buffer.push(keys.length);
        buffer.push(keys);

        for (int i = 0; i < values.length; i++) {
            buffer.push(values[i].length);
            buffer.push(values[i]);
        }
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

    public String getString(String name) {
        return strings.get(name);
    }

    public int getInt(String name) {
        try {
            return integers.get(name);
        } catch (Exception e) {
            return 0;
        }
    }

    public float getFloat(String name) {
        try {
            return floats.get(name);
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String name) {
        try {
            return longs.get(name);
        } catch (Exception e) {
            return 0;
        }
    }

    public double getDouble(String name) {
        try {
            return doubles.get(name);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean getBoolean(String name) {
        try {
            return booleans.get(name);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasInt(String name) {
        return integers.containsKey(name);
    }

    public boolean hasFloat(String name) {
        return floats.containsKey(name);
    }

    public boolean hasLong(String name) {
        return longs.containsKey(name);
    }

    public boolean hasDouble(String name) {
        return doubles.containsKey(name);
    }

    public boolean hasBoolean(String name) {
        return booleans.containsKey(name);
    }

    public boolean hasBytes(String name) {
        return bytes.containsKey(name);
    }

    public boolean hasString(String name) {
        return strings.containsKey(name);
    }

    public byte[] getBytes(String name) {
        return bytes.get(name);
    }

    public void setString(String name, String value) {
        strings.put(name, value);
    }

    public void setInt(String name, int value) {
        integers.put(name, value);
    }

    public void setFloat(String name, float value) {
        floats.put(name, value);
    }

    public void setLong(String name, long value) {
        longs.put(name, value);
    }

    public void setDouble(String name, double value) {
        doubles.put(name, value);
    }

    public void setBoolean(String name, boolean value) {
        booleans.put(name, value);
    }

    public void setBytes(String name, byte[] value) {
        bytes.put(name, value);
    }

    public void setIntIfAbsent(String name, int value) {
        if (!integers.containsKey(name)) {
            integers.put(name, value);
        }
    }

    public void setFloatIfAbsent(String name, float value) {
        if (!floats.containsKey(name)) {
            floats.put(name, value);
        }
    }

    public void setLongIfAbsent(String name, long value) {
        if (!longs.containsKey(name)) {
            longs.put(name, value);
        }
    }

    public void setDoubleIfAbsent(String name, double value) {
        if (!doubles.containsKey(name)) {
            doubles.put(name, value);
        }
    }

    public void setBooleanIfAbsent(String name, boolean value) {
        if (!booleans.containsKey(name)) {
            booleans.put(name, value);
        }
    }

    public void setBytesIfAbsent(String name, byte[] value) {
        if (!bytes.containsKey(name)) {
            bytes.put(name, value);
        }
    }

    public void setStringIfAbsent(String name, String value) {
        if (!strings.containsKey(name)) {
            strings.put(name, value);
        }
    }
}
