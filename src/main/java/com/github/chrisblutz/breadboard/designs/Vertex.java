package com.github.chrisblutz.breadboard.designs;

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
}
