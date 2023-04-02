package dev.mv.engine.logic.easing;

import dev.mv.utils.Utils;

public class EasingLinear extends Easing {
    public EasingLinear(float start, float end, float from, float to) {
        super(start, end, from, to);
    }

    @Override
    public float get(float pos) {
        return Utils.map(pos, from, to, start, end);
    }
}
