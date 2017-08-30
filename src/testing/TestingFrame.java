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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import game.Communication;
import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Path;
import game.board.Square;
import game.unit.Unit;
import game.unit.property.ability.AbilityAOE;
import game.unit.property.ability.AbilityPower;
import game.unit.property.ability.AbilityRange;
import game.unit.property.ability.ActiveAbility;

//TODO MAKE SURE YOU USE JAVAFX IN FINAL VERSION
public class TestingFrame {

    final Game game;
    final Board board;
    final Player localPlayer;

    private final TestingFrameGUI gui;

    private final FrameUpdatingThread frameUpdatingThread;

    private boolean doNaturalNextPick = true;

    private final Object dataTransferLock = new Object();

    final Map<Coordinate, List<Square>> aoeHighlightData;

    public TestingFrame(Game game, Player localPlayer) {
	this.game = game;
	board = game.getBoard();
	this.localPlayer = localPlayer;

	gui = new TestingFrameGUI(this);
	gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	frameUpdatingThread = new FrameUpdatingThread();

	aoeHighlightData = new HashMap<>();
    }

    public void startFrame() {
	setupBlockAnimationTriggers();
	frameUpdatingThread.start();
	gui.setVisible(true);
    }

    private void setupBlockAnimationTriggers() {
	for (Unit unit : game.getAllUnits()) {
	    unit.getHealthProp().getArmorProp().getBlockReporter().add(specifications -> {
		gui.triggerBlockAnimation(board.getSquare(unit.getPosProp().getValue()));
	    });
	}
    }

    Message currentlyPicking;

    private Coordinate opponentHover = null;
    private final Object opponentHoverLockObj = new Object();

    public Coordinate getOpponentHover() {
	synchronized (opponentHoverLockObj) {
	    return opponentHover;
	}
    }

    public void setOpponentHover(Coordinate coor) {
	synchronized (opponentHoverLockObj) {
	    opponentHover = coor;
	}
    }

    class FrameUpdatingThread extends Thread {

	private Communication receiveComm;

	@Override
	public void run() {
	    // TODO manage if player quits, etc.
	    receiveComm = localPlayer.getGameComm();

	    // TODO add stop statement
	    // game loop for different turns
	    if (!Game.START_GAME.equals(receiveComm.recieveObject())) {
		throw new RuntimeException("Could not properly start frame with game");
	    }
	    // game is ready to be played
	    gui.updateInformation();
	    gui.repaint();
	    while (true) {
		aoeHighlightData.clear();
		handleTurn();
		gui.updateInformation();
		gui.repaint();
	    }

	}

	public void handleTurn() {
	    setOpponentHover(null);
	    boolean shouldRun = true;
	    while (shouldRun) {
		requestNextNaturalPick();

		currentlyPicking = null;

		shouldRun = handleCommand();

		gui.updateInformation();
		gui.gameDataPanel.setCurrentlyPicking(null);
		gui.repaint();
	    }
	}

	public boolean handleCommand() {
	    Message command = null;
	    Object specs = null;

	    boolean run = false;
	    do {
		run = false;

		Object obj = receiveComm.recieveObject();
		if (Message.HOVER.equals(obj)) {
		    hover((Coordinate) receiveComm.recieveObject());
		    run = true;
		} else if (obj instanceof Message) {
		    command = (Message) obj;
		    handlePartialCoreCommand(command);
		    run = true;

		    if (Message.END_TURN.equals(command)) {
			return false;
		    }
		} else {
		    specs = obj;
		    run = false;
		}

	    } while (run);

	    handleFullCoreCommand(command, specs);
	    return true;
	}

	public void handlePartialCoreCommand(Message command) {

	    currentlyPicking = command;

	    if (command.equals(Message.UNIT_SELECT)) {
		unitSelect();
	    } else if (command.equals(Message.UNIT_MOVE)) {
		unitMove();
	    } else if (command.equals(Message.UNIT_ATTACK)) {
		unitAttack();
	    } else if (command.equals(Message.UNIT_DIR)) {
		unitChangeDir();
	    } else if (command.equals(Message.END_TURN)) {
		endTurn();
	    }
	    gui.updateInformation();
	    gui.gameDataPanel.setCurrentlyPicking(command);
	    gui.repaint();
	}

	public void handleFullCoreCommand(Message command, Object specs) {

	    if (command.equals(Message.UNIT_SELECT)) {
		unitSelect((Coordinate) specs);
	    } else if (command.equals(Message.UNIT_MOVE)) {
		unitMove((Path) specs, (Coordinate) receiveComm.recieveObject());
	    } else if (command.equals(Message.UNIT_ATTACK)) {
		unitAttack((Coordinate) specs);
	    } else if (command.equals(Message.UNIT_DIR)) {
		unitChangeDir((Direction) specs);
	    }
	    gui.updateInformation();
	    gui.repaint();

	}

