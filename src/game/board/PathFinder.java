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

    // /**
    // * Returns the path the given Unit will take to the given Coordinate.
    // *
    // * @param unit
    // * Unit to move
    // * @param moveToCoor
    // * Coordinate to move to
    // * @return the Path the Unit will take to move to the Coordinate
    // */
    // public static Path getPath(Unit unit, Coordinate moveToCoor) {
    // Board board = unit.getGame().getBoard();
    // int moveRange = unit.getMovingProp().getValue();
    // Coordinate from = unit.getPosProp().getValue();
    //
    // if (!board.isInBoard(moveToCoor)) {
    // return null;
    // }
    // if (Board.walkDist(from, moveToCoor) > moveRange) {
    // return null;
    // }
    //
    // Path path = PathFinder.getPath(board, unit, moveRange, from, moveToCoor,
    // null, null);
    // System.gc();
    // return path;
    // }
    //
    // /**
    // * The recursive algorithm to determine what path will be taken by
    // * considering all possibilities of paths. This is necessary because of
    // how
    // * pieces can block or not sidestep certain paths.
    // *
    // * @param board
    // * @param unit
    // * @param remainingMoveRange
    // * @param from
    // * @param to
    // * @param prevDir
    // * @param pathConstructed
    // * @return the constructed path to the Coordinate
    // */
    // private static Path getPath(Board board, Unit unit, int
    // remainingMoveRange, Coordinate from, Coordinate to,
    // Direction prevDir, Path pathConstructed) {
    // if (Board.walkDist(from, to) > remainingMoveRange) {
    // return null;
    // }
    // if (pathConstructed == null) {
    // pathConstructed = new Path(from);
    // }
    // for (Direction dir : Direction.values()) {
    // if (prevDir != null && dir == prevDir.getOpposite()) {
    // continue;
    // }
    // Coordinate newcoor = Coordinate.shiftCoor(from, dir);
    //
    // // TODO edit this to make it go through side step but not
    // // enemies
    // Unit intersecting = board.isInBoard(newcoor) ? board.getUnitAt(newcoor) :
    // null;
    // if (intersecting != null) {
    // if (Unit.areAllies(intersecting, unit)) {
    // if (intersecting.getMovingProp().isCurrentlyStoic()) {
    // continue;
    // }
    // } else {
    // continue;
    // }
    // }
    //
    // Path path = pathConstructed.add(newcoor);
    //
    // if (newcoor.equals(to)) {
    // return path;
    // }
    //
    // Path continuedPath = PathFinder.getPath(board, unit, remainingMoveRange -
    // 1, newcoor, to, dir, path);
    // if (continuedPath != null) {
    // return continuedPath;
    // }
    // }
    //
    // return null;
    // }

    public static Path getPath(Unit unit, Coordinate moveTo) {
	return PathFinder.getClearPathTo(unit, unit.getGame().getBoard(), unit.getPosProp().getValue(), moveTo,
		unit.getMovingProp().getValue());
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
