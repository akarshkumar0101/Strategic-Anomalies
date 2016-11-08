package testingframe;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;

import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Square;
import game.unit.Unit;

public class TestingFrame extends JFrame {

	private static final long serialVersionUID = 5606773788174572563L;

	private final Board board;
	private final Player player1, player2;

	private final GamePainter gamePainter;
	private final SquareLabel[][] labels;

	public TestingFrame(Board board, Player player1, Player player2) {
		this.board = board;
		this.player1 = player1;
		this.player2 = player2;

		labels = new SquareLabel[board.getWidth()][board.getHeight()];

		for (int y = 0; y < board.getHeight(); y++) {
			for (int x = 0; x < board.getWidth(); x++) {
				Coordinate coor = new Coordinate(x, y);
				if (!board.isInBoard(coor)) {
					labels[x][y] = new SquareLabel(null);
				} else {
					labels[x][y] = new SquareLabel(board.getSquare(coor));
				}
			}
		}
		
		gamePainter = new GamePainter();
		this.add(gamePainter);
		
	}

	public static double scale(double num, double ori1, double ori2, double new1, double new2) {
		double scale = (new1 - new2) / (ori1 - ori2);
		return (num * scale + new1);
	}
	
	class GamePainter extends JComponent{

		private static final long serialVersionUID = 7783998123812310360L;
		
		public GamePainter(){
			super();
		}
		@Override
		public void paintComponent(Graphics g){
			g.setColor(Color.black);
			
			int width = getWidth(), height = getHeight();
			
			for(int x=0; x<board.getWidth()+1;x++){
				int xdist = x*width/board.getWidth();
				g.drawLine(xdist, 0, xdist, height);
			}
			for(int y=0; y<board.getWidth()+1;y++){
				int ydist = y*height/board.getHeight();
				g.drawLine(0,ydist, width, ydist);
			}
			
		}
		
	}

	private static final Color slightBlue = new Color(192, 192, 210),slightRed = new Color(210, 192, 192);

	class SquareLabel extends JComponent {

		private static final long serialVersionUID = 7959593291619934967L;

		private final Square sqr;

		public SquareLabel(Square sqr) {
			this.sqr = sqr;
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.black);
			if (sqr == null) {
				g.fillRect(0, 0, getWidth(), getHeight());
				return;
			}
			g.clearRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.lightGray);
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.black);
			g.drawRect(0, 0, getWidth(), getHeight());

			if (sqr.isEmpty())
				return;
			Unit unit = sqr.getUnitOnTop();
			if (unit.getPlayerOwner().equals(player1)) {
				g.setColor(slightBlue);
				g.fillRect(2, 2, getWidth()-2, getHeight()-2);
			}
			else if (unit.getPlayerOwner().equals(player2)) {
				g.setColor(slightRed);
				g.fillRect(2, 2, getWidth()-2, getHeight()-2);
			}

			g.drawOval(0, 0, getWidth(), getHeight());

		}

	}

}
