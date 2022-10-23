package dev.mv.engine.oldRender.utils;

import lombok.Getter;
import lombok.Setter;

public class VariablePosition {

    @Getter
    @Setter
    private int x, y, width, height;
    private PositionCalculator pos;

    public VariablePosition(int width, int height, PositionCalculator pos) {
        this.pos = pos;
        resize(width, height);
    }

    public static VariablePosition fromScreenPosition(int x, int y, int width, int height, int sWidth, int sHeight) {
        return new VariablePosition(sWidth, sHeight, (w, h) ->
            new int[]{
                (int) (((float) x / (float) sWidth) * w),
                (int) (((float) y / (float) sHeight) * h),
                (int) (((float) width / (float) sWidth) * w),
                (int) (((float) height / (float) sHeight) * h)
            }
        );
    }

    public void resize(int width, int height) {
        int[] size = pos.resize(width, height);
        x = size[0];
        y = size[1];
        this.width = size[2];
        this.height = size[3];
    }

}