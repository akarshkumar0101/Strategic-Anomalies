package game.board;

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
     * @param unit
     * @param moveToCoor
     * @return the Path the given Unit will take to the given Coordinate if it can
     *         teleport to it.
     */
    public static Path getTeleportedPath(Unit unit, Coordinate moveToCoor) {
	return new Path(moveToCoor);
    }

    // public static int i = 0;

    public static Path getPath(Unit unit, Coordinate moveTo) {
	// long start = System.nanoTime();
	Path path = PathFinder.getClearPathTo(unit, unit.getGame().getBoard(), unit.getPosProp().getValue(), moveTo,
		unit.getMovingProp().getValue());
	// long dt = System.nanoTime() - start;
	// System.out.println((PathFinder.i++) + " ... " + (double) (dt / 1000000) + "
	// ms to find "
	// + unit.getPosProp().getValue() + " to " + moveTo);
	return path;
    }

    private static Path getClearPathTo(Unit unit, Board board, Coordinate from, Coordinate to, int moveRange) {
	return PathFinder.getClearPathTo(unit, board, from, to, moveRange, null, null);
    }

    private static Path getClearPathTo(Unit unit, Board board, Coordinate from, Coordinate to, int remainingMoveRange,
	    Direction prevMoveDir, Path builtPath) {
	if (Board.walkDist(from, to) > remainingMoveRange) {
	    return null;
	}

	for (Direction dir : Direction.values()) {
	    if (prevMoveDir != null && dir == prevMoveDir.getOpposite()) {
		continue;
	    }
	    Coordinate next = Coordinate.shiftCoor(from, dir);
	    try {
		Unit intersecting = board.getUnitAt(next);

		if (intersecting != null && (!Unit.areAllies(unit, intersecting)
			|| intersecting.getMovingProp().getStoicProp().getValue())) {
		    continue;
		}
		Path newPath = builtPath == null ? new Path(next) : builtPath.add(next);

		if (next.equals(to)) {
		    return newPath;
		}

		Path continuedPath = PathFinder.getClearPathTo(unit, board, next, to, remainingMoveRange - 1, dir,
			newPath);

		if (continuedPath != null) {
		    return continuedPath;
		}
	    } catch (Exception e) {
	    }

	}

	return null;
    }

    public static boolean hasClearPathTo(Unit unit, Coordinate from, Coordinate to, int moveRange) {
	return PathFinder.hasClearPathTo(unit, from, to, moveRange, null);
    }

    private static boolean hasClearPathTo(Unit unit, Coordinate from, Coordinate to, int remainingMoveRange,
	    Direction prevMoveDir) {
	if (from.equals(to)) {
	    return true;
	}
	if (Board.walkDist(from, to) > remainingMoveRange) {
	    return false;
	}

	for (Direction dir : Direction.values()) {
	    if (prevMoveDir != null && dir == prevMoveDir.getOpposite()) {
		continue;
	    }
	    Coordinate next = Coordinate.shiftCoor(from, dir);
	    try {
		Unit intersecting = unit.getGame().getBoard().getUnitAt(next);

		if (intersecting != null && (!Unit.areAllies(unit, intersecting)
			|| intersecting.getMovingProp().getStoicProp().getValue())) {
		    continue;
		}

		if (PathFinder.hasClearPathTo(unit, next, to, remainingMoveRange - 1, dir)) {
		    return true;
		}
	    } catch (Exception e) {
	    }

	}

	return false;
    }
}
