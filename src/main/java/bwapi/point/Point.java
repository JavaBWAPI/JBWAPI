package bwapi.point;

public abstract class Point {
    static final int TILE_WALK_FACTOR = 4; // 32 / 4

    protected int x, y;
	private int scalar;

	Point(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.scalar = type;
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	public boolean equals(Object o) {
		if (!(o instanceof Point)) return false;
		Point point = (Position) o;
		return scalar == point.scalar && x == point.x && y == point.y;
	}

	public int hashCode() {
	    //alternatively return Objects.hash(x, y); ?
		return (x << 16) + y;
	}
}