	public void hover(Coordinate coor) {
	    setOpponentHover(coor);
	    gui.repaint();
	}

	public void unitSelect() {
	}

	public void unitSelect(Coordinate coor) {
	}

	public void unitMove() {
	}

	public void unitMove(Path path, Coordinate coor) {
	}

	public void unitAttack() {
	}

	public void unitAttack(Coordinate coor) {
	}

	public void unitChangeDir() {
	}

	public void unitChangeDir(Direction dir) {
	}

	public void endTurn() {
	    // TODO feature to show end turn button click to other player?
	    // be careful bc turn has already changed at this point
	}
    }

    public boolean isLocalPlayerTurn() {
	return localPlayer.equals(game.getCurrentTurn().getPlayerTurn());
    }

    private void transmitDataToGame(Object data) {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	Communication gameComm = localPlayer.getGameComm();
	// Systemf.out.println("writing data to players out " + data);
	gameComm.sendObject(data);
    }

    public Square getSquare(Coordinate coor) {
	return board.getSquare(coor);
    }

    public void resetForNewTurnf() {

    }

    public boolean canCurrentlyClick(Coordinate coor) {
	if (!board.isInBoard(coor)) {
	    return false;
	}
	if (currentlyPicking == Message.UNIT_SELECT) {
	    return game.canSelectUnit(coor);
	} else if (currentlyPicking == Message.UNIT_MOVE) {
	    return game.canMoveTo(coor);
	} else if (currentlyPicking == Message.UNIT_ATTACK) {
	    return game.canAttack(coor);
	}
	return false;
    }

    public boolean canCurrentlyChangeDir(Direction dir) {
	return game.canChangeDir(dir);
    }

    public void requestNextNaturalPick() {
	if (!isLocalPlayerTurn() || !doNaturalNextPick) {
	    return;
	}
	// TODO add conditions for if not target ability, can't move piece etc.
	if (!game.hasSelectedUnit()) {
	    pickUnitButtonClicked();
	} else if (!game.hasMoved()) {
	    pickMoveButtonClicked();
	} else if (!game.hasAttacked()) {
	    if (game.getSelectedUnit().getAbility() instanceof ActiveAbility) {
		pickAttackButtonClicked();
	    }
	} else if (!game.hasChangedDir()) {
	    pickDirectionButtonClicked();
	} else {
	    // this seems kind of weird tbh lol
	    // endTurnButtonClicked();
	}

    }

    public void pickUnitButtonClicked() {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	if (game.hasSelectedUnit()) {
	    throw new RuntimeException("Already selected unit");
	}
	transmitDataToGame(Message.UNIT_SELECT);
    }

    public void pickMoveButtonClicked() {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	if (game.hasMoved()) {
	    throw new RuntimeException("Already moved");
	}
	if (!game.hasSelectedUnit()) {
	    throw new RuntimeException("Hasn't selected unit");
	}
	transmitDataToGame(Message.UNIT_MOVE);
    }

    public void pickAttackButtonClicked() {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	if (game.hasAttacked()) {
	    throw new RuntimeException("Already attacked");
	}
	if (!game.hasSelectedUnit()) {
	    throw new RuntimeException("Hasn't selected unit");
	}

	if (game.getSelectedUnit().getAbility() instanceof AbilityAOE) {
	    for (Square sqr : board) {
		aoeHighlightData.put(sqr.getCoor(),
			((AbilityAOE) game.getSelectedUnit().getAbility()).getAOESqaures(sqr));
	    }
	}

	transmitDataToGame(Message.UNIT_ATTACK);
    }

    public void pickDirectionButtonClicked() {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	if (game.hasChangedDir()) {
	    throw new RuntimeException("Already changed direction");
	}
	if (!game.hasSelectedUnit()) {
	    throw new RuntimeException("Hasn't selected unit");
	}
	transmitDataToGame(Message.UNIT_DIR);
    }

    public void endTurnButtonClicked() {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	transmitDataToGame(Message.END_TURN);
    }

    public void coordinateClicked(Coordinate coor) {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	if (!canCurrentlyClick(coor)) {
	    return;
	}

	transmitDataToGame(coor);
	gui.gamePanel.repaint();
    }

    public void directionClicked(Direction dir) {
	if (!isLocalPlayerTurn()) {
	    return;
	}
	if (!canCurrentlyChangeDir(dir)) {
	    return;
	}
	transmitDataToGame(dir);
    }

    private final Object mouseInLock = new Object();
    private Coordinate mouseInCoordinate;

