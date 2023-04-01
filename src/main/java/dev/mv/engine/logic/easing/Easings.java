package dev.mv.engine.logic.easing;

public final class Easings {
    private static final EasingLinear linear = new EasingLinear(0f, 0f, 0f, 0f);
    private static final EasingSinIn sinIn = new EasingSinIn(0f, 0f, 0f, 0f);
    private static final EasingSinOut sinOut = new EasingSinOut(0f, 0f, 0f, 0f);
    private static final EasingSin sin = new EasingSin(0f, 0f, 0f, 0f);

    public static float[] linear(float start, float end, float from, float to, int steps) {
        linear.set(start, end, from, to);
        return linear.simulate(steps);
    }

    public static float[] sinIn(float start, float end, float from, float to, int steps) {
        sinIn.set(start, end, from, to);
        return sinIn.simulate(steps);
    }

    public static float[] sinOut(float start, float end, float from, float to, int steps) {
        sinOut.set(start, end, from, to);
        return sinOut.simulate(steps);
    }

    public static float[] sin(float start, float end, float from, float to, int steps) {
        sin.set(start, end, from, to);
        return sin.simulate(steps);
    }
}
