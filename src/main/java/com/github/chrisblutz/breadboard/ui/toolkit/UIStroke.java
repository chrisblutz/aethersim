package com.github.chrisblutz.breadboard.ui.toolkit;

import java.awt.*;

public class UIStroke {

    public enum Cap {
        BUTT, ROUND, SQUARE
    }

    public enum Join {
        BEVEL, MITER, ROUND
    }

    private final float width;
    private final Cap cap;
    private final Join join;
    private final float miterLimit;
    private final float[] dashPattern;
    private final float dashPhase;

    private UIStroke(float width, Cap cap, Join join, float miterLimit, float[] dashPattern, float dashPhase) {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterLimit = miterLimit;
        this.dashPattern = dashPattern;
        this.dashPhase = dashPhase;
    }

    Stroke getInternalStroke(float scale) {
        // Scale up dash pattern
        float[] scaledDashPattern;
        if (scale == 1d || dashPattern == null) {
            scaledDashPattern = dashPattern;
        }else {
            scaledDashPattern = new float[dashPattern.length];
            for (int index = 0; index < dashPattern.length; index++)
                scaledDashPattern[index] = scale * dashPattern[index];
        }

        int strokeCap;
        switch (cap) {
            case BUTT -> strokeCap = BasicStroke.CAP_BUTT;
            case ROUND -> strokeCap = BasicStroke.CAP_ROUND;
            default -> strokeCap = BasicStroke.CAP_SQUARE;
        }

        int strokeJoin;
        switch (join) {
            case BEVEL -> strokeJoin = BasicStroke.JOIN_BEVEL;
            case MITER -> strokeJoin = BasicStroke.JOIN_MITER;
            default -> strokeJoin = BasicStroke.JOIN_ROUND;
        }

        return new BasicStroke(
                scale * width,
                strokeCap,
                strokeJoin,
                miterLimit,
                scaledDashPattern,
                dashPhase
        );
    }

    public static UIStroke solid(float width) {
        return solid(width, Cap.SQUARE, Join.MITER);
    }

    public static UIStroke solid(float width, Cap cap, Join join) {
        return solid(width, cap, join, 10f);
    }

    public static UIStroke solid(float width, Cap cap, Join join, float miterLimit) {
        return new UIStroke(width, cap, join, miterLimit, null, 0f);
    }

    public static UIStroke dashed(float width, float[] dashPattern) {
        return dashed(width, dashPattern, 0f);
    }

    public static UIStroke dashed(float width, float[] dashPattern, float dashPhase) {
        return dashed(width, Cap.SQUARE, Join.MITER, dashPattern, dashPhase);
    }

    public static UIStroke dashed(float width, Cap cap, Join join, float[] dashPattern) {
        return dashed(width, cap, join, dashPattern, 0f);
    }

    public static UIStroke dashed(float width, Cap cap, Join join, float[] dashPattern, float dashPhase) {
        return dashed(width, cap, join, 10f, dashPattern, dashPhase);
    }

    public static UIStroke dashed(float width, Cap cap, Join join, float miterLimit, float[] dashPattern) {
        return dashed(width, cap, join, miterLimit, dashPattern, 0f);
    }

    public static UIStroke dashed(float width, Cap cap, Join join, float miterLimit, float[] dashPattern, float dashPhase) {
        return new UIStroke(width, cap, join, miterLimit, dashPattern, dashPhase);
    }
}
