package game.board;

import game.util.Direction;

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
public class Coordinate {

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
	try {
	    Coordinate coor = (Coordinate) another;
	    if (x == coor.x && y == coor.y) {
		return true;
	    }
	    return false;
	} catch (Exception e) {
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

}
