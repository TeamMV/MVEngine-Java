package dev.mv.engine.input;

import java.util.Arrays;

public class Input {
    public static final int KEY_0 = 0;
    public static final int KEY_1 = 1;
    public static final int KEY_2 = 2;
    public static final int KEY_3 = 3;
    public static final int KEY_4 = 4;
    public static final int KEY_5 = 5;
    public static final int KEY_6 = 6;
    public static final int KEY_7 = 7;
    public static final int KEY_8 = 8;
    public static final int KEY_9 = 9;
    public static final int KEY_A = 10;
    public static final int KEY_B = 11;
    public static final int KEY_C = 12;
    public static final int KEY_D = 13;
    public static final int KEY_E = 14;
    public static final int KEY_F = 15;
    public static final int KEY_G = 16;
    public static final int KEY_H = 17;
    public static final int KEY_I = 18;
    public static final int KEY_J = 19;
    public static final int KEY_K = 20;
    public static final int KEY_L = 21;
    public static final int KEY_M = 22;
    public static final int KEY_N = 23;
    public static final int KEY_O = 24;
    public static final int KEY_P = 25;
    public static final int KEY_Q = 26;
    public static final int KEY_R = 27;
    public static final int KEY_S = 28;
    public static final int KEY_T = 29;
    public static final int KEY_U = 30;
    public static final int KEY_V = 31;
    public static final int KEY_W = 32;
    public static final int KEY_X = 33;
    public static final int KEY_Y = 34;
    public static final int KEY_Z = 35;
    public static final int KEY_ALT = 36;
    public static final int KEY_ALT_GR = 37;
    public static final int KEY_CTRL_RIGHT = 38;
    public static final int KEY_CTRL_LEFT = 39;
    public static final int KEY_SHIFT_RIGHT = 40;
    public static final int KEY_SHIFT_LEFT = 41;
    public static final int KEY_CAPS = 42;
    public static final int KEY_RETURN = 43;
    public static final int KEY_TAB = 44;
    public static final int KEY_ARROW_UP = 45;
    public static final int KEY_ARROW_DOWN = 46;
    public static final int KEY_ARROW_LEFT = 47;
    public static final int KEY_ARROW_RIGHT = 48;
    public static final int KEY_FN_1 = 49;
    public static final int KEY_FN_2 = 50;
    public static final int KEY_FN_3 = 51;
    public static final int KEY_FN_4 = 52;
    public static final int KEY_FN_5 = 53;
    public static final int KEY_FN_6 = 54;
    public static final int KEY_FN_7 = 55;
    public static final int KEY_FN_8 = 56;
    public static final int KEY_FN_9 = 57;
    public static final int KEY_FN_10 = 58;
    public static final int KEY_FN_11 = 59;
    public static final int KEY_FN_12 = 60;
    public static final int KEY_FN_13 = 61;
    public static final int KEY_FN_14 = 62;
    public static final int KEY_FN_15 = 63;
    public static final int KEY_FN_16 = 64;
    public static final int KEY_FN_17 = 65;
    public static final int KEY_FN_18 = 66;
    public static final int KEY_FN_19 = 67;
    public static final int KEY_FN_20 = 68;
    public static final int KEY_DELETE = 69;
    public static final int KEY_INSERT = 70;
    public static final int KEY_ESCAPE = 71;
    public static final int KEY_NUM = 72;
    public static final int KEY_NUM_RETURN = 73;
    public static final int KEY_NUM_INSERT = 74;
    public static final int KEY_NUM_1 = 75;
    public static final int KEY_NUM_2 = 76;
    public static final int KEY_NUM_3 = 77;
    public static final int KEY_NUM_4 = 78;
    public static final int KEY_NUM_5 = 79;
    public static final int KEY_NUM_6 = 80;
    public static final int KEY_NUM_7 = 81;
    public static final int KEY_NUM_8 = 82;
    public static final int KEY_NUM_9 = 83;
    public static final int KEY_SPACE = 84;
    public static final int KEY_BACKSPACE = 85;
    public static final int KEY_PERIOD = 86;
    public static final int KEY_COMMA = 87;
    public static final int KEY_SLASH = 88;
    public static final int KEY_SEMICOLON = 89;
    public static final int KEY_EQUALS = 90;
    public static final int KEY_MINUS = 91;
    public static final int KEY_APOSTROPHE = 92;
    public static final int KEY_GRAVE = 93;
    public static final int KEY_LEFT_BRACKET = 94;
    public static final int KEY_RIGHT_BRACKET = 95;
    public static final int KEY_BACKSLASH = 96;
    public static final int KEY_EXTRA_1 = 97;
    public static final int KEY_EXTRA_2 = 98;
    public static final int BUTTON_LEFT = 0;
    public static final int BUTTON_MIDDLE = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_4 = 3;
    public static final int BUTTON_5 = 4;
    public static final int MOUSE_X = 0;
    public static final int MOUSE_Y = 1;
    public static final int MOUSE_SCROLL_X = 2;
    public static final int MOUSE_SCROLL_Y = 3;
    public static State[] keys;
    public static State[] buttons;
    public static int[] mouse;
    public static int KEY_LAST = -1;
    public static int BUTTON_LAST = -1;