    public void setMouseInCoordinate(Coordinate mouseInCoordinate) {
	synchronized (mouseInLock) {
	    if (this.mouseInCoordinate != mouseInCoordinate) {
		this.mouseInCoordinate = mouseInCoordinate;
		gui.gameDataPanel.updateUnitInfoLabels();
		gui.gamePanel.repaint();
	    }
	}
    }

    public Square getMouseInSquare() {
	synchronized (mouseInLock) {
	    if (mouseInCoordinate == null || !board.isInBoard(mouseInCoordinate)) {
		return null;
	    } else {
		return board.getSquare(mouseInCoordinate);
	    }
	}
    }

    public void mouseEntered(Coordinate coor) {
	if (board.isInBoard(coor) && isLocalPlayerTurn()) {
	    synchronized (dataTransferLock) {
		transmitDataToGame(Message.HOVER);
		transmitDataToGame(coor);
	    }
	}
	setMouseInCoordinate(coor);
    }

    public void mouseExited(Coordinate coor) {
	if (board.isInBoard(coor) && isLocalPlayerTurn()) {
	    synchronized (dataTransferLock) {
		transmitDataToGame(Message.HOVER);
		transmitDataToGame(null);
	    }
	}
	setMouseInCoordinate(null);
    }
}

class TestingFrameGUI extends JFrame {
    private static final long serialVersionUID = 2570119871946595519L;

    private final TestingFrame testingFrame;

    private final GridBagLayout gbLayout;
    private final GridBagConstraints gbConstrains;

    public GamePanel gamePanel;
    public GameDataPanel gameDataPanel;

    public TestingFrameGUI(TestingFrame testingFrame) {
	super("Testing Frame for Strategic Anomalies");

	this.testingFrame = testingFrame;

	gbLayout = new GridBagLayout();
	gbConstrains = new GridBagConstraints();

	gamePanel = new GamePanel();
	gameDataPanel = new GameDataPanel();

	organizeComponents();

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// setSize(500, 500);
	pack();
	// setResizable(false);
    }

    public void organizeComponents() {
	// this.setContentPane(new JPanel() {
	// private static final long serialVersionUID = -5960100728713917480L;
	//
	// @Override
	// public Dimension getPreferredSize() {
	// return new Dimension(900, 900);
	// }
	// });

	getContentPane().setLayout(gbLayout);

	gbConstrains.gridx = 0;
	gbConstrains.gridy = 0;
	gbConstrains.weightx = 1;
	gbConstrains.weighty = 1;
	gbConstrains.fill = GridBagConstraints.BOTH;
	gbConstrains.anchor = GridBagConstraints.CENTER;

	getContentPane().add(gamePanel, gbConstrains);

	gbConstrains.gridx = 1;
	gbConstrains.gridy = 0;
	gbConstrains.weightx = 0;
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
	double scale = (new2 - new1) / (ori2 - ori1);
	return (num - ori1) * scale + new1;
    }

    public static final Color slightBlue = new Color(192, 192, 255), slightRed = new Color(255, 192, 192),
	    slightGreen = new Color(192, 255, 192);
    public static final Color friendlyUnitColor = new Color(192, 220, 192), enemyUnitColor = new Color(220, 192, 192);
    public static final Color canSelectColor = lighterColor(Color.blue, 150),
	    canMoveColor = lighterColor(Color.blue, 150), canAttackColor = lighterColor(Color.blue, 150);
    public static final Color aoeColor = lighterColor(Color.blue, 200);

    public static final Color gameDataPanelBackgroundColor = lighterColor(Color.lightGray, -30);

    public static Color lighterColor(Color col, int amount) {
	return new Color(makeRGBRange(col.getRed() + amount), makeRGBRange(col.getGreen() + amount),
		makeRGBRange(col.getBlue() + amount));
    }

    public static Color mixColors(Color col1, Color col2) {
	return new Color(makeRGBRange((col1.getRed() + col2.getRed()) / 2),
		makeRGBRange((col1.getGreen() + col2.getGreen()) / 2),
		makeRGBRange((col1.getBlue() + col2.getBlue()) / 2));
    }

    public static Color addColors(Color col1, Color col2) {
	return new Color(makeRGBRange(col1.getRed() + col2.getRed()), makeRGBRange(col1.getGreen() + col2.getGreen()),
		makeRGBRange(col1.getBlue() + col2.getBlue()));
    }

    private static int makeRGBRange(int a) {
	if (a > 255) {
	    return 255;
	} else if (a < 0) {
	    return 0;
	} else {
	    return a;
	}
    }

    private final Vector<Square> blockedSquares = new Vector<>();

    public static final int BLOCK_ANIMATION_TIME = 500;

