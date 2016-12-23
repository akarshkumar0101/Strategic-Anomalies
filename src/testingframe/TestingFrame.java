package testingframe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Square;
import game.unit.Knight;
import game.unit.Unit;
import game.util.Direction;

public class TestingFrame extends JFrame {

    private static final long serialVersionUID = 5606773788174572563L;

    private Board board;
    private Player player1, player2;

    private GamePainter gamePainter;

    private GridBagLayout gbLayout;
    private GridBagConstraints gbConstrains;

    public TestingFrame(Board board, Player player1, Player player2) {
	this.board = board;
	this.player1 = player1;
	this.player2 = player2;

	gamePainter = new GamePainter();

	gbLayout = new GridBagLayout();
	gbConstrains = new GridBagConstraints();

	organizeComponents();
    }

    public void organizeComponents() {

	// getContentPane().setLayout(gbLayout);

	getContentPane().add(gamePainter);

    }

    public static double scale(double num, double ori1, double ori2, double new1, double new2) {
	double scale = (new1 - new2) / (ori1 - ori2);
	return num * scale + new1;
    }

    class GamePainter extends JPanel {

	private static final long serialVersionUID = 7783998123812310360L;

	private GridLayout gridLayout;
	private SquareLabel[][] labels;

	public GamePainter() {
	    super();
	    labels = new SquareLabel[board.getWidth()][board.getHeight()];

	    gridLayout = new GridLayout(board.getHeight(), board.getWidth());

	    setLayout(gridLayout);
	    for (int y = 0; y < board.getHeight(); y++) {
		for (int x = 0; x < board.getWidth(); x++) {
		    Coordinate coor = new Coordinate(x, y);
		    if (!board.isInBoard(coor)) {
			labels[x][y] = new SquareLabel(null);
		    } else {
			labels[x][y] = new SquareLabel(board.getSquare(coor));
		    }
		    this.add(labels[x][y]);
		}
	    }
	}

    }

    private static final Color slightBlue = new Color(192, 192, 220), slightRed = new Color(220, 192, 192);

    private static Color darkerColor(Color col, int amount) {
	return new Color(amount > col.getRed() ? 0 : col.getRed() - amount,
		amount > col.getGreen() ? 0 : col.getGreen() - amount,
		amount > col.getBlue() ? 0 : col.getBlue() - amount);
    }

    class SquareLabel extends JComponent implements ActionListener, MouseListener {

	private static final long serialVersionUID = 7959593291619934967L;

	private static final double percentageIconHeight = 1, percentageIconWidth = percentageIconHeight;

	private boolean mouseIn;
	private boolean mousePressing;

	private Color currentBackgroundColor;

	private final Square sqr;
	private Unit unitOnTop;
	private Image img;

	public SquareLabel(Square sqr) {
	    this.sqr = sqr;
	    addMouseListener(this);

	    updateInformation();
	}

	public void updateInformation() {

	    unitOnTop = sqr == null ? null : sqr.getUnitOnTop();
	    if (unitOnTop == null) {
		img = null;
	    } else if (unitOnTop.getClass() == Knight.class) {
		img = new ImageIcon(getClass().getResource("/temp_pics/warrior.png")).getImage();
	    }
	}

	public Color determineBackgroundColor() {
	    if (sqr == null) {
		return Color.black;
	    }

	    Color col = Color.lightGray;

	    if (unitOnTop != null) {
		Player owner = unitOnTop.getOwnerProp().getCurrentPropertyValue();

		if (owner.equals(player1)) {
		    col = slightBlue;
		} else if (owner.equals(player2)) {
		    col = slightRed;
		}
	    }

	    if (mousePressing) {
		col = darkerColor(col, 50);
	    } else if (mouseIn) {
		col = darkerColor(col, 25);
	    }
	    return col;

	}

	@Override
	public void paintComponent(Graphics g) {
	    currentg = g;

	    currentBackgroundColor = determineBackgroundColor();
	    // clear
	    g.setColor(currentBackgroundColor);
	    g.fillRect(0, 0, getWidth(), getHeight());

	    // if outside of board
	    if (sqr == null) {
		return;
	    }

	    // border
	    g.setColor(Color.black);
	    g.drawRect(0, 0, getWidth(), getHeight());

	    // if empty square, end it
	    if (sqr.isEmpty()) {
		return;
	    }

	    // draw image in the center
	    int imgWidth = (int) (percentageIconWidth * getWidth()),
		    imgHeight = (int) (percentageIconHeight * getHeight());
	    g.drawImage(img, (getWidth() - imgWidth) / 2, (getHeight() - imgHeight) / 2, imgWidth, imgHeight, null);

	    // draw health bar
	    double healthPercentage = unitOnTop.getHealthProp().percentageHealth();
	    int healthBarHeight = getHeight() / 15;
	    g.setColor(Color.green);
	    g.fillRect(1, 1, (int) (healthPercentage * getWidth()) - 1, healthBarHeight);

	    // draw direction facing arrow
	    Direction dir = unitOnTop.getPosProp().getDirFacingProp().getCurrentPropertyValue();
	    int arrowWidth = (int) (.5 * getWidth()), arrowHeight = (int) (.2 * getHeight());
	    g.setColor(Color.red);
	    if (dir == Direction.UP) {
		g.fillPolygon(
			new int[] { (getWidth() - arrowWidth) / 2, getWidth() / 2, (getWidth() + arrowWidth) / 2 },
			new int[] { arrowHeight + healthBarHeight, healthBarHeight, arrowHeight + healthBarHeight }, 3);
	    } else if (dir == Direction.DOWN) {
		g.fillPolygon(
			new int[] { (getWidth() - arrowWidth) / 2, getWidth() / 2, (getWidth() + arrowWidth) / 2 },
			new int[] { getHeight() - arrowHeight, getHeight(), getHeight() - arrowHeight }, 3);
	    }
	    arrowHeight = (int) (.2 * getWidth());
	    arrowWidth = (int) (.5 * getHeight());
	    if (dir == Direction.LEFT) {
		g.fillPolygon(new int[] { arrowHeight, 0, arrowHeight },
			new int[] { (getHeight() - arrowWidth) / 2, getHeight() / 2, (getHeight() + arrowWidth) / 2 },
			3);
	    } else if (dir == Direction.RIGHT) {
		g.fillPolygon(new int[] { getWidth() - arrowHeight, getWidth(), getWidth() - arrowHeight },
			new int[] { (getHeight() - arrowWidth) / 2, getHeight() / 2, (getHeight() + arrowWidth) / 2 },
			3);
	    }

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	public void onClick() {
	    System.out.println("press");
	}

	Graphics currentg;

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	    mousePressing = true;
	    repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    mousePressing = false;
	    onClick();
	    repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    mouseIn = true;
	    repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
	    mouseIn = false;
	    repaint();
	}

    }

}
