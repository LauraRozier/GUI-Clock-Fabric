package net.thibmorozier.guiclock.util;

public class ClockVector2i {
    private final int x;
    private final int y;

    public ClockVector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return String.format("{x: %d, y: %d}", x, y);
    }
}
