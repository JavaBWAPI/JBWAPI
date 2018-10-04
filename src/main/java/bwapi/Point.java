package bwapi;

class Point {
    static final int TILE_WALK_FACTOR = 4; // 32 / 8

    public final int x;
    public final int y;
    private int scalar;

    Point(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.scalar = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return scalar == point.scalar && x == point.x && y == point.y;
    }

    /**
     * Check if the current point is a valid point for the current game
     */
    public boolean isValid(final Game game) {
        return x >= 0 && y >= 0 &&
                scalar * x < game.mapPixelWidth() &&
                scalar * y < game.mapPixelHeight();
    }

    public int hashCode() {
        //alternatively return Objects.hash(x, y); ?
        return (x << 16) + y;
    }
}