    public void triggerBlockAnimation(Square sqr) {
	blockedSquares.add(sqr);
	gamePanel.repaint();
	Timer timer = new Timer();
	timer.schedule(new TimerTask() {
	    @Override
	    public void run() {
		blockedSquares.remove(sqr);
		gamePanel.repaint();
	    }
	}, TestingFrameGUI.BLOCK_ANIMATION_TIME);
    }

    class GamePanel extends JPanel {

	private static final long serialVersionUID = 7783998123812310360L;

	private final SquareLabel[][] labels;
	private final GridLayout gridLayout;

	public GamePanel() {
	    super();
	    labels = new SquareLabel[testingFrame.board.getWidth()][testingFrame.board.getHeight()];
	    gridLayout = new GridLayout(testingFrame.board.getHeight(), testingFrame.board.getWidth());

	    for (int x = 0; x < testingFrame.board.getWidth(); x++) {
		for (int y = 0; y < testingFrame.board.getHeight(); y++) {
		    Coordinate coor = new Coordinate(x, y);
		    labels[x][y] = new SquareLabel(coor);
		}
	    }
	    GamePanel.this.organizeComponents();
	}

	public void organizeComponents() {
	    setLayout(gridLayout);
	    for (int y = testingFrame.board.getHeight() - 1; y >= 0; y--) {
		for (int x = 0; x < testingFrame.board.getWidth(); x++) {
		    this.add(labels[x][y]);
		}
	    }
	}

	@Override
	public Dimension getPreferredSize() {
	    return new Dimension(900, 900);
	}

	public void updateInformation() {
	    for (int x = 0; x < testingFrame.board.getWidth(); x++) {
		for (int y = 0; y < testingFrame.board.getHeight(); y++) {
		    labels[x][y].updateInformation();
		}
	    }
	    updateColorsDisplayed();
	}

	public void updateColorsDisplayed() {
	    for (Square sqr : testingFrame.board) {
		Coordinate coor = sqr.getCoor();

		gamePanel.getSquareLabel(coor).setColorToDisplay(null);

		// if (testingFrame.currentlyPicking == Message.UNIT_SELECT) {
		// if (testingFrame.game.canSelectUnit(coor)) {
		// gamePanel.getSquareLabel(coor).setColorToDisplay(canSelectColor);
		// }
		// } else if (testingFrame.currentlyPicking == Message.UNIT_MOVE) {
		// if (testingFrame.game.canMoveTo(coor)) {
		// gamePanel.getSquareLabel(coor).setColorToDisplay(canMoveColor);
		// }
		// } else if (testingFrame.currentlyPicking == Message.UNIT_ATTACK) {
		// if (testingFrame.game.canAttack(coor)) {
		// gamePanel.getSquareLabel(coor).setColorToDisplay(canAttackColor);
		// }
		// } else if (testingFrame.currentlyPicking == Message.UNIT_DIR) {
		//
		// }
	    }
	}

	private SquareLabel getSquareLabel(Coordinate coor) {
	    return labels[coor.x()][coor.y()];
	}

	class SquareLabel extends JComponent implements MouseListener {

	    private static final long serialVersionUID = 7959593291619934967L;

	    private final Coordinate coor;
	    private final boolean isInBoard;

	    private boolean mouseIn;
	    private boolean mousePressing;

	    private boolean canCurrentlyClick = false;
	    private Color colorToDisplay = null;

	    private Unit unitOnTop;
	    private Image unitImg;
	    private Image waitingImg;
	    private Image dizzyImg;

	    public SquareLabel(Coordinate coor) {
		super();
		this.coor = coor;
		isInBoard = testingFrame.board.isInBoard(coor);
		if (isInBoard) {
		    setToolTipText(coor.toString());
		}
		addMouseListener(this);
	    }

	    public void updateInformation() {
		unitOnTop = isInBoard ? testingFrame.getSquare(coor).getUnitOnTop() : null;

		unitImg = null;
		waitingImg = null;
		dizzyImg = null;
		if (unitOnTop != null) {
		    if (unitOnTop.getStunnedProp().getValue()) {
			dizzyImg = Images.stunnedImage;
		    }
		    if (unitOnTop.getWaitProp().isWaiting()) {
			waitingImg = Images.waitingImage;
		    }

		    Class<? extends Unit> clazz = unitOnTop.getClass();
		    unitImg = Images.getImage(clazz);
		}
		canCurrentlyClick = testingFrame.canCurrentlyClick(coor);
	    }