    private Input() {
    }

    private static int totalNumKeys() {
        return 98 + 1;
    }

    private static int totalNumButtons() {
        return 4 + 1;
    }

    public static void init() {
        keys = new State[totalNumKeys()];
        buttons = new State[totalNumButtons()];
        mouse = new int[4];
        Arrays.fill(keys, State.ONRELEASED);
    }

    public static boolean isKeyPressed(int key) {
        try {
            return keys[key] == State.ONPRESSED || keys[key] == State.PRESSED;
        } catch (NullPointerException | IndexOutOfBoundsException ignore) {
            return false;
        }
    }

    public static boolean isButtonPressed(int btn) {
        return buttons[btn] == State.ONPRESSED || buttons[btn] == State.PRESSED;
    }

    public static boolean isShift() {
        return isKeyPressed(KEY_SHIFT_LEFT) || isKeyPressed(KEY_SHIFT_RIGHT);
    }

    public static boolean isControl() {
        return isKeyPressed(KEY_CTRL_LEFT) || isKeyPressed(KEY_CTRL_RIGHT);
    }

    public static boolean isAlt() {
        return isKeyPressed(KEY_ALT_GR) || isKeyPressed(KEY_ALT);
    }

    public static boolean isAltGr() {
        return isKeyPressed(KEY_ALT_GR);
    }

    public static boolean isModifier() {
        return isControl() || isShift() || isAlt() || isAltGr();
    }

    static void updateKey(int rawCode, InputCollector.KeyAction action) {
        try {
            if (action == InputCollector.KeyAction.TYPE) {
                int convertedCode = convertKey(rawCode);
                keys[convertedCode] = State.ONPRESSED;
                KEY_LAST = convertedCode;
            } else if (action == InputCollector.KeyAction.RELEASE) {
                int convertedCode = convertKey(rawCode);
                keys[convertedCode] = State.ONRELEASED;
            }
        } catch (NullPointerException | IndexOutOfBoundsException ignore) {
        }
    }

    static void charTyped(int charCode) {

    }

    static void updateButton(int btn, InputCollector.MouseAction action) {
        if (action == InputCollector.MouseAction.PRESS) {
            int convertedCode = convertButton(btn);
            if (!isButtonPressed(convertedCode)) {
                buttons[convertedCode] = State.ONPRESSED;
            }
            BUTTON_LAST = convertedCode;
        } else if (action == InputCollector.MouseAction.RELEASE) {
            int convertedCode = convertButton(btn);
            if (isButtonPressed(convertedCode)) {
                buttons[convertedCode] = State.ONRELEASED;
            }
        }
    }

    static void updateMouse(int x, int y) {
        mouse[MOUSE_X] = x;
        mouse[MOUSE_Y] = y;
    }

    static void updateMouseScroll(int sx, int sy) {
        mouse[MOUSE_SCROLL_X] = sx;
        mouse[MOUSE_SCROLL_Y] = sy;
    }

