package testingframe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.Game;
import game.Player;
import game.Team;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.unit.Knight;
import game.unit.Unit;
import main.Main;

public class TestingFrame extends JFrame {

    private static final long serialVersionUID = 5606773788174572563L;

    private Board board;
    private Team team1, team2;
    private Player player1, player2;

    private final GamePainter gamePainter;
    private final GameInformation gameInformation;

    private GridBagLayout gbLayout;
    private GridBagConstraints gbConstrains;

    public TestingFrame(Game game) {
	board = game.getBoard();
	team1 = game.getTeam1();
	team2 = game.getTeam2();
	player1 = team1.getPlayers()[0];
	player2 = team2.getPlayers()[0];

	gamePainter = new GamePainter();
	gameInformation = new GameInformation();

	gbLayout = new GridBagLayout();
	gbConstrains = new GridBagConstraints();

	organizeComponents();
    }

    public void organizeComponents() {

	getContentPane().setLayout(gbLayout);

	gbConstrains.gridx = 0;
	gbConstrains.gridy = 0;
	gbConstrains.weightx = 0;
	gbConstrains.weighty = 1;
	gbConstrains.fill = GridBagConstraints.BOTH;
	gbConstrains.anchor = GridBagConstraints.CENTER;

	getContentPane().add(gamePainter, gbConstrains);

	gbConstrains.gridx = 1;
	gbConstrains.gridy = 0;
	gbConstrains.weightx = 1;
	gbConstrains.weighty = 1;
	gbConstrains.fill = GridBagConstraints.BOTH;
	gbConstrains.anchor = GridBagConstraints.CENTER;

	getContentPane().add(gameInformation, gbConstrains);

    }

    public void updateInformation() {
	gamePainter.updateInformation();
    }

    public static double scale(double num, double ori1, double ori2, double new1, double new2) {
	double scale = (new1 - new2) / (ori1 - ori2);
	return num * scale + new1;
    }

    private static final Color slightBlue = new Color(192, 192, 220), slightRed = new Color(220, 192, 192);

    private static Color darkerColor(Color col, int amount) {
	return new Color(amount > col.getRed() ? 0 : col.getRed() - amount,
		amount > col.getGreen() ? 0 : col.getGreen() - amount,
		amount > col.getBlue() ? 0 : col.getBlue() - amount);
    }

    class GamePainter extends JPanel {

	private static final long serialVersionUID = 7783998123812310360L;

	private GridLayout gridLayout;
	private SquareLabel[][] labels;

	private Square mouseInSquare;

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

	@Override
	public Dimension getPreferredSize() {
	    return new Dimension(900, 900);
	}

	public void updateInformation() {
	    for (int y = 0; y < board.getHeight(); y++) {
		for (int x = 0; x < board.getWidth(); x++) {
		    labels[x][y].updateInformation();
		}
	    }
	}

	class SquareLabel extends JComponent implements MouseListener {

	    private static final long serialVersionUID = 7959593291619934967L;

	    private static final double percentageIconHeight = 1, percentageIconWidth = percentageIconHeight;

	    private boolean mouseIn;
	    private boolean mousePressing;

	    private Color currentBackgroundColor;

	    private final Square sqr;
	    private Unit unitOnTop;
	    private Image unitImg;

	    public SquareLabel(Square sqr) {
		this.sqr = sqr;
		addMouseListener(this);

		updateInformation();
	    }

