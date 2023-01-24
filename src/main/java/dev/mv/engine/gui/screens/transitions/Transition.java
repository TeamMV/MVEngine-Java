package dev.mv.engine.gui.screens.transitions;

public class Transition {
    protected int xChange, yChange, scaleChange, rotationChange, alphaChange;
    protected boolean linear;

    public Transition(int xChange, int yChange, int scaleChange, int rotationChange, int alphaChange, boolean linear) {
        this.xChange = xChange;
        this.yChange = yChange;
        this.scaleChange = scaleChange;
        this.rotationChange = rotationChange;
        this.alphaChange = alphaChange;
        this.linear = linear;
    }

    public int getXChange() {
        return xChange;
    }

    public int getYChange() {
        return yChange;
    }

    public int getScaleChange() {
        return scaleChange;
    }

    public int getRotationChange() {
        return rotationChange;
    }

    public int getAlphaChange() {
        return alphaChange;
    }

    public boolean isLinear() {
        return linear;
    }
}
