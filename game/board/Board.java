package game.board;

import game.unit.Unit;
import game.util.InvalidCoordinateException;

//simply to hold information about the board and the pieces on it
public abstract class Board {
	// always x, y to access elements
	// use Coordinate{x,y} to represent coordinates
	protected final Square[][] grid;

	public Board() {
		grid = new Square[getWidth()][getHeight()];

		for (byte x = 0; x < getWidth(); x++) {
			for (byte y = 0; y < getHeight(); y++) {
				Coordinate coor = new Coordinate(x, y);
				try {
					checkCoordinateRange(coor);
					grid[x][y] = new Square(coor);
				} catch (InvalidCoordinateException e) {
					grid[x][y] = null;
				}
			}
		}
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract boolean isInBoard(Coordinate coor);

	// TODO public? private?
	// TODO go through everything in game package to see visibility
	final void checkCoordinateRange(Coordinate coor) {
		if (coor == null || !isInBoard(coor))
			throw new InvalidCoordinateException(coor);
	}

	public Square getSquare(Coordinate coor) {
		checkCoordinateRange(coor);
		return grid[coor.x()][coor.y()];
	}

	public Unit getUnitAt(Coordinate coor) {
		checkCoordinateRange(coor);
		return grid[coor.x()][coor.y()].getUnitOnTop();
	}

	public void placeUnit(Unit unit){
		checkCoordinateRange(unit.getCoor());
		grid[unit.getCoor().x()][unit.getCoor().y()].setUnitOnTop(unit);
	}

}
