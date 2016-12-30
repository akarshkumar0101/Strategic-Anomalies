package game.unit.property;

import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;

public class PositionProperty extends Property<Coordinate> {

    private final DirectionProperty dirFacingProp;

    public PositionProperty(Unit unit, Coordinate coor, Direction directionFacing) {
	super(unit, coor);
	dirFacingProp = new DirectionProperty(unit, directionFacing);
    }

    public DirectionProperty getDirFacingProp() {
	return dirFacingProp;
    }

    @Override
    protected void propertyChanged(Coordinate oldValue, Coordinate newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }

}
