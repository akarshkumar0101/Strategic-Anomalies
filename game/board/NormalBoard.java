package game.board;

public class NormalBoard extends Board {

	public static final int WIDTH = 11, HEIGHT = 11;

	public NormalBoard() {
		super();
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public boolean isInBoard(Coordinate coor) {
		byte x = coor.x(), y = coor.y();

		// limit to board
		if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0)
			return false;

		// exclude corners in a regular board
		if ((x < 2 || x > WIDTH - 3) && (y < 2 || y > HEIGHT - 3)) {
			for (int xt = 1; xt < 10; xt += 8) {
				for (int yt = 1; yt < 10; yt += 8) {
					if (x == xt && y == yt)
						return true;
				}
			}
			return false;
		}
		return true;
	}

}
