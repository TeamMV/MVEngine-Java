package dev.mv.engine.game.language;

import dev.mv.engine.MVEngine;
import dev.mv.utils.Utils;
import dev.mv.utils.buffer.DynamicCharBuffer;
import dev.mv.utils.collection.Vec;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Languages {

    private static Map<String, Language> languages = new HashMap<>();
    private static Language currentLanguage;

    public static Vec<String> scanLanguages(String id) {
        InputStream stream = Languages.class.getResourceAsStream("/assets/" + id + "/lang");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return Utils.fastIter(reader.lines().toList()).filter(s -> s.matches("[a-zA-Z_]+.((json)|(lang))]")).map(s -> "/assets/" + id + "/lang/" + s).collect();
    }

    public static void init(Vec<String> foundLanguages, String defaultLanguage) {
        foundLanguages.forEach(lang -> addLanguage(load("/assets/" + MVEngine.instance().getGame().getGameId() + "/lang/" + lang + ".json")));
        currentLanguage = getLanguage(defaultLanguage);
        if (currentLanguage == null && foundLanguages.len() > 0)
            currentLanguage = getLanguage(foundLanguages.get(0));
    }

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    public static Language getLanguage(String code) {
        return languages.get(code);
    }

    public static void addLanguage(Language language) {
        if (language == null) return;
        if (languages.containsKey(language.getCode())) {
            languages.get(language.getCode()).inject(language);
        } else {
            languages.put(language.getCode(), language);
        }
    }

    public static Language load(String path) {
        return load(Languages.class.getResourceAsStream(path));
    }

    public static Language load(InputStream stream) {
        String contents;
        try {
            contents = new String(stream.readAllBytes()).replaceAll("\\n", "").replaceAll("\\s+", "");
        } catch (Exception e) {
            return null;
        }

        DynamicCharBuffer buffer = new DynamicCharBuffer(contents);
        LanguageInfo info = new LanguageInfo();

        try {
            parse(buffer, "", info);
        } catch (Exception e) {
            return null;
        }

        if (info.code == null) return null;

        return new BasicLanguage(info.map, info.leftToRight, info.name, info.region, info.code);
    }

    private static void parse(DynamicCharBuffer buffer, String path, LanguageInfo info) {
        buffer.pop();
        while (buffer.peek() != '}') {
            if (buffer.peek() == '"') {
                String key = getString(buffer);
                if (buffer.pop() == ':') {
                    if (buffer.peek() == '"') {
                        String value = getString(buffer);
                        info.map.put(path + key, value);
                        if (buffer.peek() == ',') {
                            buffer.pop();
                        }
                    } else if (buffer.peek() == '{') {
                        if (key.equals("language")) {
                            parseInfo(buffer, info);
                        } else {
                            parse(buffer, path + key + ".", info);
                        }
                        if (buffer.peek() == ',') {
                            buffer.pop();
                        }
                    } else {
                        throw new RuntimeException("Malformed JSON!");
                    }
                } else {
                    throw new RuntimeException("Malformed JSON!");
                }
            } else {
                throw new RuntimeException("Malformed JSON!");
            }
        }
    }

    private static void parseInfo(DynamicCharBuffer buffer, LanguageInfo info) {
        buffer.pop();
        while (buffer.peek() != '}') {
            if (buffer.peek() == '"') {
                String key = getString(buffer);
                if (buffer.pop() == ':') {
                    if (buffer.peek() == '"') {
                        String value = getString(buffer);
                        switch (key) {
                            case "name" -> info.name = value;
                            case "region" -> info.region = value;
                            case "code" -> info.code = value;
                        }
                        if (buffer.peek() == ',') {
                            buffer.pop();
                        }
                    } else if (buffer.peek() == '{') {
                        parse(buffer, "language." + key + ".", info);
                        if (buffer.peek() == ',') {
                            buffer.pop();
                        }
                    } else {
                        int len = 0;
                        while (!Utils.isAnyOf(buffer.peek(len + 1)[len], ',', '}')) len++;
                        String str = new String(buffer.pop(len));
                        boolean b = str.equals("true") || str.equals("1") || str.equals("1b");
                        if (key.equals("leftToRight")) {
                            info.leftToRight = b;
                        } else if (key.equals("rightToLeft")) {
                            info.leftToRight = !b;
                        }
                        if (buffer.peek() == ',') {
                            buffer.pop();
                        }
                    }
                } else {
                    throw new RuntimeException("Malformed JSON!");
                }
            } else {
                throw new RuntimeException("Malformed JSON!");
            }
        }
    }

    private static String getString(DynamicCharBuffer buffer) {
        buffer.pop();
        String str = new String(buffer.pop(nextString(buffer))).replaceAll("\\\\\"", "\"");
        buffer.pop();
        return str;
    }

    private static int nextString(DynamicCharBuffer buffer) {
        int len = 0;
        while (true) {
            char[] popped = buffer.pop(len + 1);
            if (popped[popped.length - 1] == '"') {
                if (popped.length == 1) return len;
                if (popped[popped.length - 2] != '\\') return len;
            }
            len++;
        }
    }

    private static class LanguageInfo {
        Map<String, String> map = new HashMap<>();
        boolean leftToRight = true;
        String name = "";
        String region = "";
        String code = "";
    }

}
