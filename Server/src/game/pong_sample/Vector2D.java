package game.pong_sample;

public final class Vector2D {

    private double x, y;

    /**
     * Should never be called, needed for serialisation of class.
     */
    public Vector2D() {
        this.x = 0;
	    this.y = 0;
    }

    public Vector2D(double x1, double y1, double x2, double y2) {
        this.x = x2 - x1;
	    this.y = y2 - y1;
    }

    public Vector2D(double x, double y) {
        this.x = x;
	    this.y = y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        if (x == 0 && y == 0) {
            return this;
        }
	    double magnitude = getMagnitude();
        return new Vector2D(x / magnitude, y / magnitude);
    }

    public double distance(Vector2D toPosition) {
        final double xDiff = getX() - toPosition.getX();
        final double yDiff = getY() - toPosition.getY();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public void add(Vector2D vector) {
	    x += vector.x;
	    y += vector.y;
    }

	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || o.getClass() != Vector2D.class)
            return false;

        final Vector2D vector2D = (Vector2D) o;

        return Double.valueOf(x).equals(Double.valueOf(vector2D.x))
                && Double.valueOf(y).equals(Double.valueOf(vector2D.y));
    }

    @Override
    public int hashCode() {
        return Double.valueOf(x).hashCode() + Double.valueOf(y).hashCode();
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}