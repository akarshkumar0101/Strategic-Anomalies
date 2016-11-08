package game.board;

import game.util.Direction;

public class Coordinate {
	private final byte x, y;

	public Coordinate(byte x, byte y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(int x, int y) {
		this((byte) x, (byte) y);
	}

	public byte x() {
		return x;
	}

	public byte y() {
		return y;
	}

	@Override
	public boolean equals(Object another) {
		try {
			Coordinate coor = (Coordinate) another;
			if (x == coor.x && y == coor.y)
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	@Override
	public String toString(){
		return "{"+x+", "+y+"}";
	}

	public static int walkDist(Coordinate coor1, Coordinate coor2) {
		return Math.abs(coor2.x - coor1.x) + Math.abs(coor2.y - coor1.y);
	}

	public static Coordinate shiftCoor(Coordinate coor, Direction dir) {
		if (dir == Direction.RIGHT || dir == Direction.LEFT) {
			return new Coordinate(coor.x + dir.toInt(), coor.y);
		} else if (dir == Direction.UP || dir == Direction.DOWN) {
			return new Coordinate(coor.x, coor.y + dir.toInt());
		} else
			return null;
	}

}
