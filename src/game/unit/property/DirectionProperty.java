package game.unit.property;

import game.board.Direction;
import game.unit.Unit;

public class DirectionProperty extends Property<Direction> {

    public DirectionProperty(Unit unit, Direction direction) {
	super(unit, direction);
    }

    @Override
    protected void propertyChanged(Direction oldValue, Direction newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }

}
