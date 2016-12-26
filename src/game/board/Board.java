package game.board;

import java.util.ArrayList;
import java.util.List;

import game.unit.Unit;
import game.util.InvalidCoordinateException;

/**
 * Board holds squares that can hold pieces on them. Contains information about
 * pieces locations.
 * 
 * @author Akarsh
 *
 */
public abstract class Board {
    /**
     * Grid of squares the board has. Uses (x, y) to access elements.
     */
    protected final Square[][] grid;

    /**
     * Initializes board with its subclasses' width and height.
     */
    public Board() {
	grid = new Square[getWidth()][getHeight()];

	for (byte x = 0; x < getWidth(); x++) {
	    for (byte y = 0; y < getHeight(); y++) {
		Coordinate coor = new Coordinate(x, y);
		if (isInBoard(coor)) {
		    grid[x][y] = new Square(coor);
		} else {
		    grid[x][y] = null;
		}
	    }
	}
    }

    /**
     * @return the width of the smallest rectangle the board will fit in.
     */
    public abstract int getWidth();

    /**
     * @return the height of the smallest rectangle the board will fit in.
     */
    public abstract int getHeight();

    /**
     * @param coor
     *            to check
     * @return true if the coordinate is in the board if the board is in an odd
     *         shape.
     */
    public abstract boolean isInBoard(Coordinate coor);

    // TODO public? private?
    // TODO go through everything in game package to see visibility
    final void checkCoordinateRange(Coordinate coor) {
	if (coor == null || !isInBoard(coor)) {
	    throw new InvalidCoordinateException(coor);
	}
    }

    /**
     * @param coor
     *            to use
     * @return the Square that is at the Coordinate
     */
    public Square getSquare(Coordinate coor) {
	checkCoordinateRange(coor);
	return grid[coor.x()][coor.y()];
    }

    /**
     * @param coor
     *            to use
     * @return the Unit that is on top of the Square at the Coordinate
     */
    public Unit getUnitAt(Coordinate coor) {
	checkCoordinateRange(coor);
	return grid[coor.x()][coor.y()].getUnitOnTop();
    }

    public void linkBoardToUnit(Unit unit) {
	Coordinate coor = unit.getPosProp().getCurrentPropertyValue();
	checkCoordinateRange(coor);
	getSquare(coor).setUnitOnTop(unit);

	unit.getPosProp().addPropertyListener((oldValue, newValue, unit1, property, specifications) -> {
	    checkCoordinateRange(oldValue);
	    checkCoordinateRange(newValue);

	    Square oldsqr = getSquare(oldValue);
	    Square newsqr = getSquare(newValue);
	    oldsqr.removeUnitOnTop();
	    newsqr.setUnitOnTop(unit1);
	});
	unit.getDeathReporter()
		.add(specifications -> getSquare(unit.getPosProp().getCurrentPropertyValue()).removeUnitOnTop());
    }

    public void linkBoardToUnits(List<Unit> units) {
	for (Unit unit : units) {
	    linkBoardToUnit(unit);
	}
    }

    public List<Square> squaresInRange(Square sqr, int range) {
	List<Square> squares = new ArrayList<>(range * range * 4);
	for (int x = sqr.getCoor().x() - range; x <= sqr.getCoor().x() + range; x++) {
	    for (int y = sqr.getCoor().y() - range; y <= sqr.getCoor().y() + range; y++) {
		Coordinate coor = new Coordinate(x, y);
		if (!(walkDist(sqr.getCoor(), coor) > range || !isInBoard(coor))) {
		    squares.add(getSquare(coor));
		}
	    }
	}
	return squares;
    }

    /**
     * @param coor1
     *            first Coordinate
     * @param coor2
     *            second Coordinate
     * @return the sum of the horizontal distance and the vertical distance of
     *         between the two Coordinates
     */
    public static int walkDist(Coordinate coor1, Coordinate coor2) {
	return Math.abs(coor2.x() - coor1.x()) + Math.abs(coor2.y() - coor1.y());
    }

    /**
     * @param coor1
     *            first Coordinate
     * @param coor2
     *            second Coordinate
     * @return the actual air distance between the two Coordinates
     */
    public static double absDist(Coordinate coor1, Coordinate coor2) {
	return Math.sqrt(Math.pow(Math.abs(coor2.x() - coor1.x()), 2) + Math.pow(Math.abs(coor2.y() - coor1.y()), 2));
    }

}
