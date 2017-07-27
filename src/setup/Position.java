package setup;

import java.io.Serializable;

import game.board.Coordinate;
import game.board.Direction;

public class Position implements Serializable {

    private static final long serialVersionUID = 8515591703481026601L;

    private final Coordinate coor;
    private final Direction dir;

    public Position(Coordinate coor, Direction dir) {
	this.coor = coor;
	this.dir = dir;
    }

    public Coordinate getCoor() {
	return coor;
    }

    public Direction getDir() {
	return dir;
    }
}
