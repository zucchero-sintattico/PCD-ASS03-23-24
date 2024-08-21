package org.src.common;

import java.io.Serializable;

public class Point2d implements Serializable {
    private final int x;
    private final int y;

    public Point2d(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x and y must be positive");
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
