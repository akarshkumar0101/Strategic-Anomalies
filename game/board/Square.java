package game.board;

import game.unit.Unit;

public class Square {

	private final Coordinate coor;

	private Unit unitOnTop;

	public Square(Unit unitOnTop, Coordinate coor) {
		this.coor = coor;
		this.unitOnTop = unitOnTop;
	}

	public Square(Coordinate coor) {
		this(null, coor);
	}

	public Coordinate getCoor() {
		return coor;
	}

	public Unit getUnitOnTop() {
		return unitOnTop;
	}

	/**
	 * Places UnitOnTop on this square and sets the unit's coordinate to the
	 * square's coordinate
	 */
	public void setUnitOnTop(Unit unitOnTop) {
		this.unitOnTop = unitOnTop;
		if (unitOnTop != null)
			unitOnTop.getCoorProp().setCoor(coor);
	}

	public void removeUnitOnTop() {
		this.unitOnTop = null;
	}

	public boolean isEmpty() {
		return (unitOnTop == null);
	}

}