	    public void setColorToDisplay(Color col) {
		colorToDisplay = col;
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (isInBoard && canCurrentlyClick) {
		    testingFrame.coordinateClicked(coor);
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
		mouseIn = true;
		testingFrame.mouseEntered(coor);

		repaint();
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		mouseIn = false;
		testingFrame.mouseExited(coor);

		repaint();
	    }

	    @Override
	    public void paintComponent(Graphics g) {
		try {
		    paintComponentActual(g);
		} catch (Exception e) {
		    paintComponent(g);
		}
	    }

	    private Color getUnitOwnershipColor() {
		if (unitOnTop != null) {
		    Player owner = unitOnTop.getOwnerProp().getValue();
		    if (owner.equals(testingFrame.localPlayer)) {
			return TestingFrameGUI.friendlyUnitColor;
		    } else {
			return TestingFrameGUI.enemyUnitColor;
		    }
		}
		return null;
	    }

	    private Color determineBackgroundColor() {
		if (!isInBoard) {
		    return Color.black;
		}
		Color col = null;
		if (colorToDisplay != null) {
		    col = colorToDisplay;
		} else {
		    col = getUnitOwnershipColor();

		    if (col == null) {
			col = Color.lightGray;
		    }
		    if (canCurrentlyClick) {
			// col = mixColors(col, lighterColor(Color.blue, 200));
		    }
		    if (testingFrame.currentlyPicking == Message.UNIT_ATTACK && testingFrame.aoeHighlightData != null
			    && testingFrame.getMouseInSquare() != null) {
			List<Square> aoe = testingFrame.aoeHighlightData.get(testingFrame.getMouseInSquare().getCoor());
			if (aoe != null && aoe.contains(testingFrame.board.getSquare(coor))) {
			    col = aoeColor;
			}
		    }
		}

		if (mousePressing) {
		    col = TestingFrameGUI.lighterColor(col, -50);
		} else if (mouseIn) {
		    col = TestingFrameGUI.lighterColor(col, -25);
		} else if (coor.equals(testingFrame.getOpponentHover())) {
		    col = TestingFrameGUI.lighterColor(col, -25);
		}
		// col = TestingFrame.combineColors(col,
		// gameDataPanel.colorsDisplayed[sqr.getCoor().x()][sqr.getCoor().y()]);
		return col;
	    }

	    public void paintComponentActual(Graphics g) {
		Color background = determineBackgroundColor();
		// clear
		g.setColor(background);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (canCurrentlyClick) {
		    Color circleCol = lighterColor(background, -50);
		    g.setColor(circleCol);
		    double percentCircle = .9;
		    int width = (int) (getWidth() * percentCircle), height = (int) (getHeight() * percentCircle);
		    g.fillOval((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
		}

		// if outside of testingFrame.board
		if (!isInBoard) {
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
		double aspectratio = (double) getHeight() / getWidth();
		double imgaspectratio = (double) unitImg.getHeight(null) / unitImg.getWidth(null);
		double ratio = 0;
		if (imgaspectratio > aspectratio) {
		    ratio = (double) getHeight() / unitImg.getHeight(null);
		} else {
		    ratio = (double) getWidth() / unitImg.getWidth(null);
		}

		int imgWidth = (int) (ratio * unitImg.getWidth(null)),
			imgHeight = (int) (ratio * unitImg.getHeight(null));
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
		Direction dir = unitOnTop.getPosProp().getDirFacingProp().getValue();
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

		// golden border if unit is picked
		if (testingFrame.game.getSelectedUnit() == unitOnTop) {
		    g.drawImage(Images.goldenFrameImage, 1, 1, getWidth() - 2, getHeight() - 2, null);
		}
		// border
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth(), getHeight());

		if (blockedSquares.contains(testingFrame.board.getSquare(coor))) {
		    int imgw = Images.blockedImage.getWidth(null), imgh = Images.blockedImage.getHeight(null);
		    double scale = (double) getWidth() / imgw;
		    int scaledimgh = (int) (imgh * scale);
		    g.drawImage(Images.blockedImage, 0, getHeight() / 2 - scaledimgh / 2, getWidth(), scaledimgh, null);
		}
	    }

	}
    }

    public class GameDataPanel extends JPanel {
	private static final long serialVersionUID = -4840314334092192454L;

	private final GridBagLayout gdpgbLayout;
	private final GridBagConstraints gdpgbConstrains;

	private final JLabel turnInfoLabel;

	final JToggleButton pickUnitTButton;
	final JToggleButton pickMoveTButton;
	final JToggleButton pickAttackTButton;
	final JToggleButton pickDirectionTButton;
	final JButton endTurnButton;

	private final JButton upDirButton;
	private final JButton leftDirButton;
	private final JButton rightDirButton;
	private final JButton downDirButton;

	private final JSeparator commandInfoSeperator;

	private final JLabel unitInfoLabel1;
	private final JLabel unitInfoLabel2;

	private JLabel hoverUnitLabel;
	private JLabel selectedUnitLabel;

	private final HashMap<JToggleButton, Border> normalBorders;

	public GameDataPanel() {
	    super();

	    gdpgbLayout = new GridBagLayout();
	    gdpgbConstrains = new GridBagConstraints();

	    pickUnitTButton = new JToggleButton("  Pick   ");

	    pickMoveTButton = new JToggleButton("  Move   ");
	    pickAttackTButton = new JToggleButton(" Attack  ");
	    pickDirectionTButton = new JToggleButton("Direction");

	    endTurnButton = new JButton("End Turn");

	    pickUnitTButton.setFocusable(false);
	    pickMoveTButton.setFocusable(false);
	    pickAttackTButton.setFocusable(false);
	    pickDirectionTButton.setFocusable(false);
	    endTurnButton.setFocusable(false);

	    endTurnButton.setPreferredSize(new Dimension(100, 100));

	    int arrowLength = 40;
	    upDirButton = new JButton(
		    new ImageIcon(Images.getScaledImage(Images.upArrowImage, arrowLength, arrowLength)));
	    leftDirButton = new JButton(
		    new ImageIcon(Images.getScaledImage(Images.leftArrowImage, arrowLength, arrowLength)));
	    rightDirButton = new JButton(
		    new ImageIcon(Images.getScaledImage(Images.rightArrowImage, arrowLength, arrowLength)));
	    downDirButton = new JButton(
		    new ImageIcon(Images.getScaledImage(Images.downArrowImage, arrowLength, arrowLength)));

	    upDirButton.setFocusable(false);
	    leftDirButton.setFocusable(false);
	    rightDirButton.setFocusable(false);
	    downDirButton.setFocusable(false);

	    upDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
	    leftDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
	    rightDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
	    downDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));

	    commandInfoSeperator = new JSeparator();
	    commandInfoSeperator.setPreferredSize(new Dimension(1, 10));

	    turnInfoLabel = new JLabel("[player's name]");
	    unitInfoLabel1 = new JLabel();
	    unitInfoLabel1.setVerticalAlignment(SwingConstants.TOP);
	    unitInfoLabel2 = new JLabel();
	    unitInfoLabel2.setVerticalAlignment(SwingConstants.TOP);

	    normalBorders = new HashMap<>();

	    GameDataPanel.this.organizeComponents();
	    setupButtonLogic();
	}

