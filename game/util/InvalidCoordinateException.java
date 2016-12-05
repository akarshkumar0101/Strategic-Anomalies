package game.util;

import game.board.Coordinate;

/**
 * The RuntimeException if a invalid coordinate is given to a Board.
 * 
 * @author Akarsh
 *
 */
public class InvalidCoordinateException extends RuntimeException {

	private static final long serialVersionUID = -6037382486174350695L;

	/**
	 * The invalid Coordinate.
	 */
	private final Coordinate coor;

	/**
	 * Initializes the InvalidCoordinateException with the given Coordinate.
	 * 
	 * @param coor
	 *            the invalid Coordinate
	 */
	public InvalidCoordinateException(Coordinate coor) {
		super("Invalid coordinate: " + coor);
		this.coor = coor;
	}

	/**
	 * @return the invalid Coordinate.
	 */
	public Coordinate getCoordinate() {
		return coor;
	}
}
