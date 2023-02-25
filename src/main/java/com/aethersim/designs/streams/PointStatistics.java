package com.aethersim.designs.streams;

import com.aethersim.designs.Point;

import java.util.function.Function;
import java.util.stream.Collector;

public class PointStatistics {

    private int minimumX = Integer.MAX_VALUE, maximumX = Integer.MIN_VALUE;
    private int minimumY = Integer.MAX_VALUE, maximumY = Integer.MIN_VALUE;

    private final Function<Point, Boolean> xDecider;
    private final Function<Point, Boolean> yDecider;

    public PointStatistics() {
        this(null, null);
    }

    public PointStatistics(Function<Point, Boolean> decider) {
        this(decider, decider);
    }

    public PointStatistics(Function<Point, Boolean> xDecider, Function<Point, Boolean> yDecider) {
        this.xDecider = xDecider;
        this.yDecider = yDecider;
    }

    public int getMinimumX() {
        return minimumX;
    }

    public int getMaximumX() {
        return maximumX;
    }

    public int getMinimumY() {
        return minimumY;
    }

    public int getMaximumY() {
        return maximumY;
    }

    protected void accept(Point point) {
        if (xDecider == null || xDecider.apply(point)) {
            minimumX = Math.min(minimumX, point.getX());
            maximumX = Math.max(maximumX, point.getX());
        }
        if (yDecider == null || yDecider.apply(point)) {
            minimumY = Math.min(minimumY, point.getY());
            maximumY = Math.max(maximumY, point.getY());
        }
    }

    public PointStatistics combine(PointStatistics... others) {
        // Update the statistics in this object, then return it
        for (PointStatistics other : others) {
            minimumX = Math.min(minimumX, other.minimumX);
            maximumX = Math.max(maximumX, other.maximumX);
            minimumY = Math.min(minimumY, other.minimumY);
            maximumY = Math.max(maximumY, other.maximumY);
        }

        return this;
    }

    public static Collector<Point, PointStatistics, PointStatistics> collector() {
        return Collector.of(
                PointStatistics::new,
                PointStatistics::accept,
                PointStatistics::combine,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static Collector<Point, PointStatistics, PointStatistics> collector(final Function<Point, Boolean> decider) {
        return Collector.of(
                () -> new PointStatistics(decider),
                PointStatistics::accept,
                PointStatistics::combine,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static Collector<Point, PointStatistics, PointStatistics> collector(final Function<Point, Boolean> xDecider, final Function<Point, Boolean> yDecider) {
        return Collector.of(
                () -> new PointStatistics(xDecider, yDecider),
                PointStatistics::accept,
                PointStatistics::combine,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }
}
