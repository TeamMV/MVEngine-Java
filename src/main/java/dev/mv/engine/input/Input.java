package dev.mv.engine.input;

import java.util.Arrays;

public class Input {
    public static State[] keys;
    public static State[] buttons;
    public static int[] mouse;

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
    public static int KEY_LAST = -1;

    public static final int BUTTON_LEFT = 0;
    public static final int BUTTON_MIDDLE = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_4 = 3;
    public static final int BUTTON_5 = 4;
    public static int BUTTON_LAST = -1;

    public static final int MOUSE_X = 0;
    public static final int MOUSE_Y = 1;
    public static final int MOUSE_SCROLL_X = 2;
    public static final int MOUSE_SCROLL_Y = 3;

    private static int totalNumKeys() {
        return 83 + 1;
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

    static void updateKey(int rawCode, InputCollector.KeyAction action) {
        try {
            if (action == InputCollector.KeyAction.PRESS || action == InputCollector.KeyAction.TYPE) {
                int convertedCode = convertKey(rawCode);
                if (!isKeyPressed(convertedCode)) {
                    keys[convertedCode] = State.ONPRESSED;
                }
                KEY_LAST = convertedCode;
            } else if (action == InputCollector.KeyAction.RELEASE) {
                int convertedCode = convertKey(rawCode);
                if (isKeyPressed(convertedCode)) {
                    keys[convertedCode] = State.ONRELEASED;
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException ignore) {}
    }

    static void updateButton(int btn, InputCollector.MouseAction action) {
        if(action == InputCollector.MouseAction.PRESS) {
            int convertedCode = convertButton(btn);
            if(!isButtonPressed(convertedCode)) {
                buttons[convertedCode] = State.ONPRESSED;
            }
            BUTTON_LAST = convertedCode;
        } else if(action == InputCollector.MouseAction.RELEASE) {
            int convertedCode = convertButton(btn);
            if(isButtonPressed(convertedCode)) {
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

    private static int convertKey(int rawCode) {
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
            case 97 -> KEY_A;
            case 98 -> KEY_B;
            case 99 -> KEY_C;
            case 100 -> KEY_D;
            case 101 -> KEY_E;
            case 102 -> KEY_F;
            case 103 -> KEY_G;
            case 104 -> KEY_H;
            case 105 -> KEY_I;
            case 106 -> KEY_J;
            case 107 -> KEY_K;
            case 108 -> KEY_L;
            case 109 -> KEY_M;
            case 110 -> KEY_N;
            case 111 -> KEY_O;
            case 112 -> KEY_P;
            case 113 -> KEY_Q;
            case 114 -> KEY_R;
            case 115 -> KEY_S;
            case 116 -> KEY_T;
            case 117 -> KEY_U;
            case 118 -> KEY_V;
            case 119 -> KEY_W;
            case 120 -> KEY_X;
            case 121 -> KEY_Y;
            case 122 -> KEY_Z;
            case 65513 -> KEY_ALT;
            case 65027 -> KEY_ALT_GR;
            case 65507 -> KEY_CTRL_LEFT;
            case 65508 -> KEY_CTRL_RIGHT;
            case 65505 -> KEY_SHIFT_LEFT;
            case 65506 -> KEY_SHIFT_RIGHT;
            case 65509 -> KEY_CAPS;
            case 65293 -> KEY_RETURN;
            case 65289 -> KEY_TAB;
            case 65362 -> KEY_ARROW_UP;
            case 65364 -> KEY_ARROW_DOWN;
            case 65361 -> KEY_ARROW_LEFT;
            case 65363 -> KEY_ARROW_RIGHT;
            case 65470 -> KEY_FN_1;
            case 65471 -> KEY_FN_2;
            case 65472 -> KEY_FN_3;
            case 65473 -> KEY_FN_4;
            case 65474 -> KEY_FN_5;
            case 65475 -> KEY_FN_6;
            case 65476 -> KEY_FN_7;
            case 65477 -> KEY_FN_8;
            case 65478 -> KEY_FN_9;
            case 65479 -> KEY_FN_10;
            case 65480 -> KEY_FN_11;
            case 65481 -> KEY_FN_12;
            //Following FN keys might be wrong...
            case 65482 -> KEY_FN_13;
            case 65483 -> KEY_FN_14;
            case 65484 -> KEY_FN_15;
            case 65485 -> KEY_FN_16;
            case 65486 -> KEY_FN_17;
            case 65487 -> KEY_FN_18;
            case 65488 -> KEY_FN_19;
            case 65489 -> KEY_FN_20;
            case 65535 -> KEY_DELETE;
            case 65379 -> KEY_INSERT;
            case 65307 -> KEY_ESCAPE;
            case 65407 -> KEY_NUM;
            case 65421 -> KEY_NUM_RETURN;
            case 65456 -> KEY_NUM_INSERT;
            case 65436 -> KEY_NUM_1;
            case 65433 -> KEY_NUM_2;
            case 65435 -> KEY_NUM_3;
            case 65430 -> KEY_NUM_4;
            case 65437 -> KEY_NUM_5;
            case 65432 -> KEY_NUM_6;
            case 65429 -> KEY_NUM_7;
            case 65431 -> KEY_NUM_8;
            case 65434 -> KEY_NUM_9;
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

    public enum State {
        PRESSED,
        ONPRESSED,
        ONRELEASED,
        RELEASED
    }
}
