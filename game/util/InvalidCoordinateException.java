package game.util;

import game.board.Coordinate;

public class InvalidCoordinateException extends RuntimeException {

	private static final long serialVersionUID = -6037382486174350695L;

	private final Coordinate coor;

	public InvalidCoordinateException(Coordinate coor) {
		super("Invalid coordinate: "+coor);
		this.coor = coor;
	}

	public Coordinate getCoordinate() {
		return coor;
	}
}
