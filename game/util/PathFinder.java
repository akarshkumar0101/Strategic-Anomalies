package game.util;

import game.board.Board;
import game.board.Coordinate;
import game.board.Path;
import game.unit.Unit;

/**
 * The algorithm used to determine which path a unit will take to a given
 * coordinate. This algorithm includes units that can/cannot sidestep, whether a
 * unit is blocking the path, etc.
 * 
 * @author Akarsh
 *
 */
public class PathFinder {

	/**
	 * Returns the path the given Unit will take to the given Coordinate.
	 * 
	 * @param unit
	 *            Unit to move
	 * @param moveToCoor
	 *            Coordinate to move to
	 * @return the Path the Unit will take to move to the Coordinate
	 */
	public static Path getPath(Unit unit, Coordinate moveToCoor) {
		Board board = unit.getGame().getBoard();
		int moveRange = unit.getMoveRange();
		Coordinate from = unit.getCoorProp().getProp();

		if (!board.isInBoard(moveToCoor))
			return null;
		if (Coordinate.walkDist(from, moveToCoor) > moveRange)
			return null;

		Path path = getPath(board, unit, moveRange, from, moveToCoor, null, null);
		System.gc();
		return path;
	}

	/**
	 * The recursive algorithm to determine what path will be taken by
	 * considering all possibilities of paths. This is necessary because of how
	 * pieces can block or not sidestep certain paths.
	 * 
	 * @param board
	 * @param unit
	 * @param remainingMoveRange
	 * @param from
	 * @param to
	 * @param prevDir
	 * @param pathConstructed
	 * @return the constructed path to the Coordinate
	 */
	private static Path getPath(Board board, Unit unit, int remainingMoveRange, Coordinate from, Coordinate to,
			Direction prevDir, Path pathConstructed) {
		if (Coordinate.walkDist(from, to) > remainingMoveRange)
			return null;
		if (pathConstructed == null) {
			pathConstructed = new Path(from);
		}
		for (Direction dir : Direction.values()) {
			if (prevDir != null && dir == prevDir.getOpposite()) {
				continue;
			}
			Path path = new Path(pathConstructed);
			Coordinate newcoor = Coordinate.shiftCoor(from, dir);

			// TODO edit this to make it go through side step but not
			// enemies
			Unit intersecting = board.getUnitAt(newcoor);
			if (intersecting != null) {
				if (Unit.areAllies(intersecting, unit)) {
					if (!intersecting.canSideStep())
						continue;
				} else
					continue;
			}

			path.addCoordinate(newcoor);

			if (newcoor.equals(to))
				return path;

			Path continuedPath = getPath(board, unit, remainingMoveRange - 1, newcoor, to, dir, path);
			if (continuedPath != null)
				return continuedPath;
		}

		return null;
	}
}
