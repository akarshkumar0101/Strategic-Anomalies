package testingframe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
	private final GridLayout gridLayout;
	private final SquareLabel[][] labels;

	public TestingFrame(Board board, Player player1, Player player2) {
		this.board = board;
		this.player1 = player1;
		this.player2 = player2;

		labels = new SquareLabel[board.getWidth()][board.getHeight()];

		gamePainter = new GamePainter();
		gridLayout = new GridLayout(board.getHeight(), board.getWidth());
		gamePainter.setLayout(gridLayout);

		for (int y = 0; y < board.getHeight(); y++) {
			for (int x = 0; x < board.getWidth(); x++) {
				Coordinate coor = new Coordinate(x, y);
				if (!board.isInBoard(coor)) {
					labels[x][y] = new SquareLabel(null);
				} else {
					labels[x][y] = new SquareLabel(board.getSquare(coor));
				}
				gamePainter.add(labels[x][y]);
			}
		}

		this.add(gamePainter);

	}

	public static double scale(double num, double ori1, double ori2, double new1, double new2) {
		double scale = (new1 - new2) / (ori1 - ori2);
		return (num * scale + new1);
	}

	class GamePainter extends JPanel {

		private static final long serialVersionUID = 7783998123812310360L;

		public GamePainter() {
			super();
		}

		@Override
		public void paint(Graphics g) {

			int totalwidth = getWidth(), totalheight = getHeight();

			g.clearRect(0, 0, totalwidth, totalheight);
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, totalwidth, totalheight);

			g.setColor(Color.black);

			for (int x = 0; x < board.getWidth() + 1; x++) {
				int xdist = x * totalwidth / board.getWidth();
				g.drawLine(xdist, 0, xdist, totalheight);
			}
			for (int y = 0; y < board.getWidth() + 1; y++) {
				int ydist = y * totalheight / board.getHeight();
				g.drawLine(0, ydist, totalwidth, ydist);
			}
			super.paint(g);
		}

	}

	private static final Color slightBlue = new Color(192, 192, 210), slightRed = new Color(210, 192, 192);

	class SquareLabel extends JComponent implements ActionListener, MouseListener {

		private static final long serialVersionUID = 7959593291619934967L;

		private final Square sqr;

		public SquareLabel(Square sqr) {
			this.sqr = sqr;

			this.addMouseListener(this);
		}

		public void paintComponent(Graphics g) {
			currentg = g;
			if (sqr == null) {
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
				return;
			}

			g.setColor(Color.lightGray);
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.black);
			g.drawRect(0, 0, getWidth(), getHeight());

			if (sqr.isEmpty())
				return;
			Unit unit = sqr.getUnitOnTop();
			if (unit.getOwnerProp().getProp().equals(player1)) {
				g.setColor(slightBlue);
				g.fillRect(2, 2, getWidth() - 2, getHeight() - 2);
			} else if (unit.getOwnerProp().getProp().equals(player2)) {
				g.setColor(slightRed);
				g.fillRect(2, 2, getWidth() - 2, getHeight() - 2);
			}

			g.drawOval(0, 0, getWidth(), getHeight());

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// pressed this button
		}

		Graphics currentg;

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			currentg.drawOval(50, 50, 50, 50);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}

}
