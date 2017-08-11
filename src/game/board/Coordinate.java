package game.board;

import java.io.Serializable;

/**
 * Coordinate is used to track location on the board for pieces and squares.
 * Pieces implement this by having a CoordinateProperty that tracks Coordinate
 * changes. Coordinate is like an integer, not an object in that it is
 * disposable and cannot not be changed after it is made; its x and y properties
 * are permanent after creation.
 *
 * @author Akarsh
 *
 */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = 5265089525764467646L;

    /**
     * x and y information.
     */
    private final byte x, y;

    /**
     * Initializes coordinate with given values.
     *
     * @param x
     * @param y
     */
    public Coordinate(byte x, byte y) {
	this.x = x;
	this.y = y;
    }

    /**
     * Initializes coordinate with given values.
     *
     * @param x
     * @param y
     */
    public Coordinate(int x, int y) {
	this((byte) x, (byte) y);
    }

    public Coordinate(Coordinate another) {
	this(another.x, another.y);
    }

    /**
     * @return the x value of the Coordinate.
     */
    public byte x() {
	return x;
    }

    /**
     * @return the y value of the Coordinate.
     */
    public byte y() {
	return y;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object another) {
	if (!(another instanceof Coordinate)) {
	    return false;
	}
	Coordinate coor = (Coordinate) another;
	if (x == coor.x && y == coor.y) {
	    return true;
	} else {
	    return false;
	}
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "{" + x + ", " + y + "}";
    }

    /**
     * @param coor
     *            to shift
     * @param dir
     *            direction to shift it in
     * @return the Coordinate shifted one unit in the specified Direction
     */
    public static Coordinate shiftCoor(Coordinate coor, Direction dir) {
	if (dir == Direction.RIGHT || dir == Direction.LEFT) {
	    return new Coordinate(coor.x + dir.toInt(), coor.y);
	} else if (dir == Direction.UP || dir == Direction.DOWN) {
	    return new Coordinate(coor.x, coor.y + dir.toInt());
	} else {
	    return null;
	}
    }

    // coor2 is directly in one direction of coor1
    public static Direction inDirectDirection(Coordinate coor1, Coordinate coor2) {
	Direction dir = null;

	if (coor1.equals(coor2)) {
	    return dir;
	}

	int xdist = coor2.x() - coor1.x();
	int ydist = coor2.y() - coor1.y();

	if (xdist == 0) {
	    dir = ydist > 0 ? Direction.DOWN : Direction.UP;
	} else if (ydist == 0) {
	    dir = xdist > 0 ? Direction.RIGHT : Direction.UP;
	}
	return dir;

    }

    // coor2 is generally in one direction of coor1
    public static Direction inGeneralDirection(Coordinate coor1, Coordinate coor2) {
	Direction dir = null;
	int xdist = coor2.x() - coor1.x();
	int ydist = coor2.y() - coor1.y();
	if (Math.abs(xdist) > Math.abs(ydist)) {
	    dir = xdist > 0 ? Direction.RIGHT : Direction.LEFT;
	} else if (Math.abs(ydist) > Math.abs(xdist)) {
	    dir = ydist > 0 ? Direction.UP : Direction.DOWN;
	} else {
	    if (xdist > 0 && ydist > 0) {
		dir = Direction.UP;
	    } else if (xdist > 0 && ydist < 0) {
		dir = Direction.RIGHT;
	    } else if (xdist < 0 && ydist < 0) {
		dir = Direction.DOWN;
	    } else if (xdist < 0 && ydist > 0) {
		dir = Direction.LEFT;
	    }
	}
	return dir;
    }

}
