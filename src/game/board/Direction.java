package game.board;

/**
 * Direction on the 2D Board that are possible: UP, DOWN, LEFT, RIGHT.
 * 
 * @author Akarsh
 *
 */
public enum Direction {
    // up and right are towards increasing coordinate values
    UP, RIGHT, DOWN, LEFT;

    /**
     * @return the opposite direction of this object.
     */
    public Direction getOpposite() {
	int ordin = ordinal() + 2;
	if (ordin > 3) {
	    ordin -= 4;
	}

	return Direction.values()[ordin];
    }

    /**
     * @return 1 if this is UP or RIGHT, -1 otherwise.
     */
    public int toInt() {
	if (ordinal() < 2) {
	    return 1;
	} else {
	    return -1;
	}
    }
}
