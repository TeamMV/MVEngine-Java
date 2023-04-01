package dev.mv.engine.logic.easing;

public abstract class Easing {
    protected float start, end, from, to;

    public Easing(float start, float end, float from, float to) {
        this.start = start;
        this.end = end;
        this.from = from;
        this.to = to;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public void set(float start, float end, float from, float to) {
        this.start = start;
        this.end = end;
        this.from = from;
        this.to = to;
    }

    public abstract float get(float pos);

    public float[] simulate(int steps) {
        float[] ret = new float[steps];
        float step = (end - steps) / steps;
        int count = 0;
        for (float i = start; i < end; i += step) {
            ret[count++] = get(i);
        }
        return ret;
    }
}
