package game.unit.properties;

import game.unit.Unit;
import game.util.Direction;

public class DirectionProperty extends Property<Direction> {

    public DirectionProperty(Unit unit, Direction direction) {
	super(unit, direction);
    }

    @Override
    protected void propertyChanged(Direction oldValue, Direction newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }

}
