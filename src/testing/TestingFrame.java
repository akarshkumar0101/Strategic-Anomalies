package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import game.Communication;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Path;
import game.board.PathFinder;
import game.board.Square;
import game.unit.Unit;
import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Archer;
import game.unit.listofunits.DarkMagicWitch;
import game.unit.listofunits.Guardian;
import game.unit.listofunits.Hunter;
import game.unit.listofunits.LightMagicWitch;
import game.unit.listofunits.Lightningmancer;
import game.unit.listofunits.Pyromancer;
import game.unit.listofunits.Scout;
import game.unit.listofunits.Warrior;
import game.unit.property.ability.ActiveTargetAbilityProperty;

//TODO MAKE SURE YOU USE JAVAFX IN FINAL VERSION
public class TestingFrame extends JFrame {

    private static final long serialVersionUID = 5606773788174572563L;

    static {
	try {
	    // UIManager.LookAndFeelInfo[] looks =
	    // UIManager.getInstalledLookAndFeels();
	    // javax.swing.plaf.metal.MetalLookAndFeel
	    // javax.swing.plaf.nimbus.NimbusLookAndFeel
	    // com.sun.java.swing.plaf.motif.MotifLookAndFeel
	    // com.sun.java.swing.plaf.windows.WindowsLookAndFeel
	    // com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
	} catch (Exception e) {
	    System.out.println("Didnt find look and feel");
	}
    }

    private final TestingGame game;
    private final Board board;
    private final TestingPlayer[] localFramePlayers;
    // player that will be used to get announcements from game to update
    // testingframe
    private final TestingPlayer receivingPlayer;

    private final GamePanel gamePanel;
    private final GameDataPanel gameDataPanel;

    private final GridBagLayout gbLayout;
    private final GridBagConstraints gbConstrains;

    private final Thread gameAnnouncementThread;

    public TestingFrame(TestingGame game, TestingPlayer... localFramePlayers) {
	super("Testing Frame for Strategic Anomalies");
	this.game = game;
	board = game.getBoard();
	this.localFramePlayers = localFramePlayers;
	receivingPlayer = localFramePlayers[0];

	gamePanel = new GamePanel();
	gameDataPanel = new GameDataPanel();

	gbLayout = new GridBagLayout();
	gbConstrains = new GridBagConstraints();

	organizeComponents();

	// setSize(1450, 1000);
	pack();
	setResizable(false);

	gameAnnouncementThread = new FrameUpdatingThread();

	gameAnnouncementThread.start();

	gameDataPanel.resetForNewTurn();

    }

    class FrameUpdatingThread extends Thread {
	private Communication receiveComm;

	@Override
	public void run() {
	    // TODO manage if player quits, etc.
	    receiveComm = receivingPlayer.getGameComm();

	    // TODO add stop statement
	    // game loop for different turns
	    while (true) {
		handleTurn();
		updateInformation();
		repaint();
		gameDataPanel.resetForNewTurn();
	    }

	}

	public void handleTurn() {
	    boolean shouldRun = true;
	    while (shouldRun) {
		shouldRun = handleCommand();
	    }
	}

	public boolean handleCommand() {
	    return handleCommand(receiveComm.recieveObject());
	}