    public static int convertKey(int rawCode) {
        return switch (rawCode) {
            default -> -1;
            case 48 -> KEY_0;
            case 49 -> KEY_1;
            case 50 -> KEY_2;
            case 51 -> KEY_3;
            case 52 -> KEY_4;
            case 53 -> KEY_5;
            case 54 -> KEY_6;
            case 55 -> KEY_7;
            case 56 -> KEY_8;
            case 57 -> KEY_9;
            case 65 -> KEY_A;
            case 66 -> KEY_B;
            case 67 -> KEY_C;
            case 68 -> KEY_D;
            case 69 -> KEY_E;
            case 70 -> KEY_F;
            case 71 -> KEY_G;
            case 72 -> KEY_H;
            case 73 -> KEY_I;
            case 74 -> KEY_J;
            case 75 -> KEY_K;
            case 76 -> KEY_L;
            case 77 -> KEY_M;
            case 78 -> KEY_N;
            case 79 -> KEY_O;
            case 80 -> KEY_P;
            case 81 -> KEY_Q;
            case 82 -> KEY_R;
            case 83 -> KEY_S;
            case 84 -> KEY_T;
            case 85 -> KEY_U;
            case 86 -> KEY_V;
            case 87 -> KEY_W;
            case 88 -> KEY_X;
            case 89 -> KEY_Y;
            case 90 -> KEY_Z;
            case 342 -> KEY_ALT;
            case -1 -> KEY_ALT_GR;
            case 341 -> KEY_CTRL_LEFT;
            case 345 -> KEY_CTRL_RIGHT;
            case 340 -> KEY_SHIFT_LEFT;
            case 344 -> KEY_SHIFT_RIGHT;
            case 280 -> KEY_CAPS;
            case 335 -> KEY_RETURN;
            case 258 -> KEY_TAB;
            case 265 -> KEY_ARROW_UP;
            case 264 -> KEY_ARROW_DOWN;
            case 263 -> KEY_ARROW_LEFT;
            case 262 -> KEY_ARROW_RIGHT;
            case 290 -> KEY_FN_1;
            case 291 -> KEY_FN_2;
            case 292 -> KEY_FN_3;
            case 293 -> KEY_FN_4;
            case 294 -> KEY_FN_5;
            case 295 -> KEY_FN_6;
            case 296 -> KEY_FN_7;
            case 297 -> KEY_FN_8;
            case 298 -> KEY_FN_9;
            case 299 -> KEY_FN_10;
            case 300 -> KEY_FN_11;
            case 301 -> KEY_FN_12;
            case 302 -> KEY_FN_13;
            case 303 -> KEY_FN_14;
            case 304 -> KEY_FN_15;
            case 305 -> KEY_FN_16;
            case 306 -> KEY_FN_17;
            case 307 -> KEY_FN_18;
            case 308 -> KEY_FN_19;
            case 309 -> KEY_FN_20;
            case 261 -> KEY_DELETE;
            case 260 -> KEY_INSERT;
            case 256 -> KEY_ESCAPE;
            case 282 -> KEY_NUM;
            case -2 -> KEY_NUM_RETURN;
            case 320 -> KEY_NUM_INSERT;
            case 321 -> KEY_NUM_1;
            case 322 -> KEY_NUM_2;
            case 323 -> KEY_NUM_3;
            case 324 -> KEY_NUM_4;
            case 325 -> KEY_NUM_5;
            case 326 -> KEY_NUM_6;
            case 327 -> KEY_NUM_7;
            case 328 -> KEY_NUM_8;
            case 329 -> KEY_NUM_9;
            case 32 -> KEY_SPACE;
            case 259 -> KEY_BACKSPACE;
            case 46 -> KEY_PERIOD;
            case 44 -> KEY_COMMA;
            case 47 -> KEY_SLASH;
            case 45 -> KEY_MINUS;
            case 61 -> KEY_EQUALS;
            case 59 -> KEY_SEMICOLON;
            case 39 -> KEY_APOSTROPHE;
            case 91 -> KEY_LEFT_BRACKET;
            case 92 -> KEY_BACKSLASH;
            case 93 -> KEY_RIGHT_BRACKET;
            case 96 -> KEY_GRAVE;
            case 161 -> KEY_EXTRA_1;
            case 162 -> KEY_EXTRA_2;
        };
    }

    private static int convertButton(int btn) {
        return switch (btn) {
            case 0 -> BUTTON_LEFT;
            case 2 -> BUTTON_MIDDLE;
            case 1 -> BUTTON_RIGHT;
            case 3 -> BUTTON_4;
            case 4 -> BUTTON_5;
            default -> -1;
        };
    }

    public static String keyToStr(int key) {
        return switch (key) {
            default -> "-";
            case KEY_0 -> "0";
            case KEY_1 -> "1";
            case KEY_2 -> "2";
            case KEY_3 -> "3";
            case KEY_4 -> "4";
            case KEY_5 -> "5";
            case KEY_6 -> "6";
            case KEY_7 -> "7";
            case KEY_8 -> "8";
            case KEY_9 -> "9";
            case KEY_A -> "a";
            case KEY_B -> "b";
            case KEY_C -> "c";
            case KEY_D -> "d";
            case KEY_E -> "e";
            case KEY_F -> "f";
            case KEY_G -> "g";
            case KEY_H -> "h";
            case KEY_I -> "i";
            case KEY_J -> "j";
            case KEY_K -> "k";
            case KEY_L -> "l";
            case KEY_M -> "m";
            case KEY_N -> "n";
            case KEY_O -> "o";
            case KEY_P -> "p";
            case KEY_Q -> "q";
            case KEY_R -> "r";
            case KEY_S -> "s";
            case KEY_T -> "t";
            case KEY_U -> "u";
            case KEY_V -> "v";
            case KEY_W -> "w";
            case KEY_X -> "x";
            case KEY_Y -> "y";
            case KEY_Z -> "z";
            case KEY_RETURN -> "\n";
            case KEY_NUM_RETURN -> "\n";
            case KEY_SPACE -> " ";
            case KEY_PERIOD -> ".";
            case KEY_COMMA -> ",";
            case KEY_SLASH -> "/";
            case KEY_MINUS -> "-";
            case KEY_EQUALS -> "=";
            case KEY_SEMICOLON -> ";";
            case KEY_APOSTROPHE -> "'";
            case KEY_LEFT_BRACKET -> "[";
            case KEY_BACKSLASH -> "\\";
            case KEY_RIGHT_BRACKET -> "]";
            case KEY_GRAVE -> "`";
        };
    }

    public enum State {
        PRESSED,
        ONPRESSED,
        ONRELEASED,
        RELEASED
    }
}
