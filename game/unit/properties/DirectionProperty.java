package game.unit.properties;

import game.unit.Unit;
import game.util.Direction;

public class DirectionProperty extends Property<Direction> {

	private Direction direction;

	public DirectionProperty(Unit unit, Direction Direction) {
		super(unit);
		this.direction = Direction;
	}

	public Direction getDir() {
		return direction;
	}

	public void setDir(Direction direction) {
		Direction oldDir = this.direction;
		this.direction = direction;
		super.propertyChanged(oldDir, this.direction);
	}
}