	public boolean handleCommand(Object command) {
	    if (!Message.isCommand(command)) {
		throw new RuntimeException("Not a command");
	    }

	    if (Message.END_TURN.equals(command)) {
		return false;
	    }

	    Object specifications = receiveComm.recieveObject();

	    if (Message.isCommand(specifications)) {
		return handleCommand(specifications);
	    }

	    if (command.equals(Message.HOVER)) {
		hover((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_SELECT)) {
		unitSelect((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_MOVE)) {
		unitMove((Path) specifications, (Coordinate) receiveComm.recieveObject());
	    } else if (command.equals(Message.UNIT_ATTACK)) {
		unitAttack((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_DIR)) {
		unitChangeDir((Direction) specifications);
	    }
	    return true;
	}

	public void hover(Coordinate coor) {
	}

	public void unitSelect(Coordinate coor) {
	}

	public void unitMove(Path path, Coordinate coor) {
	    updateInformation();
	    repaint();
	}

	public void unitAttack(Coordinate coor) {
	    updateInformation();
	    repaint();
	}

	public void unitChangeDir(Direction dir) {
	    updateInformation();
	    repaint();
	}

    }

    public boolean playerIsUsingThisFrame(TestingPlayer player) {
	for (TestingPlayer p : localFramePlayers) {
	    if (p.equals(player)) {
		return true;
	    }
	}
	return false;
    }

    public void transmitDataToGame(Object data) {
	TestingPlayer currentPlayer = (TestingPlayer) game.getCurrentTurn().getPlayerTurn();
	if (!playerIsUsingThisFrame(currentPlayer)) {
	    return;
	}
	Communication gameComm = currentPlayer.getGameComm();
	gameComm.sendObject(data);
    }

    public void organizeComponents() {

	getContentPane().setLayout(gbLayout);

	gbConstrains.gridx = 0;
	gbConstrains.gridy = 0;
	gbConstrains.weightx = 0;
	gbConstrains.weighty = 1;
	gbConstrains.fill = GridBagConstraints.BOTH;
	gbConstrains.anchor = GridBagConstraints.CENTER;

	getContentPane().add(gamePanel, gbConstrains);

	gbConstrains.gridx = 1;
	gbConstrains.gridy = 0;
	gbConstrains.weightx = 1;
	gbConstrains.weighty = 1;
	gbConstrains.fill = GridBagConstraints.BOTH;
	gbConstrains.anchor = GridBagConstraints.CENTER;

	getContentPane().add(gameDataPanel, gbConstrains);

    }

    public void updateInformation() {
	gamePanel.updateInformation();
	gameDataPanel.updateInformation();
    }

    public static double scale(double num, double ori1, double ori2, double new1, double new2) {
	double scale = (new1 - new2) / (ori1 - ori2);
	return num * scale + new1;
    }

    private static final Color slightBlue = new Color(192, 192, 220), slightRed = new Color(220, 192, 192);

    private static Color darkerColor(Color col, int amount) {
	return new Color(Math.max(col.getRed() - amount, 0), Math.max(col.getGreen() - amount, 0),
		Math.max(col.getBlue() - amount, 0));
    }

    private static Color combineColors(Color col1, Color col2) {
	return new Color(Math.min(col1.getRed() + col2.getRed(), 255), Math.min(col1.getGreen() + col2.getGreen(), 255),
		Math.min(col1.getBlue() + col2.getBlue(), 255));
    }

    class GamePanel extends JPanel {

	private static final long serialVersionUID = 7783998123812310360L;

	private final GridLayout gridLayout;
	private final SquareLabel[][] labels;

	private Square mouseInSquare;

	public GamePanel() {
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

	public void setMouseInSquare(Square sqr) {
	    if (mouseInSquare != sqr) {
		mouseInSquare = sqr;
		gameDataPanel.updateUnitInfoLabels();
	    }
	}

	public void updateInformation() {
	    for (int y = 0; y < board.getHeight(); y++) {
		for (int x = 0; x < board.getWidth(); x++) {
		    labels[x][y].updateInformation();
		}
	    }
	}

	public void updateAllCanCurrentlyClick() {
	    for (int y = 0; y < board.getHeight(); y++) {
		for (int x = 0; x < board.getWidth(); x++) {
		    labels[x][y].updateCanCurrentlyClick();
		}
	    }
	}

	class SquareLabel extends JComponent implements MouseListener {

	    private static final long serialVersionUID = 7959593291619934967L;

	    private boolean mouseIn;
	    private boolean mousePressing;

	    private boolean canCurrentlyClick = true;

	    private Color currentBackgroundColor;

	    private final Square sqr;
	    private Unit unitOnTop;
	    private Image unitImg;
	    private Image waitingImg;
	    private Image dizzyImg;

	    public SquareLabel(Square sqr) {
		this.sqr = sqr;
		if (sqr != null) {
		    setToolTipText(sqr.getCoor().toString());
		}
		addMouseListener(this);
	    }

	    public void updateInformation() {
		unitOnTop = sqr == null ? null : sqr.getUnitOnTop();

		waitingImg = null;
		dizzyImg = null;
		if (unitOnTop == null) {
		    unitImg = null;
		} else {
		    if (unitOnTop.getStunnedProp().getCurrentPropertyValue()) {
			dizzyImg = Images.stunnedImage;
		    }
		    if (unitOnTop.getWaitProp().isWaiting()) {
			waitingImg = Images.waitingImage;
		    }

		    Class<? extends Unit> clazz = unitOnTop.getClass();
		    unitImg = Images.getImage(clazz);
		}
		updateCanCurrentlyClick();
	    }

	    public void updateCanCurrentlyClick() {
		canCurrentlyClick = sqr == null ? false : gameDataPanel.canCurrentlyClick(sqr);
	    }

	    public Color determineBackgroundColor() {
		if (sqr == null) {
		    return Color.black;
		}

		Color col = Color.lightGray;

		if (unitOnTop != null) {
		    Player owner = unitOnTop.getOwnerProp().getCurrentPropertyValue();

		    if (owner.equals(game.getPlayer1())) {
			col = TestingFrame.slightBlue;
		    } else if (owner.equals(game.getPlayer2())) {
			col = TestingFrame.slightRed;
		    }
		}

		if (mousePressing) {
		    col = TestingFrame.darkerColor(col, 50);
		} else if (mouseIn) {
		    col = TestingFrame.darkerColor(col, 25);
		}
		col = TestingFrame.combineColors(col,
			gameDataPanel.colorsDisplayed[sqr.getCoor().x()][sqr.getCoor().y()]);
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
		if (unitOnTop == null) {
		    return;
		}

		// draw image in the center
		double ratio = (double) getHeight() / unitImg.getHeight(null);
		int imgWidth = (int) (ratio * unitImg.getWidth(null)), imgHeight = getHeight();
		g.drawImage(unitImg, (getWidth() - imgWidth) / 2, (getHeight() - imgHeight) / 2, imgWidth, imgHeight,
			null);

		// draw waiting image (if stunned)
		double smallpiclen = .3;
		g.drawImage(waitingImg, (int) (getWidth() * (1 - smallpiclen)), (int) (getHeight() * (1 - smallpiclen)),
			(int) (getWidth() * smallpiclen), (int) (getHeight() * smallpiclen), null);
		// draw stunned image
		g.drawImage(dizzyImg, 0, (int) (getHeight() * (1 - smallpiclen)), (int) (getWidth() * smallpiclen),
			(int) (getHeight() * smallpiclen), null);

		// draw health bar
		double healthPercentage = unitOnTop.getHealthProp().currentPercentageHealth();
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
		if (sqr != null && canCurrentlyClick) {
		    gameDataPanel.coordinateClicked(sqr.getCoor());
		}
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		if (canCurrentlyClick) {
		    mousePressing = true;
		    repaint();
		}
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		if (mousePressing) {
		    mousePressing = false;
		    repaint();
		}
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
	}
    }

    class GameDataPanel extends JPanel {
	private static final long serialVersionUID = -4840314334092192454L;

	private final GridBagLayout gdpgbLayout;
	private final GridBagConstraints gdpgbConstrains;

	private final JLabel turnInfoLabel;

	private final PickingButton pickUnitTButton;
	private final PickingButton pickMoveTButton;
	private final PickingButton pickAttackTButton;
	private final PickingButton pickDirectionTButton;
	private final JButton endTurnButton;

	private final JButton upDirButton;
	private final JButton leftDirButton;
	private final JButton rightDirButton;
	private final JButton downDirButton;

	private final JSeparator commandInfoSeperator;

	private final JLabel unitInfoLabel1;
	private final JLabel unitInfoLabel2;

	private JLabel pointHoverUnitLabel;
	private JLabel pointPickedUnitLabel;

	private PickingButton currentlyPicking;

	private Unit unitPicked;

	public final Color[][] colorsDisplayed;

	public GameDataPanel() {
	    super();

	    currentlyPicking = null;

	    colorsDisplayed = new Color[board.getWidth()][board.getHeight()];

	    gdpgbLayout = new GridBagLayout();
	    gdpgbConstrains = new GridBagConstraints();

	    pickUnitTButton = new PickingButton("  Pick   ");
	    pickMoveTButton = new PickingButton("  Move   ");
	    pickAttackTButton = new PickingButton(" Attack  ");
	    pickDirectionTButton = new PickingButton("Direction");

	    endTurnButton = new JButton("End Turn ");
	    endTurnButton.setPreferredSize(new Dimension(100, 100));

	    int arrowLength = 40;
	    upDirButton = new JButton() {
		private static final long serialVersionUID = -1992040650621842214L;

		@Override
		public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    g.drawImage(Images.upArrowImage, (getWidth() - arrowLength) / 2, (getHeight() - arrowLength) / 2,
			    arrowLength, arrowLength, null);
		}
	    };
	    upDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
	    leftDirButton = new JButton() {
		private static final long serialVersionUID = -3786913193009448314L;

		@Override
		public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    g.drawImage(Images.leftArrowImage, (getWidth() - arrowLength) / 2, (getHeight() - arrowLength) / 2,
			    arrowLength, arrowLength, null);
		}
	    };
	    leftDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
	    rightDirButton = new JButton() {
		private static final long serialVersionUID = 363437984462915163L;

		@Override
		public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    g.drawImage(Images.rightArrowImage, (getWidth() - arrowLength) / 2, (getHeight() - arrowLength) / 2,
			    arrowLength, arrowLength, null);
		}
	    };
	    rightDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
	    downDirButton = new JButton() {
		private static final long serialVersionUID = -6210276380909591358L;

		@Override
		public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    g.drawImage(Images.downArrowImage, (getWidth() - arrowLength) / 2, (getHeight() - arrowLength) / 2,
			    arrowLength, arrowLength, null);
		}
	    };
	    downDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));

