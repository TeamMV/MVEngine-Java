package dev.mv.mvln.lexer;

import dev.mv.mvln.errors.SyntaxError;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    @SneakyThrows
    public static TokenStream tokenize(String fileName) {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        List<Token> tokens = new ArrayList<>();
        String line;
        int lineNumber = 1;
        boolean inComment = false;
        boolean inStr = false;
        String buffer = "";
        int lineBuffer = 0;
        int posBuffer = 0;
        while ((line = reader.readLine()) != null) {
            char[] chars = line.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (inComment) {
                    if (chars.length >= i + 2) {
                        if (chars[i] == '*' && chars[i + 1] == '/') {
                            inComment = false;
                            i += 2;
                        }
                    }
                }
                else if (inStr) {
                    boolean shouldExit = false;
                    while (!shouldExit) {
                        buffer += chars[i];
                        if (i + 1 == chars.length) {
                            shouldExit = true;
                        }
                        else if (chars[i + 1] == '`') {
                            i++;
                            shouldExit = true;
                        }
                        else {
                            i++;
                        }
                    }
                    if (chars[i] == '`') {
                        inStr = false;
                        tokens.add(new Token(TokenType.ML_STRING, buffer, fileName, lineBuffer, posBuffer));
                    }
                    else {
                        buffer += "\n";
                    }
                }
                else if (chars[i] == '`') {
                    inStr = true;
                    buffer = "";
                    posBuffer = i - 1;
                    lineBuffer = lineNumber;
                }
                else if (chars[i] == '"') {
                    i++;
                    buffer = "";
                    posBuffer = i - 2;
                    while (chars[i] != '"') {
                        buffer += chars[i];
                        i++;
                    }
                    tokens.add(new Token(TokenType.STRING, buffer, fileName, lineNumber, posBuffer));
                }
                else if (chars[i] == '\'') {
                    i++;
                    tokens.add(new Token(TokenType.CHAR, chars[i] + "", fileName, lineNumber, i - 2));
                    i++;
                    if (chars[i] != '\'') {
                        throw new IndexOutOfBoundsException("Bound for char is 1!");
                    }
                }
                else if (Character.isWhitespace(chars[i])) {}
                else if (Character.isAlphabetic(chars[i])) {
                    String id = "";
                    int pos = i + 1;
                    while (Character.isAlphabetic(chars[i]) || Character.isDigit(chars[i]) || chars[i] == '_') {
                        id += chars[i];
                        if (i + 2 < chars.length) {
                            i++;
                        }
                        else {
                            i++;
                            break;
                        }
                    }
                    i--;
                    tokens.add(new Token(TokenType.fromSymbol(id), id, fileName, lineNumber, pos));
                }
                else if (Character.isDigit(chars[i])) {
                    String num = "";
                    int pos = i + 1;
                    if (chars[i] == '0' && (chars[i + 1] == 'x' || chars[i + 1] == 'b')) {
                        num += '0';
                        num += chars[i + 1];
                        i += 2;
                    }
                    boolean fl = false;
                    while (Character.isDigit(chars[i]) || chars[i] == '_' || chars[i] == '.') {
                        if (chars[i] != '_') {
                            num += chars[i];
                        }
                        else if (chars[i] == '.') {
                            if (!fl) {
                                fl = true;
                                num += ".";
                            }
                            else {
                                break;
                            }
                        }
                        if (i + 2 < chars.length) {
                            i++;
                        }
                        else {
                            break;
                        }
                    }
                    tokens.add(new Token(fl ? TokenType.FLOAT : TokenType.INTEGER, num, fileName, lineNumber, pos));
                }
                else {
                    if (chars.length >= i + 2) {
                        String token = "" + chars[i] + chars[i + 1];
                        if (token.equals("//")) {
                            break;
                        }
                        else if (token.equals("/*")) {
                            inComment = true;
                            i++;
                            continue;
                        }

                        TokenType type = TokenType.fromSymbol(token);
                        if (type != TokenType.IDENTIFIER) {
                            tokens.add(new Token(type, token, fileName, lineNumber, i + 1));
                            i++;
                        }
                        else {
                            type = TokenType.fromSymbol(chars[i] + "");
                            if (type == TokenType.IDENTIFIER) {
                                throw new SyntaxError("Unknown symbol '" + chars[i] + "' at line " + lineNumber + ":" + (i - 1) + " in " + fileName);
                            }
                            tokens.add(new Token(type, chars[i] + "", fileName, lineNumber, i + 1));
                        }
                    }
                    else {
                        TokenType type = TokenType.fromSymbol(chars[i] + "");
                        if (type == TokenType.IDENTIFIER) {
                            throw new SyntaxError("Unknown symbol '" + chars[i] + "' at line " + lineNumber + ":" + (i - 1) + " in " + fileName);
                        }
                        tokens.add(new Token(type, chars[i] + "", fileName, lineNumber, i + 1));
                    }
                }
            }
            lineNumber++;
        }
        tokens.add(new Token(TokenType.EOF, "", fileName, lineNumber, 0));
        return new TokenStream(tokens);
    }

}