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

    private static int getApproxDistance(final int x1, final int y1, final int x2, final int y2) {
        int max = Math.abs(x1 - x2);
        int min = Math.abs(y1 - y2);
        if (max < min) {
            final int temp = min;
            min = max;
            max = temp;
        }

        if (min <= (max >> 2)) {
            return max;
        }

        final int minCalc = (3 * min) >> 3;
        return (minCalc >> 5) + minCalc + max - (max >> 4) - (max >> 6);
    }

    public int getApproxDistance(final Point point) {
        return getApproxDistance(x, y, point.x, point.y);
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
