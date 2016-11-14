package game.util;

public enum Direction {
	UP, RIGHT, DOWN, LEFT;

	public Direction getOpposite() {
		int ordin = this.ordinal() + 2;
		if (ordin > 3)
			ordin -= 4;

		return Direction.values()[ordin];
	}

	public int toInt() {
		if (ordinal() > 1)
			return -1;
		return 1;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
