package game.board;

/**
 * The original game implementation of the board.
 * 
 * @author Akarsh
 *
 */
public class NormalBoard extends Board {

    /**
     * Width and Height of the original board.
     */
    public static final int WIDTH = 11, HEIGHT = 11;

    /**
     * Initializes NormalBoard.
     */
    public NormalBoard() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see game.board.Board#getWidth()
     */
    @Override
    public int getWidth() {
	return WIDTH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see game.board.Board#getHeight()
     */
    @Override
    public int getHeight() {
	return HEIGHT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see game.board.Board#isInBoard(game.board.Coordinate)
     */
    @Override
    public boolean isInBoard(Coordinate coor) {
	byte x = coor.x(), y = coor.y();

	// limit to board
	if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) {
	    return false;
	}

	// exclude corners in a regular board
	if ((x < 2 || x > WIDTH - 3) && (y < 2 || y > HEIGHT - 3)) {
	    for (int xt = 1; xt < 10; xt += 8) {
		for (int yt = 1; yt < 10; yt += 8) {
		    if (x == xt && y == yt) {
			return true;
		    }
		}
	    }
	    return false;
	}
	return true;
    }

}
