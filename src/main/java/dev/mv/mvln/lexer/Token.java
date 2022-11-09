package dev.mv.mvln.lexer;

import lombok.Getter;

public class Token {
    @Getter
    private TokenType type;
    @Getter
    private String value, file;
    @Getter
    private int line, pos;

    public Token(TokenType type, String value, String file, int line, int pos) {
        this.type = type;
        this.value = value;
        this.file = file;
        this.line = line;
        this.pos = pos;
    }
}
