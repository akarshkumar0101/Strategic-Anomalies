package game.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Path represents a path that units can take to get to a certain Coordinate. It
 * includes a start Coordinate and a list of Coordinates to follow to end up in
 * the final Coordinate.
 * 
 * @author Akarsh
 *
 */
public class Path {

	/**
	 * The starting Coordinate
	 */
	private final Coordinate startCoor;
	/**
	 * List of Coordinates to follow, these coordinates should next to each
	 * other but do not have to be.
	 */
	private final List<Coordinate> pathCoordinates;
	/**
	 * Option to include teleportation for units that may teleport.
	 */
	private final boolean teleportation;

	/**
	 * Initializes path with the starting Coordinate given
	 * 
	 * @param startCoor
	 *            the starting Coordinate
	 */
	public Path(Coordinate startCoor) {
		this(startCoor, 10);
	}

	/**
	 * Initializes path with the starting Coordinate given, teleportation, and
	 * given Coordinates
	 * 
	 * @param startCoor
	 *            the starting Coordinate
	 * @param teleportation
	 *            option telling if teleportation is used
	 * @param coors
	 *            Coordinates given in path
	 */
	public Path(Coordinate startCoor, boolean teleportation, Coordinate... coors) {
		this.startCoor = startCoor;
		this.teleportation = teleportation;
		pathCoordinates = new ArrayList<>(coors.length);
		for (Coordinate coor : coors) {
			pathCoordinates.add(coor);
		}
	}

	/**
	 * Initializes path with the starting Coordinate given and the expected
	 * length of the path
	 * 
	 * @param startCoor
	 *            the starting Coordinate
	 * @param expectedLength
	 *            the expected length of the path
	 */
	public Path(Coordinate startCoor, int expectedLength) {
		pathCoordinates = new ArrayList<>(expectedLength);
		this.startCoor = startCoor;
		teleportation = false;
	}

	/**
	 * Initializes a non-equal copy of the given Path
	 * 
	 * @param another
	 *            Path to copy
	 */
	public Path(Path another) {
		pathCoordinates = new ArrayList<>(another.pathCoordinates);
		startCoor = another.startCoor;
		teleportation = another.teleportation;
	}

	/**
	 * Adds the Coordinate to the path.
	 * 
	 * @param coor
	 *            Coordinate to add
	 */
	public void addCoordinate(Coordinate coor) {
		pathCoordinates.add(coor);
	}

	/**
	 * Adds the Coordinates contained in the path given to the original path.
	 * The existing beforehand should have the starting coordinate of the given
	 * path already added if avoiding teleportation.
	 * 
	 * @param another
	 *            Path to use
	 */
	public void addPath(Path another) {
		for (Coordinate coor : another.pathCoordinates) {
			pathCoordinates.add(coor);
		}
	}

	/**
	 * @return the starting Coordinate of the path
	 */
	public Coordinate getStartCoor() {
		return startCoor;
	}

	/**
	 * @return the end Coordinate after the path is followed.
	 */
	public Coordinate getEndCoor() {
		return pathCoordinates.get(pathCoordinates.size() - 1);
	}

	/**
	 * @return the list of Coordinates, not including the starting Coordinate.
	 */
	public List<Coordinate> getPathCoordinates() {
		return pathCoordinates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "Path: {";

		for (Coordinate coor : pathCoordinates) {
			str += coor + " ";
		}

		return str + "}";
	}
}