	public void organizeComponents() {

	    setLayout(gdpgbLayout);

	    int gridy = 0;
	    int gridx = 0;

	    gdpgbConstrains.gridx = gridx;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 6;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    turnInfoLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
	    turnInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    add(turnInfoLabel, gdpgbConstrains);

	    Font normalFont = new Font("Times New Roman", Font.PLAIN, 20);
	    int gap = 10;
	    gdpgbConstrains.gridx = gridx;
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

	    gdpgbConstrains.gridx = ++gridx;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    gdpgbConstrains.insets = new Insets(gap, 5, gap, 5);
	    JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
	    add(sep, gdpgbConstrains);

	    gdpgbConstrains.gridx = ++gridx;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    gdpgbConstrains.insets = new Insets(gap, 0, gap, 0);
	    pickMoveTButton.setFont(normalFont);
	    add(pickMoveTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = ++gridx;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    pickAttackTButton.setFont(normalFont);
	    add(pickAttackTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = ++gridx;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    pickDirectionTButton.setFont(normalFont);
	    add(pickDirectionTButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = ++gridx;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.NORTH;
	    endTurnButton.setFont(normalFont);
	    add(endTurnButton, gdpgbConstrains);

	    gridx = 3;
	    gdpgbConstrains.gridx = gridx;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.insets = new Insets(0, 0, 0, 0);
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(upDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = gridx - 1;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(leftDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = gridx + 1;
	    gdpgbConstrains.gridy = gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 1;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(rightDirButton, gdpgbConstrains);

	    gdpgbConstrains.gridx = gridx;
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
	    gdpgbConstrains.gridwidth = 6;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    add(commandInfoSeperator, gdpgbConstrains);

	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 0;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 6;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    unitInfoLabel1.setFont(normalFont);
	    add(unitInfoLabel1, gdpgbConstrains);

	    gdpgbConstrains.gridx = 0;
	    gdpgbConstrains.gridy = ++gridy;
	    gdpgbConstrains.weightx = 1;
	    gdpgbConstrains.weighty = 1;
	    gdpgbConstrains.gridheight = 1;
	    gdpgbConstrains.gridwidth = 6;
	    gdpgbConstrains.fill = GridBagConstraints.BOTH;
	    gdpgbConstrains.anchor = GridBagConstraints.CENTER;
	    unitInfoLabel2.setFont(normalFont);
	    add(unitInfoLabel2, gdpgbConstrains);

	    setBackground(gameDataPanelBackgroundColor);

	    normalBorders.put(pickUnitTButton, pickUnitTButton.getBorder());
	    normalBorders.put(pickMoveTButton, pickMoveTButton.getBorder());
	    normalBorders.put(pickAttackTButton, pickAttackTButton.getBorder());
	    normalBorders.put(pickDirectionTButton, pickDirectionTButton.getBorder());

	}

	public void setupButtonLogic() {
	    pickUnitTButton.addActionListener(e -> {
		testingFrame.pickUnitButtonClicked();
	    });
	    pickMoveTButton.addActionListener(e -> {
		testingFrame.pickMoveButtonClicked();
	    });
	    pickAttackTButton.addActionListener(e -> {
		testingFrame.pickAttackButtonClicked();
	    });
	    pickDirectionTButton.addActionListener(e -> {
		testingFrame.pickDirectionButtonClicked();
	    });
	    endTurnButton.addActionListener(e -> {
		testingFrame.endTurnButtonClicked();
	    });
	    upDirButton.addActionListener(e -> {
		testingFrame.directionClicked(Direction.UP);
	    });
	    leftDirButton.addActionListener(e -> {
		testingFrame.directionClicked(Direction.LEFT);
	    });
	    rightDirButton.addActionListener(e -> {
		testingFrame.directionClicked(Direction.RIGHT);
	    });
	    downDirButton.addActionListener(e -> {
		testingFrame.directionClicked(Direction.DOWN);
	    });

	    // to use following code all the buttons above must do
	    // gamedatapanel.requestfocus after each click

	    // setFocusable(true);
	    // addKeyListener(new KeyListener() {
	    // @Override
	    // public void keyTyped(KeyEvent e) {
	    // }
	    //
	    // @Override
	    // public void keyPressed(KeyEvent e) {
	    // }
	    //
	    // @Override
	    // public void keyReleased(KeyEvent e) {
	    // if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	    // naturalNextPick();
	    // }
	    // }
	    // });
	}

	private final ImageIcon greenDotIcon = new ImageIcon(Images.getScaledImage(Images.greenDotImage, 25, 25));
	private final ImageIcon redDotIcon = new ImageIcon(Images.getScaledImage(Images.redDotImage, 25, 25));

	public void updateInformation() {
	    Player currentPlayer = testingFrame.game.getCurrentTurn().getPlayerTurn();
	    turnInfoLabel.setText(currentPlayer.getName() + "'s turn ");
	    turnInfoLabel.setIcon(testingFrame.isLocalPlayerTurn() ? greenDotIcon : redDotIcon);

	    updateEnableButtons();
	    updateSelectButtons();

	    updateUnitInfoLabels();

	}

	public void updateUnitInfoLabels() {
	    if (testingFrame.game.hasSelectedUnit()) {
		selectedUnitLabel = unitInfoLabel1;
		hoverUnitLabel = unitInfoLabel2;
	    } else {
		hoverUnitLabel = unitInfoLabel1;
		selectedUnitLabel = unitInfoLabel2;
	    }
	    Square mouseinsqr = testingFrame.getMouseInSquare();
	    hoverUnitLabel.setText(
		    getHTMLabelInfoString(mouseinsqr == null ? null : mouseinsqr.getUnitOnTop(), "Hovering Over Unit"));
	    selectedUnitLabel.setText(getHTMLabelInfoString(testingFrame.game.getSelectedUnit(), "Selected Unit"));
	}

	void selectTurnPartButton(Message turnPart, boolean selected) {
	    getButton(turnPart).setSelected(selected);
	}

	void selectAllTurnPartButtons(boolean selected) {
	    pickUnitTButton.setSelected(selected);
	    pickMoveTButton.setSelected(selected);
	    pickAttackTButton.setSelected(selected);
	    pickDirectionTButton.setSelected(selected);
	    endTurnButton.setSelected(selected);
	}

	void enableTurnPartButton(Message turnPart, boolean enable) {
	    getButton(turnPart).setEnabled(enable);
	}

	void enableAllTurnPartButtons(boolean enable) {
	    pickUnitTButton.setEnabled(enable);
	    pickMoveTButton.setEnabled(enable);
	    pickAttackTButton.setEnabled(enable);
	    pickDirectionTButton.setEnabled(enable);
	    endTurnButton.setEnabled(enable);
	}

	void enableAllDirButtons(boolean enable) {
	    upDirButton.setEnabled(enable);
	    leftDirButton.setEnabled(enable);
	    rightDirButton.setEnabled(enable);
	    downDirButton.setEnabled(enable);
	}

	/**
	 * Call update information on game data panel first
	 */
	public void setCurrentlyPicking(Message turnPart) {
	    allNaturalBorders();

	    if (turnPart == Message.UNIT_SELECT || turnPart == Message.UNIT_MOVE || turnPart == Message.UNIT_ATTACK
		    || turnPart == Message.UNIT_DIR) {
		JToggleButton button = (JToggleButton) getButton(turnPart);
		setSelectingBorder(button);

		selectTurnPartButton(turnPart, true);
	    }

	}

	public void updateSelectButtons() {
	    selectTurnPartButton(Message.UNIT_SELECT, testingFrame.game.hasSelectedUnit());
	    selectTurnPartButton(Message.UNIT_MOVE, testingFrame.game.hasMoved());
	    selectTurnPartButton(Message.UNIT_ATTACK, testingFrame.game.hasAttacked());
	    selectTurnPartButton(Message.UNIT_DIR, testingFrame.game.hasChangedDir());
	}

	public void updateEnableButtons() {
	    enableAllTurnPartButtons(false);
	    enableAllDirButtons(false);

	    if (testingFrame.isLocalPlayerTurn()) {
		enableTurnPartButton(Message.UNIT_SELECT, !testingFrame.game.hasSelectedUnit());
		enableTurnPartButton(Message.UNIT_MOVE,
			!testingFrame.game.hasMoved() && testingFrame.game.hasSelectedUnit());
		enableTurnPartButton(Message.UNIT_ATTACK,
			!testingFrame.game.hasAttacked() && testingFrame.game.hasSelectedUnit());
		enableTurnPartButton(Message.UNIT_DIR,
			!testingFrame.game.hasChangedDir() && testingFrame.game.hasSelectedUnit());

		if (testingFrame.currentlyPicking == Message.UNIT_DIR && !testingFrame.game.hasChangedDir()) {
		    enableAllDirButtons(true);
		}

		enableTurnPartButton(Message.END_TURN, true);
	    }

	}

	private AbstractButton getButton(Message turnPart) {
	    if (Message.UNIT_SELECT.equals(turnPart)) {
		return pickUnitTButton;
	    } else if (turnPart == Message.UNIT_MOVE) {
		return pickMoveTButton;
	    } else if (turnPart == Message.UNIT_ATTACK) {
		return pickAttackTButton;
	    } else if (turnPart == Message.UNIT_DIR) {
		return pickDirectionTButton;
	    } else if (turnPart == Message.END_TURN) {
		return endTurnButton;
	    }
	    return null;
	}

	private void allNaturalBorders() {
	    setNaturalBorder(pickUnitTButton);
	    setNaturalBorder(pickMoveTButton);
	    setNaturalBorder(pickAttackTButton);
	    setNaturalBorder(pickDirectionTButton);
	}

	private void setNaturalBorder(JToggleButton button) {
	    button.setBorder(normalBorders.get(button));
	}

	private final Border redBottomBorder = BorderFactory.createMatteBorder(0, 0, 5, 0, Color.red);

	private void setSelectingBorder(JToggleButton button) {
	    CompoundBorder border = new CompoundBorder(redBottomBorder, normalBorders.get(button));
	    button.setBorder(border);
	}

	private String getHTMLabelInfoString(Unit unit, String title) {
	    if (unit == null) {
		return "";
	    } else {
		String str = "<html>";

		str += "<strong><u>" + title + "</u></strong>";

		Player owner = unit.getOwnerProp().getValue();
		str += "<br>" + owner.getName() + "'s " + unit.getClass().getSimpleName();

		int health = unit.getHealthProp().getValue();
		double percentHealth = unit.getHealthProp().currentPercentageHealth();
		str += "<br>Health: "
			+ colorize(health + "(" + (int) (percentHealth * 100) + "%)", percentHealth, 1, true);

		int power = ((AbilityPower) unit.getAbility()).getAbilityPowerProperty().getValue();
		int defaultPower = 0;
		try {
		    defaultPower = ((AbilityRange) unit.getAbility()).getAbilityRangeProperty().getDefaultValue();
		} catch (ClassCastException e) {
		}
		str += "<br>Power: " + colorize(power + "", power, defaultPower, true);

		int armor = unit.getHealthProp().getArmorProp().getValue();
		int defaultArmor = unit.getHealthProp().getArmorProp().getDefaultValue();
		str += "<br>Armor: " + colorize(armor + "", armor, defaultArmor, true);

		if (unit.getStunnedProp().getValue()) {
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

	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	}
    }

}
