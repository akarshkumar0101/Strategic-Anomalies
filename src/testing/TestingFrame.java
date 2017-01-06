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
import game.board.Square;
import game.unit.Unit;
import game.unit.listofunits.Warrior;

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
	System.out.println("send:" + data);
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
	return new Color(amount > col.getRed() ? 0 : col.getRed() - amount,
		amount > col.getGreen() ? 0 : col.getGreen() - amount,
		amount > col.getBlue() ? 0 : col.getBlue() - amount);
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
		gameDataPanel.updateHoveringOver();
	    }
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
	    private Image dizzyImg;

	    public SquareLabel(Square sqr) {
		this.sqr = sqr;
		addMouseListener(this);
	    }

	    public void updateInformation() {
		unitOnTop = sqr == null ? null : sqr.getUnitOnTop();

		dizzyImg = null;
		if (unitOnTop == null) {
		    unitImg = null;
		} else {
		    if (unitOnTop.getStunnedProp().getCurrentPropertyValue()) {
			dizzyImg = Images.dizzyImage;
		    }
		    if (unitOnTop.getClass() == Warrior.class) {
			unitImg = Images.warriorImage;
		    }
		}
	    }

	    public Color determineBackgroundColor() {
		if (sqr == null) {
		    return Color.black;
		}

		Color col = Color.lightGray;

		if (unitOnTop != null) {
		    Player owner = unitOnTop.getOwnerProp().getCurrentPropertyValue();

		    if (owner.equals(game.getPlayer1())) {
			col = slightBlue;
		    } else if (owner.equals(game.getPlayer2())) {
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
		if (unitOnTop == null) {
		    return;
		}

		// draw image in the center
		int imgWidth = (int) (percentageIconWidth * getWidth()),
			imgHeight = (int) (percentageIconHeight * getHeight());
		g.drawImage(unitImg, (getWidth() - imgWidth) / 2, (getHeight() - imgHeight) / 2, imgWidth, imgHeight,
			null);
		// draw dizzy (if stunned)
		double dizzpercentlen = .3;
		g.drawImage(dizzyImg, (int) (getWidth() * (1 - dizzpercentlen)),
			(int) (getHeight() * (1 - dizzpercentlen)), (int) (getWidth() * dizzpercentlen),
			(int) (getHeight() * dizzpercentlen), null);

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
		if (sqr != null) {
		    if (gameDataPanel.canCurrentlyClick(sqr)) {
			gameDataPanel.coordinateClicked(sqr.getCoor());
		    }
		}
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		if (gameDataPanel.canCurrentlyClick(sqr)) {
		    mousePressing = true;
		    repaint();
		}
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		if (gameDataPanel.canCurrentlyClick(sqr)) {
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

	public GameDataPanel() {
	    super();

	    currentlyPicking = null;

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
	    commandInfoSeperator.setPreferredSize(new Dimension(1, 50));

	    turnInfoLabel = new JLabel("[player's name]");
	    unitInfoLabel1 = new JLabel();
	    unitInfoLabel1.setVerticalAlignment(SwingConstants.TOP);
	    unitInfoLabel2 = new JLabel();
	    unitInfoLabel2.setVerticalAlignment(SwingConstants.TOP);

	    organizeComponents();
	    setupButtonLogic();

	    setBackground(darkerColor(Color.lightGray, 30));
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
	    gdpgbConstrains.weighty = 1;
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
	    if (pickUnitTButton.hasPicked) {
		pointPickedUnitLabel = unitInfoLabel1;
		pointHoverUnitLabel = unitInfoLabel2;
	    } else {
		pointHoverUnitLabel = unitInfoLabel1;
		pointPickedUnitLabel = unitInfoLabel2;
	    }
	    updateHoveringOver();
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

	public Color getCanClickIndicatorColor(Square sqr) {
	    if (canCurrentlyClick(sqr)) {
		if (currentlyPicking == pickUnitTButton) {
		    return null;
		} else if (currentlyPicking == pickMoveTButton) {
		    return null;
		} else if (currentlyPicking == pickAttackTButton) {
		    return null;
		} else if (currentlyPicking == pickDirectionTButton) {
		    return null;
		}
		return null;
	    } else {
		return null;
	    }
	}

	public void coordinateClicked(Coordinate coor) {
	    if (currentlyPicking == pickUnitTButton || currentlyPicking == pickMoveTButton
		    || currentlyPicking == pickAttackTButton) {
		if (currentlyPicking.hasPicked) {
		    return;
		}
		currentlyPicking.hasPicked = true;
		transmitDataToGame(coor);
		naturalNextPick();
	    }
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

	public void updateHoveringOver() {
	    Square mouseinsqr = gamePanel.mouseInSquare;
	    pointHoverUnitLabel.setText(getLabelInfoString(mouseinsqr));
	}

	public String getLabelInfoString(Square square) {
	    if (square == null) {
		return "";
	    } else {
		Unit unit = square.getUnitOnTop();
		if (unit == null) {
		    return "Not hovering over anything";
		} else {
		    return "Hovering over: " + unit.getUnitClass();
		}
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

	} catch (Exception e) {
	    e.printStackTrace();
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