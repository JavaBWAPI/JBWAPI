package bwapi;

class Point {
    static final int TILE_WALK_FACTOR = 4; // 32 / 8

    public final int x;
    public final int y;
    private final int scalar;

    Point(final int x, final int y, final int type) {
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

    protected double getDistance(final int x, final int y) {
        final int dx = x - this.x;
        final int dy = y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean equals(final Object o) {
        if (o != null && this.getClass().equals(o.getClass())) {
            final Point point = (Point) o;
            return x == point.x && y == point.y;
        }
        return false;

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
        return (x << 16) ^ y;
    }
}
