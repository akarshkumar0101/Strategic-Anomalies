package setup;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;

public class SetupTemplate implements Serializable {

    private static final long serialVersionUID = -6091622181104129029L;

    private final Class<? extends Board> compatibleBoardType;

    private final HashMap<Coordinate, PieceInformation> template;

    public SetupTemplate(Class<? extends Board> compatibleBoardType) {
	this.compatibleBoardType = compatibleBoardType;
	template = new HashMap<>();
    }

    public Class<? extends Board> getCompatibleBoardType() {
	return compatibleBoardType;
    }

    public void put(Class<? extends Unit> unitClass, Coordinate coor, Direction dirFacing) {
	if (unitClass == null) {
	    remove(coor);
	} else {
	    template.put(coor, new PieceInformation(unitClass, coor, dirFacing));
	}
    }

    public void remove(Coordinate coor) {
	template.remove(coor);
    }

    public boolean pieceExistsAt(Coordinate coor) {
	return template.containsKey(coor);
    }

    public Class<? extends Unit> getUnitClassAt(Coordinate coor) {
	if (template.get(coor) == null) {
	    return null;
	} else {
	    return template.get(coor).getUnitClass();
	}
    }

    public Direction getDirFacing(Coordinate coor) {
	if (template.get(coor) == null) {
	    return null;
	} else {
	    return template.get(coor).getPostion().getDir();
	}
    }

    private static Unit createNewUnit(Class<? extends Unit> unitClass, Position pos, Game game, Player player) {
	Constructor<? extends Unit> cons;
	try {
	    cons = unitClass.getConstructor(Game.class, Player.class, Direction.class, Coordinate.class);
	    Unit unit = cons.newInstance(game, player, pos.getDir(), pos.getCoor());
	    return unit;
	} catch (Exception e) {
	    throw new RuntimeException("Could not find/create new unit");
	}
    }

    public static void setupBoardWithTemplates(Board board, Game game, Player homePlayer, Player awayPlayer,
	    SetupTemplate a, SetupTemplate b) {
	if (a.compatibleBoardType != b.compatibleBoardType) {
	    throw new RuntimeException("Incompatible selection boards");
	}
	if (board.getClass() != a.compatibleBoardType) {
	    throw new RuntimeException("Incompatible board with selection board");
	}

	for (PieceInformation pi : a.template.values()) {
	    Position pos = board.createHomePosition(pi.getPostion());

	    Unit unit = SetupTemplate.createNewUnit(pi.getUnitClass(), pos, game, homePlayer);
	    board.linkBoardToUnit(unit);
	}
	for (PieceInformation pi : b.template.values()) {
	    Position pos = board.createAwayPosition(pi.getPostion());

	    Unit unit = SetupTemplate.createNewUnit(pi.getUnitClass(), pos, game, awayPlayer);
	    board.linkBoardToUnit(unit);
	}

    }
}

class PieceInformation implements Serializable {

    private static final long serialVersionUID = 3204645410267261251L;

    private final Class<? extends Unit> unitClass;

    private final Position pos;

    public PieceInformation(Class<? extends Unit> unitClass, Coordinate location, Direction dirFacing) {
	this.unitClass = unitClass;
	pos = new Position(location, dirFacing);
    }

    public Class<? extends Unit> getUnitClass() {
	return unitClass;
    }

    public Position getPostion() {
	return pos;
    }
}