package dev.mv.engine.logic.easing;

public class EasingSin extends Easing {
    public EasingSin(float start, float end, float from, float to) {
        super(start, end, from, to);
    }

    @Override
    public float get(float pos) {
        return (float) ((Math.cos((Math.PI * (pos - from)) / (to - from) + Math.PI) + 1) * ((end - start) / 2) + start);

    }
}
