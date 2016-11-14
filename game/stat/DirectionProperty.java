package game.stat;

import game.unit.Unit;
import game.util.Direction;

public class DirectionProperty extends Property<Direction> {
	private final Unit unit;
	private Direction direction;

	public DirectionProperty(Unit unit, Direction Direction) {
		this.unit = unit;
		this.direction = Direction;
	}

	public Direction getDir() {
		return direction;
	}

	public void setDir(Direction direction) {
		Direction oldDir = this.direction;
		this.direction = direction;
		super.propertyChanged(unit, oldDir, this.direction);
	}
}
