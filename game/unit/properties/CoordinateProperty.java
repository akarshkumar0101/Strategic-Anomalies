package game.unit.properties;

import game.board.Coordinate;
import game.unit.Unit;

public class CoordinateProperty extends Property<Coordinate> {

	public CoordinateProperty(Unit unit, Coordinate coor) {
		super(unit,coor);
	}

	public Coordinate getCoor() {
		return property;
	}

	public void setCoor(Coordinate newcoor) {
		if (property.equals(newcoor))
			return;
		Coordinate oldCoor = this.property;
		this.property = newcoor;
		super.propertyChanged(oldCoor, this.property);
	}
}
