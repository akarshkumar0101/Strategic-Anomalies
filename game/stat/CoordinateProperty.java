package game.stat;

import game.board.Coordinate;
import game.unit.Unit;

public class CoordinateProperty extends Property<Coordinate> {
	private final Unit unit;
	private Coordinate coor;

	public CoordinateProperty(Unit unit, Coordinate coor) {
		this.unit = unit;
		this.coor = coor;
	}

	public Coordinate getCoor() {
		return coor;
	}

	public void setCoor(Coordinate coor) {
		Coordinate oldCoor = this.coor;
		this.coor = coor;
		super.propertyChanged(unit, oldCoor, this.coor);
	}
}
