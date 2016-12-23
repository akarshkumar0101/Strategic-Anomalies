package game.unit.properties;

import game.board.Coordinate;
import game.unit.Unit;
import game.util.Direction;

public class PositionProperty extends Property<Coordinate> {

    private final DirectionProperty dirFacingProp;

    public PositionProperty(Unit unit, Coordinate coor, Direction directionFacing) {
	super(unit, coor);
	dirFacingProp = new DirectionProperty(unit, directionFacing);
    }

    public DirectionProperty getDirFacingProp() {
	return dirFacingProp;
    }

}
