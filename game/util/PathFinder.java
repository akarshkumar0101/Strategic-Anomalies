package game.util;

import game.board.Board;
import game.board.Coordinate;
import game.board.Path;
import game.unit.Unit;

public class PathFinder {
	public static Path getPath(Unit unit, Coordinate moveToCoor) {
		Board board = unit.getBoard();
		int moveRange = unit.getMoveRange();
		Coordinate from = unit.getCoor();

		if (!board.isInBoard(moveToCoor))
			return null;
		if (Coordinate.walkDist(from, moveToCoor) > moveRange)
			return null;

		Path path = getPath(board, unit, moveRange, from, moveToCoor, null, null);
		System.gc();
		return path;
	}

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
