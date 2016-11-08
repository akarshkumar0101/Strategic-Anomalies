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

	public boolean isEmpty() {
		return (unitOnTop == null);
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
			unitOnTop.setCoor(coor);
	}
	
	public void removeUnitOnTop(){
		setUnitOnTop(null);
	}

	public Coordinate getCoordinate() {
		return coor;
	}

}
