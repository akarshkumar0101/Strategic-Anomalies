package game.board;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.Game;
import game.Player;
import game.unit.Unit;
import setup.Position;
import setup.SetupTemplate;

/**
 * Board holds squares that can hold pieces on them. Contains information about
 * pieces locations.
 *
 * @author Akarsh
 *
 */
public abstract class Board implements Iterable<Square> {
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

    public abstract Coordinate transformCoordinateForOtherPlayer(Coordinate coor);

    public abstract Direction transformDirectionForOtherPlayer(Direction dir);

    public abstract Position createHomePosition(Position templatePos);

    public abstract Position createAwayPosition(Position templatePos);

    // TODO public? private?
    // TODO go through everything in game package to see visibility
    private final void checkCoordinateRange(Coordinate coor) {
	if (coor == null || !isInBoard(coor)) {
	    throw new RuntimeException("Invalid Coordinate: " + coor);
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
	Coordinate coor = unit.getPosProp().getValue();
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
	unit.getDeathReporter().add(specifications -> getSquare(unit.getPosProp().getValue()).removeUnitOnTop());
    }

    public void linkBoardToUnits(List<Unit> units) {
	for (Unit unit : units) {
	    linkBoardToUnit(unit);
	}
    }

    public void setupBoard(Game game, Player player1, Player player2, SetupTemplate homeTemp, SetupTemplate awayTemp) {
	SetupTemplate.setupBoardWithTemplates(this, game, player1, player2, homeTemp, awayTemp);
    }

    public List<Square> squaresInRange(Square sqr, int range) {
	List<Square> squares = new ArrayList<>(2 * range * (range + 1));
	for (int x = sqr.getCoor().x() - range; x <= sqr.getCoor().x() + range; x++) {
	    for (int y = sqr.getCoor().y() - range; y <= sqr.getCoor().y() + range; y++) {
		Coordinate coor = new Coordinate(x, y);
		if (isInBoard(coor) && Board.walkDist(sqr.getCoor(), coor) <= range) {
		    squares.add(getSquare(coor));
		}
	    }
	}
	return squares;
    }

    public Square locationOf(Unit unit) {
	if (unit == null) {
	    throw new IllegalArgumentException("Unit is null");
	}
	for (Square sqr : this) {
	    if (sqr.getUnitOnTop() == null) {
		continue;
	    }
	    if (unit.equals(sqr.getUnitOnTop())) {
		return sqr;
	    }

	}
	return null;
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

    @Override
    public Iterator<Square> iterator() {
	return new BoardIterator();
    }

    private class BoardIterator implements Iterator<Square> {

	boolean hasNext;
	private final int[] currentCoor;

	public BoardIterator() {
	    hasNext = true;
	    currentCoor = new int[] { -1, 0 };
	    findNext();
	}

	@Override
	public boolean hasNext() {
	    return hasNext;
	}

	@Override
	public Square next() {
	    Square sqr = grid[currentCoor[0]][currentCoor[1]];
	    if (sqr != null) {
		findNext();
		return sqr;
	    }

	    throw new RuntimeException("Nothing left on the board");
	}

	private void findNext() {
	    int y = currentCoor[1];
	    for (int x = currentCoor[0] + 1; true; x++) {
		if (x == getWidth()) {
		    x = 0;
		    y++;
		    if (y == getHeight()) {
			hasNext = false;
			return;
		    }
		}
		if (grid[x][y] != null) {
		    currentCoor[0] = x;
		    currentCoor[1] = y;
		    return;
		}
	    }

	}
    }
    // this should sync
}
