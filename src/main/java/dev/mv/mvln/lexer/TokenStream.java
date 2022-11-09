package dev.mv.mvln.lexer;

import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

public class TokenStream {

    private List<Token> tokens;
    @Getter
    private int index = 0;
    private int length;
    private Token token;
    @Getter
    private boolean finished;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
        length = tokens.size();
        finished = length <= index + 1;
        if (!finished) {
            token = tokens.get(index);
        }
    }

    public Token getCurrentToken() {
        return token;
    }

    public TokenStream advance() {
        if (!finished) {
            index++;
            finished = length <= index + 1;
            token = tokens.get(index);
        }
        return this;
    }

    public Token getNextToken() {
        advance();
        return getCurrentToken();
    }

    public Token peek(int amount) {
        if (index + amount < length) {
            return tokens.get(index + amount);
        }
        return null;
    }

    public Stream<Token> stream() {
        return tokens.stream();
    }
}