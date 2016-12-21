package game.unit.properties;

import game.unit.Unit;
import game.util.Direction;

public class DirectionProperty extends Property<Direction> {

	public DirectionProperty(Unit unit, Direction direction) {
		super(unit, direction);
	}

	public void setDir(Direction newdir) {
		if (getCurrentPropertyValue().equals(newdir))
			return;
		Direction oldDir = getCurrentPropertyValue();
		setDir(newdir);
		super.propertyChanged(oldDir, getCurrentPropertyValue());
	}
}
