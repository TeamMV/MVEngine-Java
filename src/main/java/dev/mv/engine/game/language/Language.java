package dev.mv.engine.game.language;

import java.util.Map;

public interface Language {

    String translate(String code);

    boolean isLeftToRight();

    boolean has(String code);

    Map<String, String> toMap();

    void inject(Language language);

    String getName();

    String getRegion();

    String getCode();

}
