package com.github.chrisblutz.breadboard.designs;

import java.util.Objects;

public class Vertex {

    private int x;
    private int y;

    public Vertex() {}

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vertex withOffset(int xOffset, int yOffset) {
        return new Vertex(getX() + xOffset, getY() + yOffset);
    }

    public Vertex withOffset(Vertex offset) {
        return withOffset(offset.getX(), offset.getY());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Vertex vertex = (Vertex) other;
        return x == vertex.x && y == vertex.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