	    commandInfoSeperator = new JSeparator();
	    commandInfoSeperator.setPreferredSize(new Dimension(1, 10));

	    turnInfoLabel = new JLabel("[player's name]");
	    unitInfoLabel1 = new JLabel();
	    unitInfoLabel1.setVerticalAlignment(SwingConstants.TOP);
	    unitInfoLabel2 = new JLabel();
	    unitInfoLabel2.setVerticalAlignment(SwingConstants.TOP);

	    organizeComponents();
	    setupButtonLogic();

	    setBackground(TestingFrame.darkerColor(Color.lightGray, 30));
	}

	public void organizeComponents() {

	    setLayout(gdpgbLayout);

	    int gridy = 0;

	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 5;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    turnInfoLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
	    turnInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    add(turnInfoLabel, gdpgbConstrains);

	    Font normalFont = new Font("Times New Roman", Font.PLAIN, 20);
	    int gap = 10;
	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    gdpgbConstrains.insets = new Insets(gap, 0, gap, 0);
	    pickUnitTButton.setFont(normalFont);
	    add(pickUnitTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 1;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    pickMoveTButton.setFont(normalFont);
	    add(pickMoveTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 2;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    pickAttackTButton.setFont(normalFont);
	    add(pickAttackTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 3;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    pickDirectionTButton.setFont(normalFont);
	    add(pickDirectionTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 4;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    endTurnButton.setFont(normalFont);
	    add(endTurnButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 2;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.insets = new Insets(0, 0, 0, 0);
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(upDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 1;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(leftDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 3;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(rightDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 2;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(downDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 5;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(commandInfoSeperator, gdpgbConstrains);

	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 5;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    unitInfoLabel1.setFont(normalFont);
	    add(unitInfoLabel1, gdpgbConstrains);

	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 1;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 5;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    unitInfoLabel2.setFont(normalFont);
	    add(unitInfoLabel2, gdpgbConstrains);

	}

	public void setupButtonLogic() {
	    pickUnitTButton.addActionListener(e -> {
		setCurrentlyPicking(pickUnitTButton);
		GameDataPanel.this.requestFocus();
	    });
	    pickMoveTButton.addActionListener(e -> {
		setCurrentlyPicking(pickMoveTButton);
		GameDataPanel.this.requestFocus();
	    });
	    pickAttackTButton.addActionListener(e -> {
		setCurrentlyPicking(pickAttackTButton);
		GameDataPanel.this.requestFocus();
	    });
	    pickDirectionTButton.addActionListener(e -> {
		setCurrentlyPicking(pickDirectionTButton);
		GameDataPanel.this.requestFocus();
	    });
	    endTurnButton.addActionListener(e -> {
		transmitDataToGame(Message.END_TURN);
		GameDataPanel.this.requestFocus();
	    });
	    upDirButton.addActionListener(e -> {
		directionClicked(Direction.UP);
		GameDataPanel.this.requestFocus();
	    });
	    leftDirButton.addActionListener(e -> {
		directionClicked(Direction.LEFT);
		GameDataPanel.this.requestFocus();
	    });
	    rightDirButton.addActionListener(e -> {
		directionClicked(Direction.RIGHT);
		GameDataPanel.this.requestFocus();
	    });
	    downDirButton.addActionListener(e -> {
		directionClicked(Direction.DOWN);
		GameDataPanel.this.requestFocus();
	    });

	    setFocusable(true);
	    addKeyListener(new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			naturalNextPick();
		    }
		}
	    });
	}

	public void updateInformation() {
	    turnInfoLabel.setText(((TestingPlayer) game.getCurrentTurn().getPlayerTurn()).getName() + "'s turn");
	    updateUnitInfoLabels();
	}

	public void updateUnitInfoLabels() {
	    if (pickUnitTButton.hasPicked) {
		pointPickedUnitLabel = unitInfoLabel1;
		pointHoverUnitLabel = unitInfoLabel2;
	    } else {
		pointHoverUnitLabel = unitInfoLabel1;
		pointPickedUnitLabel = unitInfoLabel2;
	    }
	    Square mouseinsqr = gamePanel.mouseInSquare;
	    pointHoverUnitLabel.setText(
		    getHTMLabelInfoString(mouseinsqr == null ? null : mouseinsqr.getUnitOnTop(), "Hovering Over Unit"));
	    pointPickedUnitLabel.setText(getHTMLabelInfoString(unitPicked, "Selected Unit"));

	}

	public boolean canCurrentlyClick(Square sqr) {
	    if (currentlyPicking == pickUnitTButton) {
		Unit unit = sqr.getUnitOnTop();
		if (unit != null
			&& unit.getOwnerProp().getCurrentPropertyValue() == game.getCurrentTurn().getPlayerTurn()) {
		    return true;
		}
	    } else if (currentlyPicking == pickMoveTButton) {
		return true;
	    } else if (currentlyPicking == pickAttackTButton) {
		return true;
	    } else if (currentlyPicking == pickDirectionTButton) {
		return false;
	    }
	    return false;
	}

	public void updateColorsDisplayed() {
	    for (Square sqr : board) {
		Coordinate coor = sqr.getCoor();
		Unit unit = sqr.getUnitOnTop();

		colorsDisplayed[coor.x()][coor.y()] = Color.black;

		if (currentlyPicking == pickUnitTButton) {
		    if (unit != null
			    && unit.getOwnerProp().getCurrentPropertyValue() == game.getCurrentTurn().getPlayerTurn()) {
			colorsDisplayed[coor.x()][coor.y()] = Color.blue;
		    }
		} else if (currentlyPicking == pickMoveTButton) {
		    // if (unitPicked.getGamePathTo(coor) != null) {
		    // colorsDisplayed[coor.x()][coor.y()] = Color.green;
		    // }
		    if (PathFinder.hasClearPathTo(unitPicked, unitPicked.getPosProp().getCurrentPropertyValue(), coor,
			    unitPicked.getMovingProp().getCurrentPropertyValue())) {
			colorsDisplayed[coor.x()][coor.y()] = Color.green;
		    }
		} else if (currentlyPicking == pickAttackTButton) {
		    if (unitPicked.getAbilityProp() instanceof ActiveTargetAbilityProperty
			    && ((ActiveTargetAbilityProperty) unitPicked.getAbilityProp()).canUseAbilityOn(sqr)) {
			colorsDisplayed[coor.x()][coor.y()] = Color.red;
		    }
		} else if (currentlyPicking == pickDirectionTButton) {

		}

	    }
	}

	public void coordinateClicked(Coordinate coor) {
	    if (currentlyPicking == pickUnitTButton || currentlyPicking == pickMoveTButton
		    || currentlyPicking == pickAttackTButton) {
		if (currentlyPicking.hasPicked) {
		    return;
		}
		if (currentlyPicking == pickUnitTButton) {
		    unitPicked = board.getUnitAt(coor);
		}
		currentlyPicking.hasPicked = true;
		transmitDataToGame(coor);
		naturalNextPick();
	    }
	    updateColorsDisplayed();
	    gamePanel.repaint();
	}

	public void directionClicked(Direction dir) {
	    transmitDataToGame(dir);
	    pickDirectionTButton.hasPicked = true;
	}

	public void enableAllMoveButtons(boolean enable) {
	    if (enable) {
		pickUnitTButton.setSelected(false);
		pickMoveTButton.setSelected(false);
		pickAttackTButton.setSelected(false);
		pickDirectionTButton.setSelected(false);
	    }
	    pickUnitTButton.setEnabled(enable);
	    pickMoveTButton.setEnabled(enable);
	    pickAttackTButton.setEnabled(enable);
	    pickDirectionTButton.setEnabled(enable);
	    endTurnButton.setEnabled(enable);

	    enableAllDirButtons(false);

	    pickUnitTButton.hasPicked = pickMoveTButton.hasPicked = pickAttackTButton.hasPicked = pickDirectionTButton.hasPicked = false;
	}

	public void resetForNewTurn() {
	    unitPicked = null;
	    boolean inputIsEnabled = inputIsEnabled();
	    enableAllMoveButtons(inputIsEnabled);
	    currentlyPicking = null;
	    if (inputIsEnabled) {
		setCurrentlyPicking(pickUnitTButton);
	    }
	    updateInformation();
	    repaint();
	}

	public boolean inputIsEnabled() {
	    return playerIsUsingThisFrame((TestingPlayer) game.getCurrentTurn().getPlayerTurn());
	}

	public void enableAllDirButtons(boolean enable) {
	    upDirButton.setEnabled(enable);
	    leftDirButton.setEnabled(enable);
	    rightDirButton.setEnabled(enable);
	    downDirButton.setEnabled(enable);
	}

	public void setCurrentlyPicking(PickingButton currentlyPicking) {
	    if (this.currentlyPicking == currentlyPicking) {
		currentlyPicking.setSelected(!currentlyPicking.isSelected());
		return;
	    }
	    PickingButton prev = this.currentlyPicking;

	    if (prev == pickUnitTButton && !pickUnitTButton.hasPicked) {
		currentlyPicking.setSelected(false);
		System.out.println("Must pick unit first before continuing");
		return;
	    }
	    if (prev == pickUnitTButton) {
		updateUnitInfoLabels();
	    }

	    this.currentlyPicking = currentlyPicking;
	    if (prev != null) {
		if (prev.hasPicked) {
		    prev.setEnabled(false);
		} else {
		    prev.setEnabled(true);
		    prev.setSelected(false);
		}
		prev.repaint();
	    }
	    if (this.currentlyPicking != null) {

		if (this.currentlyPicking == pickUnitTButton) {
		    transmitDataToGame(Message.UNIT_SELECT);
		} else if (this.currentlyPicking == pickMoveTButton) {
		    transmitDataToGame(Message.UNIT_MOVE);
		} else if (this.currentlyPicking == pickAttackTButton) {
		    transmitDataToGame(Message.UNIT_ATTACK);
		} else if (this.currentlyPicking == pickDirectionTButton) {
		    transmitDataToGame(Message.UNIT_DIR);
		}

		this.currentlyPicking.setSelected(true);
		this.currentlyPicking.repaint();
	    }
	    if (this.currentlyPicking == pickDirectionTButton) {
		enableAllDirButtons(true);
	    } else {
		enableAllDirButtons(false);
	    }
	    gamePanel.updateAllCanCurrentlyClick();

	    updateColorsDisplayed();
	    gamePanel.repaint();
	}

	public void naturalNextPick() {
	    if (currentlyPicking == pickUnitTButton && pickUnitTButton.hasPicked) {
		setCurrentlyPicking(pickMoveTButton);
	    } else if (currentlyPicking == pickMoveTButton) {
		setCurrentlyPicking(pickAttackTButton);
	    } else if (currentlyPicking == pickAttackTButton) {
		setCurrentlyPicking(pickDirectionTButton);
	    } else if (currentlyPicking == pickDirectionTButton) {
		endTurnButton.doClick();
	    }
	}

	private String getHTMLabelInfoString(Unit unit, String title) {
	    if (unit == null) {
		return "";
	    } else {
		String str = "<html>";

		str += "<strong><u>" + title + "</u></strong>";

		TestingPlayer owner = (TestingPlayer) unit.getOwnerProp().getCurrentPropertyValue();
		str += "<br>" + owner.getName() + "'s " + unit.getClass().getSimpleName();

		int health = unit.getHealthProp().getCurrentPropertyValue();
		double percentHealth = unit.getHealthProp().currentPercentageHealth();
		str += "<br>Health: "
			+ colorize(health + "(" + (int) (percentHealth * 100) + "%)", percentHealth, 1, true);

		int power = unit.getAbilityProp().getCurrentPropertyValue();
		int defaultPower = unit.getAbilityProp().getDefaultPropertyValue();
		str += "<br>Power: " + colorize(power + "", power, defaultPower, true);

		int armor = unit.getHealthProp().getArmorProp().getCurrentPropertyValue();
		int defaultArmor = unit.getHealthProp().getArmorProp().getDefaultPropertyValue();
		str += "<br>Armor: " + colorize(armor + "", armor, defaultArmor, true);

		if (unit.getStunnedProp().getCurrentPropertyValue()) {
		    str += "<br>" + colorize("stunned*", 1, 2, true);
		}
		if (unit.getWaitProp().isWaiting()) {
		    str += "<br>" + colorize("is waiting*", 1, 2, true);
		}

		str += "</html>";
		return str;
	    }
	}

	private String colorize(String str, double value, double defaultValue, boolean greaterIsGreen) {

	    if (value == defaultValue) {
		return str;
	    } else if (!(value > defaultValue ^ greaterIsGreen)) {
		// green
		return "<font color=\"green\">" + str + "</font>";
	    } else {
		// red
		return "<font color=\"red\">" + str + "</font>";
	    }
	}

	class PickingButton extends JToggleButton {
	    private static final long serialVersionUID = 2238679707104152403L;

	    private boolean hasPicked;

	    public PickingButton(String text) {
		super(text);
		hasPicked = false;
	    }

	    @Override
	    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (currentlyPicking == this) {
		    g.setColor(Color.red);
		    g.fillRect(0, getHeight() - 7, getWidth(), getHeight());
		}
	    }
	}

    }

}

class Images {

    public static Image warriorImage;
    public static Image guardianImage;

    public static Image pyromancerImage;
    public static Image aquamancerImage;
    public static Image lightningmancerImage;

    public static Image scoutImage;
    public static Image archerImage;
    public static Image hunterImage;

    public static Image darkmagicwitchImage;
    public static Image lightmagicwitchImage;

    public static Image upArrowImage;
    public static Image rightArrowImage;
    public static Image downArrowImage;
    public static Image leftArrowImage;

    public static Image stunnedImage;
    public static Image waitingImage;

    static {
	try {
	    Images.warriorImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/warrior.png"));
	    Images.guardianImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/guardian.png"));

	    Images.pyromancerImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/pyromancer.png"));
	    Images.aquamancerImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/aquamancer.png"));
	    Images.lightningmancerImage = ImageIO
		    .read(TestingFrame.class.getResourceAsStream("/temp_pics/lightningmancer.png"));

	    Images.scoutImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/scout.png"));
	    Images.archerImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/archer.png"));
	    Images.hunterImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/hunter.png"));

	    Images.darkmagicwitchImage = ImageIO
		    .read(TestingFrame.class.getResourceAsStream("/temp_pics/darkmagicwitch.png"));
	    Images.lightmagicwitchImage = ImageIO
		    .read(TestingFrame.class.getResourceAsStream("/temp_pics/lightmagicwitch.png"));

	    Images.upArrowImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/redarrow.png"));
	    Images.rightArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 90);
	    Images.downArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 180);
	    Images.leftArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 270);

	    Images.stunnedImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/dizzy.png"));

	    Images.waitingImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/waiting.png"));

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static Image getImage(Class<? extends Unit> unitClass) {
	if (unitClass == Warrior.class) {
	    return Images.warriorImage;
	} else if (unitClass == Guardian.class) {
	    return Images.guardianImage;
	} else if (unitClass == Pyromancer.class) {
	    return Images.pyromancerImage;
	} else if (unitClass == Aquamancer.class) {
	    return Images.aquamancerImage;
	} else if (unitClass == Lightningmancer.class) {
	    return Images.lightningmancerImage;
	} else if (unitClass == Scout.class) {
	    return Images.scoutImage;
	} else if (unitClass == Archer.class) {
	    return Images.archerImage;
	} else if (unitClass == Hunter.class) {
	    return Images.hunterImage;
	} else if (unitClass == DarkMagicWitch.class) {
	    return Images.darkmagicwitchImage;
	} else if (unitClass == LightMagicWitch.class) {
	    return Images.lightmagicwitchImage;
	} else {
	    return null;
	}
    }

    // the following is not my code
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