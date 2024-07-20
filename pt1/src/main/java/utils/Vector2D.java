package utils;

import utils.Point2D;
public record Vector2D(double x, double y) {

    public static Vector2D makeV2d(Point2D from, Point2D to) {
        return new Vector2D(to.x() - from.x(), to.y() - from.y());
    }

    public Vector2D sum(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public double abs() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D getNormalized() {
        double module = Math.sqrt(x * x + y * y);
        return new Vector2D(x / module, y / module);
    }

    public Vector2D mul(double fact) {
        return new Vector2D(x * fact, y * fact);
    }

    public String toString() {
        return "V2d(" + x + "," + y + ")";
    }


}

