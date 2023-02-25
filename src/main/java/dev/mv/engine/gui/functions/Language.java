package dev.mv.engine.gui.functions;

public enum Language {
    JAVA("JAVA"),
    PYTHON("PYTHON"),
    JAVASCRIPT("JAVASCRIPT"),
    TYPESCRIPT("TYPESCRIPT"),
    CSHARP("CSHAPR"),
    C("C"),
    CPP("CPP");

    Language(String lang) {
    }

    public static Language fromString(String lang) {
        if (lang.isBlank()) return JAVA;
        lang = lang.toUpperCase();
        return Language.valueOf(lang);
    }

}
