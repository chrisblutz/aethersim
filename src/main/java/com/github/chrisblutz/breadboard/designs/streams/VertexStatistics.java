package com.github.chrisblutz.breadboard.designs.streams;

import com.github.chrisblutz.breadboard.designs.Vertex;

import java.util.stream.Collector;

public class VertexStatistics {

    private int minimumX = Integer.MAX_VALUE, maximumX = Integer.MIN_VALUE;
    private int minimumY = Integer.MAX_VALUE, maximumY = Integer.MIN_VALUE;

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

    protected void accept(Vertex vertex) {
        minimumX = Math.min(minimumX, vertex.getX());
        maximumX = Math.max(maximumX, vertex.getX());
        minimumY = Math.min(minimumY, vertex.getY());
        maximumY = Math.max(maximumY, vertex.getY());
    }

    public VertexStatistics combine(VertexStatistics... others) {
        // Update the statistics in this object, then return it
        for (VertexStatistics other : others) {
            minimumX = Math.min(minimumX, other.minimumX);
            maximumX = Math.max(maximumX, other.maximumX);
            minimumY = Math.min(minimumY, other.minimumY);
            maximumY = Math.max(maximumY, other.maximumY);
        }

        return this;
    }

    public static Collector<Vertex, VertexStatistics, VertexStatistics> collector() {
        return Collector.of(
                VertexStatistics::new,
                VertexStatistics::accept,
                VertexStatistics::combine,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }
}
