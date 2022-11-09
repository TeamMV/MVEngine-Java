package dev.mv.mvln.lexer;

public enum TokenType {
    IDENTIFIER(Tag.NAMED),
    ML_STRING(Tag.STRING),
    STRING(Tag.STRING),
    CHAR(Tag.STRING),
    INTEGER(Tag.NUMERIC),
    FLOAT(Tag.NUMERIC),
    TYPE("type"),
    RETURN("return"),
    BYTE_TYPE("byte"),
    CHAR_TYPE("char"),
    STRING_TYPE("string"),
    SHORT_TYPE("short"),
    INT_TYPE("int"),
    FLOAT_TYPE("float"),
    LONG_TYPE("long"),
    DOUBLE_TYPE("double"),
    VOID("void"),
    FOR("for"),
    FOREACH("foreach"),
    IN("in"),
    WHILE("while"),
    DO("do"),
    IMPORT("import"),
    IS("is"),
    AS("as"),
    FROM("from"),
    EXIT("exit"),
    CONTINUE("continue"),
    NATIVE("native"),
    ASYNC("async"),
    WAITFOR("waitfor"),
    OVERRIDE("override"),
    TASK("task"),
    HEAD("head"),
    BODY("body"),
    SEMICOLON(";"),
    COMMA(","),
    DOT("."),
    COLON(":"),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    PLUS("+"),
    INCREASE("++"),
    MINUS("-"),
    DECREASE("--"),
    DIVIDE("/"),
    MULTIPLY("*"),
    POWER("**"),
    MODULO("%"),
    EQUALS("="),
    DOUBLE_EQUALS("=="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_EQUALS(">="),
    LESS_EQUALS("<="),
    ELSE("else"),
    AND("&&"),
    OR("||"),
    BIT_OR("|"),
    XOR("^"),
    BIT_LEFT_SHIFT("<<"),
    BIT_RIGHT_SHIFT(">>"),
    NEGATE("!"),
    IF("if"),
    ELSE_IF("elif"),
    EXTENDS("pulls"),
    STATIC("static"),
    EOF("");

    private String name;
    private Tag tag;

    TokenType() {
        this(null, Tag.DEFAULT);
    }

    TokenType(String name) {
        this(name, Tag.DEFAULT);
    }

    TokenType(Tag tag) {
        this(null, tag);
    }

    TokenType(String name, Tag tag) {
        this.name = name;
        this.tag = tag;
    }

    public static TokenType fromSymbol(String symbol) {
        switch (symbol) {
            case "type": return TYPE;
            case "return": return RETURN;
            case "byte": return BYTE_TYPE;
            case "char": return CHAR_TYPE;
            case "string": return STRING_TYPE;
            case "short": return SHORT_TYPE;
            case "int": return INT_TYPE;
            case "float": return FLOAT_TYPE;
            case "long": return LONG_TYPE;
            case "double": return DOUBLE_TYPE;
            case "void": return VOID;
            case "for": return FOR;
            case "foreach": return FOREACH;
            case "in": return IN;
            case "while": return WHILE;
            case "do": return DO;
            case "import": return IMPORT;
            case "is": return IS;
            case "as": return AS;
            case "from": return FROM;
            case "exit": return EXIT;
            case "continue": return CONTINUE;
            case "native": return NATIVE;
            case "async": return ASYNC;
            case "waitfor": return WAITFOR;
            case "override": return OVERRIDE;
            case "task": return TASK;
            case "head": return HEAD;
            case "body": return BODY;
            case ";": return SEMICOLON;
            case ",": return COMMA;
            case ".": return DOT;
            case ":": return COLON;
            case "(": return LEFT_PAREN;
            case ")": return RIGHT_PAREN;
            case "[": return LEFT_BRACKET;
            case "]": return RIGHT_BRACKET;
            case "{": return LEFT_BRACE;
            case "}": return RIGHT_BRACE;
            case "+": return PLUS;
            case "++": return INCREASE;
            case "-": return MINUS;
            case "--": return DECREASE;
            case "/": return DIVIDE;
            case "*": return MULTIPLY;
            case "**": return POWER;
            case "%": return MODULO;
            case "=": return EQUALS;
            case "==": return DOUBLE_EQUALS;
            case ">": return GREATER_THAN;
            case "<": return LESS_THAN;
            case ">=": return GREATER_EQUALS;
            case "<=": return LESS_EQUALS;
            case "else": return ELSE;
            case "&&": return AND;
            case "||": return OR;
            case "|": return BIT_OR;
            case "^": return XOR;
            case "<<": return BIT_LEFT_SHIFT;
            case ">>": return BIT_RIGHT_SHIFT;
            case "!": return NEGATE;
            case "if": return IF;
            case "elif": return ELSE_IF;
            case "pulls": return EXTENDS;
            case "static": return STATIC;
            default: return IDENTIFIER;
        }
    }

    enum Tag {
        DEFAULT,
        NAMED,
        STRING,
        NUMERIC;
    }

}