	    public void updateInformation() {

		unitOnTop = sqr == null ? null : sqr.getUnitOnTop();
		if (unitOnTop == null) {
		    unitImg = null;
		} else if (unitOnTop.getClass() == Knight.class) {
		    unitImg = Images.warriorImage;
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
		g.drawImage(unitImg, (getWidth() - imgWidth) / 2, (getHeight() - imgHeight) / 2, imgWidth, imgHeight,
			null);

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
		    g.drawImage(Images.upArrowImage, (getWidth() - arrowWidth) / 2, 0, arrowWidth, arrowHeight, null);
		} else if (dir == Direction.DOWN) {
		    g.drawImage(Images.downArrowImage, (getWidth() - arrowWidth) / 2, getHeight() - arrowHeight,
			    arrowWidth, arrowHeight, null);
		}
		arrowHeight = (int) (.2 * getWidth());
		arrowWidth = (int) (.5 * getHeight());
		if (dir == Direction.LEFT) {
		    g.drawImage(Images.leftArrowImage, 0, (getHeight() - arrowWidth) / 2, arrowHeight, arrowWidth,
			    null);

		} else if (dir == Direction.RIGHT) {
		    g.drawImage(Images.rightArrowImage, getWidth() - arrowHeight, (getHeight() - arrowWidth) / 2,
			    arrowHeight, arrowWidth, null);
		}

	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
		System.out.println("click");
		if (sqr != null) {
		    Main.movableUnit.getPosProp().setPropertyValue(sqr.getCoor());
		    TestingFrame.this.updateInformation();
		    TestingFrame.this.repaint();
		}
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		mousePressing = true;
		repaint();
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		mousePressing = false;
		repaint();
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		setMouseInSquare(sqr);
		mouseIn = true;
		repaint();
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		setMouseInSquare(null);
		mouseIn = false;
		repaint();
	    }

	    public void setMouseInSquare(Square sqr) {
		if (gamePainter.mouseInSquare == sqr) {
		    return;
		}
		gamePainter.mouseInSquare = sqr;
		// System.out.println("Mouse is now in: " + (sqr == null ?
		// "null" :
		// sqr.getCoor()));
	    }
	}
    }

    class GameInformation extends JPanel {

	private static final long serialVersionUID = 1L;

	private Square mouseInSquare;

	public GameInformation() {

	}

	public void updateInformation() {
	    mouseInSquare = gamePainter.mouseInSquare;

	}

	@Override
	public void paintComponent(Graphics g) {
	}

    }

}

class Images {

    public static Image warriorImage;

    public static Image upArrowImage;
    public static Image rightArrowImage;
    public static Image downArrowImage;
    public static Image leftArrowImage;

    public static Image dizzyImage;
    static {
	try {
	    warriorImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/warrior.png"));

	    upArrowImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/redarrow.png"));
	    rightArrowImage = rotate((BufferedImage) upArrowImage, 90);
	    downArrowImage = rotate((BufferedImage) upArrowImage, 180);
	    leftArrowImage = rotate((BufferedImage) upArrowImage, 270);

	    dizzyImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/dizzy.png"));

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static BufferedImage rotate(BufferedImage image, int _thetaInDegrees) {
	double _theta = Math.toRadians(_thetaInDegrees);

	AffineTransform xform = new AffineTransform();

	if (image.getWidth() > image.getHeight()) {
	    xform.setToTranslation(0.5 * image.getWidth(), 0.5 * image.getWidth());
	    xform.rotate(_theta);

	    int diff = image.getWidth() - image.getHeight();

	    switch (_thetaInDegrees) {
	    case 90:
		xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth() + diff);
		break;
	    case 180:
		xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth() + diff);
		break;
	    default:
		xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth());
		break;
	    }
	} else if (image.getHeight() > image.getWidth()) {
	    xform.setToTranslation(0.5 * image.getHeight(), 0.5 * image.getHeight());
	    xform.rotate(_theta);

	    int diff = image.getHeight() - image.getWidth();

	    switch (_thetaInDegrees) {
	    case 180:
		xform.translate(-0.5 * image.getHeight() + diff, -0.5 * image.getHeight());
		break;
	    case 270:
		xform.translate(-0.5 * image.getHeight() + diff, -0.5 * image.getHeight());
		break;
	    default:
		xform.translate(-0.5 * image.getHeight(), -0.5 * image.getHeight());
		break;
	    }
	} else {
	    xform.setToTranslation(0.5 * image.getWidth(), 0.5 * image.getHeight());
	    xform.rotate(_theta);
	    xform.translate(-0.5 * image.getHeight(), -0.5 * image.getWidth());
	}

	AffineTransformOp op = new AffineTransformOp(xform, AffineTransformOp.TYPE_BILINEAR);

	return op.filter(image, null);
    }
}