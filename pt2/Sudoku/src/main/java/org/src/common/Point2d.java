package org.src.common;

public record Point2d(int x, int y) {
    public Point2d {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x and y must be positive");
        }
    }
}
