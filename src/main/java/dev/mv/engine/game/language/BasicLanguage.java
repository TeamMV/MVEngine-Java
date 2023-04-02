package dev.mv.engine.game.language;

import dev.mv.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class BasicLanguage implements Language {

    private Map<String, String> map;
    private boolean leftToRight;
    private String name;
    private String region;
    private String code;

    BasicLanguage(Map<String, String> map, boolean leftToRight, String name, String region, String code) {
        this.map = map;
        this.leftToRight = leftToRight;
        this.name = name;
        this.region = region;
        this.code = code;
    }

    @Override
    public String translate(String code) {
        return Utils.ifNotNull(map.get(code)).thenReturn(s -> s).otherwiseReturn(code).getGenericReturnValue().value();
    }

    @Override
    public boolean isLeftToRight() {
        return leftToRight;
    }

    @Override
    public boolean has(String code) {
        return map.containsKey(code);
    }

    @Override
    public Map<String, String> toMap() {
        return (Map<String, String>) ((HashMap<String, String>) map).clone();
    }

    @Override
    public void inject(Language language) {
        map.putAll(language.toMap());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String getCode() {
        return code;
    }
}
