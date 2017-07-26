package game.board;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import game.Game;
import game.Player;
import game.unit.Unit;

public abstract class SelectionBoard implements Serializable {

    private static final long serialVersionUID = 8230025453636848756L;

    protected final Selection[][] grid;

    /**
     * Initializes board with its subclasses' width and height.
     */
    public SelectionBoard() {
	grid = new Selection[getWidth()][getHeight()];

	for (byte x = 0; x < getWidth(); x++) {
	    for (byte y = 0; y < getHeight(); y++) {
		grid[x][y] = null;
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

    public boolean isEmptyAt(Coordinate coor) {
	return grid[coor.x()][coor.y()] == null;
    }

    public Class<? extends Unit> getUnitClass(Coordinate coor) {
	if (!isEmptyAt(coor)) {
	    return grid[coor.x()][coor.y()].getUnitClass();
	} else {
	    return null;
	}
    }

    public Direction getDirFacing(Coordinate coor) {
	if (!isEmptyAt(coor)) {
	    return grid[coor.x()][coor.y()].getDirFacing();
	} else {
	    return null;
	}
    }

    public void removeSelection(Coordinate coor) {
	grid[coor.x()][coor.y()] = null;
    }

    public void setSelection(Coordinate coor, Class<? extends Unit> unitClass, Direction dirFacing) {
	if (unitClass == null) {
	    removeSelection(coor);
	} else {
	    if (dirFacing == null) {
		dirFacing = Direction.UP;
	    }
	    if (grid[coor.x()][coor.y()] == null) {
		grid[coor.x()][coor.y()] = new Selection(unitClass, dirFacing);
	    } else {
		grid[coor.x()][coor.y()].setUnitClass(unitClass);
		grid[coor.x()][coor.y()].setDirFacing(dirFacing);
	    }
	}
    }

    protected abstract Class<? extends Board> getCompatibleBoardClass();

    protected abstract Board createNewCompatibleBoard();

    protected abstract Object[] transformHomePosition(Coordinate coor, Direction dir);

    protected abstract Object[] transformAwayPosition(Coordinate coor, Direction dir);

    public Board combineWithOtherSelection(Game game, Player homePlayer, Player awayPlayer, SelectionBoard otherSel) {
	Board board = createNewCompatibleBoard();
	setupBoardWithSelections(board, game, homePlayer, awayPlayer, otherSel);
	return board;
    }

    public void setupBoardWithSelections(Board board, Game game, Player homePlayer, Player awayPlayer,
	    SelectionBoard otherSelBoard) {
	if (this.getClass() != otherSelBoard.getClass()) {
	    throw new RuntimeException("Incompatible selection boards");
	}
	if (board.getClass() != getCompatibleBoardClass()) {
	    throw new RuntimeException("Incompatible board with selection board");
	}

	for (int x = 0; x < getWidth(); x++) {
	    for (int y = 0; y < getHeight(); y++) {
		Selection homeSel = grid[x][y];
		if (homeSel != null) {
		    Object[] position = transformHomePosition(new Coordinate(x, y), homeSel.dirFacing);
		    Coordinate coor = (Coordinate) position[0];
		    Direction dir = (Direction) position[1];

		    Unit unit = createNewUnit(homeSel.unitClass, game, homePlayer, dir, coor);
		    board.linkBoardToUnit(unit);
		}

		Selection awaySel = otherSelBoard.grid[x][y];
		if (awaySel != null) {
		    Object[] position = transformAwayPosition(new Coordinate(x, y), awaySel.dirFacing);
		    Coordinate coor = (Coordinate) position[0];
		    Direction dir = (Direction) position[1];

		    Unit unit = createNewUnit(awaySel.unitClass, game, awayPlayer, dir, coor);
		    board.linkBoardToUnit(unit);
		}
	    }
	}

    }

    // new Scout(tgame, player1, Direction.LEFT, new Coordinate(3, 5));
    public Unit createNewUnit(Class<? extends Unit> unitClass, Game game, Player player, Direction dir,
	    Coordinate coor) {
	Constructor<? extends Unit> cons;
	try {
	    cons = unitClass.getConstructor(Game.class, Player.class, Direction.class, Coordinate.class);
	    Unit unit = cons.newInstance(game, player, dir, coor);
	    return unit;
	} catch (Exception e) {
	    throw new RuntimeException("Could not find/create new unit");
	}
    }

    // describes which Class <? extends Unit> and what direction it is in
    protected class Selection implements Serializable {
	private static final long serialVersionUID = -3861218340046231404L;

	private Class<? extends Unit> unitClass;
	private Direction dirFacing;

	private Selection(Class<? extends Unit> unitClass, Direction dirFacing) {
	    this.unitClass = unitClass;
	    this.dirFacing = dirFacing;
	}

	public void setUnitClass(Class<? extends Unit> unitClass) {
	    this.unitClass = unitClass;
	}

	public void setDirFacing(Direction dirFacing) {
	    this.dirFacing = dirFacing;
	}

	public Class<? extends Unit> getUnitClass() {
	    return unitClass;
	}

	public Direction getDirFacing() {
	    return dirFacing;
	}
    }

}
