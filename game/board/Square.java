package game.board;

import game.unit.Unit;

/**
 * Square is the element of the Board's grid that can hold pieces.
 * 
 * @author Akarsh
 *
 */
public class Square {

    /**
     * Location of the Square
     */
    private final Coordinate coor;

    /**
     * The Unit on top of this Square
     */
    private Unit unitOnTop;

    /**
     * Initializes the Square with the given Coordinate
     * 
     * @param coor
     *            location of the Square
     */
    public Square(Coordinate coor) {
	this(null, coor);
    }

    /**
     * Initializes the Square with the given Coordinate and the Unit on top
     * 
     * @param unitOnTop
     *            Unit on top of the Square
     * @param coor
     *            location of the Square
     */
    public Square(Unit unitOnTop, Coordinate coor) {
	this.coor = coor;
	this.unitOnTop = unitOnTop;
    }

    /**
     * @return the location of the Square
     */
    public Coordinate getCoor() {
	return coor;
    }

    /**
     * @return the Unit on top of the Square
     */
    public Unit getUnitOnTop() {
	return unitOnTop;
    }

    /**
     * Places the given Unit on this square and sets the Unit's coordinate to
     * the Square coordinate.
     * 
     * @param unitOnTop
     */
    public void setUnitOnTop(Unit unitOnTop) {
	this.unitOnTop = unitOnTop;
	if (unitOnTop != null) {
	    unitOnTop.getPosProp().setPropertyValue(coor);
	}
    }

    /**
     * Discards the unit on top of the Square.
     */
    public void removeUnitOnTop() {
	unitOnTop = null;
    }

    /**
     * @return true if no Unit is on top of this Square.
     */
    public boolean isEmpty() {
	return unitOnTop == null;
    }

}
