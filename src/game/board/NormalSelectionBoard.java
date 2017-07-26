package game.board;

public class NormalSelectionBoard extends SelectionBoard {

    public static final int WIDTH = NormalBoard.WIDTH, HEIGHT = NormalBoard.HEIGHT / 2;

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
	if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) {
	    return false;
	}
	return NormalBoard.isInNormalBoard(coor);
    }

    @Override
    protected Class<? extends Board> getCompatibleBoardClass() {
	return NormalBoard.class;
    }

    @Override
    protected Board createNewCompatibleBoard() {
	return new NormalBoard();
    }

    @Override
    protected Object[] transformHomePosition(Coordinate coor, Direction dir) {
	return new Object[] { coor, dir };
    }

    @Override
    protected Object[] transformAwayPosition(Coordinate coor, Direction dir) {
	int x = NormalBoard.WIDTH - 1 - coor.x();
	int y = NormalBoard.HEIGHT - 1 - coor.y();
	coor = new Coordinate(x, y);
	dir = dir.getOpposite();
	return new Object[] { coor, dir };
    }

}
