package setup;

import java.util.ArrayList;
import java.util.List;

import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;

public class Template {

    private final Class<? extends Board> compatibleBoardType;

    private final List<PieceInformation> pieceKnowledge;

    public Template(Class<? extends Board> compatibleBoardType) {
	this.compatibleBoardType = compatibleBoardType;
	pieceKnowledge = new ArrayList<>(15);
    }

    public void put(Class<? extends Unit> unitClass, Coordinate coor) {

    }

}

class PieceInformation {

    private final Class<? extends Unit> clazz;

    private final Coordinate location;

    private final Direction dirFacing;

    public PieceInformation(Class<? extends Unit> clazz, Coordinate location, Direction dirFacing) {
	this.clazz = clazz;
	this.location = location;
	this.dirFacing = dirFacing;
    }

    public Class<? extends Unit> getUnitClass() {
	return clazz;
    }

    public Coordinate getLocation() {
	return location;
    }

    public Direction dirFacing() {
	return dirFacing;
    }
}