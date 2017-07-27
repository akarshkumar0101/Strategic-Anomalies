package game.board;

import setup.Position;

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
	return NormalBoard.WIDTH;
    }

    /*
     * (non-Javadoc)
     *
     * @see game.board.Board#getHeight()
     */
    @Override
    public int getHeight() {
	return NormalBoard.HEIGHT;
    }

    public static boolean isInNormalBoard(Coordinate coor) {
	byte x = coor.x(), y = coor.y();

	// limit to board
	if (x >= NormalBoard.WIDTH || x < 0 || y >= NormalBoard.HEIGHT || y < 0) {
	    return false;
	}

	// exclude corners in a regular board
	if ((x < 2 || x > NormalBoard.WIDTH - 3) && (y < 2 || y > NormalBoard.HEIGHT - 3)) {
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

    /*
     * (non-Javadoc)
     *
     * @see game.board.Board#isInBoard(game.board.Coordinate)
     */
    @Override
    public boolean isInBoard(Coordinate coor) {
	return NormalBoard.isInNormalBoard(coor);
    }

    public static Coordinate transformCoordinateForOtherPlayerNormalBoard(Coordinate coor) {
	int x = NormalBoard.WIDTH - 1 - coor.x();
	int y = NormalBoard.HEIGHT - 1 - coor.y();
	return new Coordinate(x, y);
    }

    @Override
    public Coordinate transformCoordinateForOtherPlayer(Coordinate coor) {
	return NormalBoard.transformCoordinateForOtherPlayerNormalBoard(coor);
    }

    @Override
    public Direction transformDirectionForOtherPlayer(Direction dir) {
	return dir.getOpposite();
    }

    @Override
    public Position createHomePosition(Position templatePos) {
	return templatePos;
    }

    @Override
    public Position createAwayPosition(Position templatePos) {
	int x = NormalBoard.WIDTH - 1 - templatePos.getCoor().x();
	int y = NormalBoard.HEIGHT - 1 - templatePos.getCoor().y();
	return new Position(new Coordinate(x, y), templatePos.getDir().getOpposite());
    }

}
