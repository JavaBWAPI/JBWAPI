package bwapi;

public abstract class Point<T extends Point<T>> implements Comparable<Point<T>> {
    static final int TILE_WALK_FACTOR = 4; // 32 / 8

    public final int x;
    public final int y;
    private final int scalar;

    protected Point(final int x, final int y, final int type) {
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

    public double getDistance(T point) {
        final int dx = point.x - this.x;
        final int dy = point.y - this.y;
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

    public int getApproxDistance(final T point) {
        return getApproxDistance(x, y, point.x, point.y);
    }

    public abstract T subtract(T other);

    public abstract T add(T other);

    public abstract T divide(int divisor);

    public abstract T multiply(int multiplier);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x &&
                y == point.y;
    }

    /**
     * Check if the current point is a valid point for the current game
     */
    public boolean isValid(final Game game) {
        return x >= 0 && y >= 0 &&
                scalar * x < game.mapPixelWidth() &&
                scalar * y < game.mapPixelHeight();
    }

    @Override
    public int hashCode() {
        return (x << 16) ^ y;
    }

    @Override
    public int compareTo(Point o) {
        if (scalar == o.scalar) {
            return hashCode() - o.hashCode();
        }
        return scalar - o.scalar;
    }
}
