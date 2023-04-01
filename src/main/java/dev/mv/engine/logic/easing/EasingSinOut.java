package dev.mv.engine.logic.easing;

public class EasingSinOut extends Easing {
    public EasingSinOut(float start, float end, float from, float to) {
        super(start, end, from, to);
    }

    @Override
    public float get(float pos) {
        return (float) (Math.sin((Math.PI * (pos - from)) / (2 * (to - from))) * (end - start) + start);
    }
}
