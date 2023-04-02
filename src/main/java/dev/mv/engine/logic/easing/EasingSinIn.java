package dev.mv.engine.logic.easing;

public class EasingSinIn extends Easing {


    public EasingSinIn(float start, float end, float from, float to) {
        super(start, end, from, to);
    }

    @Override
    public float get(float pos) {
        return (float) ((Math.cos((Math.PI * (pos - from)) / (2 * (to - from)) + Math.PI) + 1) * (end - start) + start);
    }
}
