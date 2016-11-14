package game.unit.properties;

import game.board.Coordinate;
import game.unit.Unit;

public class CoordinateProperty extends Property<Coordinate> {

	private Coordinate coor;

	public CoordinateProperty(Unit unit, Coordinate coor) {
		super(unit);
		this.coor = coor;
	}

	public Coordinate getCoor() {
		return coor;
	}

	public void setCoor(Coordinate coor) {
		Coordinate oldCoor = this.coor;
		this.coor = coor;
		super.propertyChanged(oldCoor, this.coor);
	}
}
