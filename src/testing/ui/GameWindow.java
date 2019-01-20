package testing.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
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

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Path;
import game.board.Square;
import game.interaction.incident.IncidentListener;
import game.unit.Unit;
import game.unit.listofunits.Archer;
import game.unit.listofunits.Guardian;
import game.unit.listofunits.Hunter;
import game.unit.listofunits.Scout;
import game.unit.listofunits.Warrior;
import game.unit.property.ability.Ability;
import game.unit.property.ability.AbilityAOE;
import game.unit.property.ability.AbilityPower;
import game.unit.property.ability.AbilityRange;
import game.unit.property.ability.ActiveAbility;
import game.unit.property.ability.ActiveTargetAbility;
import io.Communication;
import testing.Message;

//TODO MAKE SURE YOU USE JAVAFX IN FINAL VERSION
public class GameWindow {

	final Game game;
	final Board board;
	final Player localPlayer;

	private final GameWindowGUI gui;

	private final FrameUpdatingThread frameUpdatingThread;

	private boolean doNaturalNextPick = true;

	private final Object dataTransferLock = new Object();

	final Map<Coordinate, List<Square>> aoeHighlightData;

	public GameWindow(Game game, Player localPlayer) {
		this.game = game;
		this.board = game.getBoard();
		this.localPlayer = localPlayer;

		this.gui = new GameWindowGUI(this);
		this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.frameUpdatingThread = new FrameUpdatingThread();

		this.aoeHighlightData = new HashMap<>();
	}

	public void startFrame() {
		this.gui.setupAnimationTriggers();
		this.gui.setupSoundTriggers();
		this.frameUpdatingThread.start();
		this.gui.setVisible(true);
		// Sounds.playSound(Sounds.headlinesSongSound);
	}

	Message currentlyPicking;

	private Coordinate opponentHover = null;
	private final Object opponentHoverLockObj = new Object();

	public Coordinate getOpponentHover() {
		synchronized (this.opponentHoverLockObj) {
			return this.opponentHover;
		}
	}

	public void setOpponentHover(Coordinate coor) {
		synchronized (this.opponentHoverLockObj) {
			this.opponentHover = coor;
		}
	}

	class FrameUpdatingThread extends Thread {

		private Communication receiveComm;

		@Override
		public void run() {
			// TODO manage if player quits, etc.
			this.receiveComm = GameWindow.this.localPlayer.getGameComm();

			// TODO add stop statement
			// game loop for different turns
			if (!Game.START_GAME.equals(this.receiveComm.recieveObject()))
				throw new RuntimeException("Could not properly start frame with game");
			// game is ready to be played
			GameWindow.this.gui.updateInformation();
			GameWindow.this.gui.repaint();
			while (true) {
				GameWindow.this.aoeHighlightData.clear();
				this.handleTurn();
				GameWindow.this.gui.updateInformation();
				GameWindow.this.gui.repaint();
			}

		}

		public void handleTurn() {
			GameWindow.this.setOpponentHover(null);
			boolean shouldRun = true;
			while (shouldRun) {
				GameWindow.this.requestNextNaturalPick();

				GameWindow.this.currentlyPicking = null;

				shouldRun = this.handleCommand();

				GameWindow.this.gui.updateInformation();
				GameWindow.this.gui.gameDataPanel.setCurrentlyPicking(null);
				GameWindow.this.gui.repaint();
			}
		}

		public boolean handleCommand() {
			Message command = null;
			Object specs = null;

			boolean run = false;
			do {
				run = false;

				Object obj = this.receiveComm.recieveObject();
				if (Message.HOVER.equals(obj)) {
					this.hover((Coordinate) this.receiveComm.recieveObject());
					run = true;
				} else if (obj instanceof Message) {
					command = (Message) obj;
					this.handlePartialCoreCommand(command);
					run = true;

					if (Message.END_TURN.equals(command))
						return false;
				} else {
					specs = obj;
					run = false;
				}

			} while (run);

			this.handleFullCoreCommand(command, specs);
			return true;
		}

		public void handlePartialCoreCommand(Message command) {

			GameWindow.this.currentlyPicking = command;

			if (command.equals(Message.UNIT_SELECT)) {
				this.unitSelect();
			} else if (command.equals(Message.UNIT_MOVE)) {
				this.unitMove();
			} else if (command.equals(Message.UNIT_ATTACK)) {
				this.unitAttack();
			} else if (command.equals(Message.UNIT_DIR)) {
				this.unitChangeDir();
			} else if (command.equals(Message.END_TURN)) {
				this.endTurn();
			}
			GameWindow.this.gui.updateInformation();
			GameWindow.this.gui.gameDataPanel.setCurrentlyPicking(command);
			GameWindow.this.gui.repaint();
		}

		public void handleFullCoreCommand(Message command, Object specs) {

			if (command.equals(Message.UNIT_SELECT)) {
				this.unitSelect((Coordinate) specs);
			} else if (command.equals(Message.UNIT_MOVE)) {
				this.unitMove((Path) specs, (Coordinate) this.receiveComm.recieveObject());
			} else if (command.equals(Message.UNIT_ATTACK)) {
				this.unitAttack((Coordinate) specs);
			} else if (command.equals(Message.UNIT_DIR)) {
				this.unitChangeDir((Direction) specs);
			}
			GameWindow.this.gui.updateInformation();
			GameWindow.this.gui.repaint();

		}

		public void hover(Coordinate coor) {
			GameWindow.this.setOpponentHover(coor);
			GameWindow.this.gui.repaint();
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
		return this.game.getCurrentTurn() == null ? false
				: this.localPlayer.equals(this.game.getCurrentTurn().getPlayerTurn());
	}

	private void transmitDataToGame(Object data) {
		if (!this.isLocalPlayerTurn())
			return;
		Communication gameComm = this.localPlayer.getGameComm();
		// Systemf.out.println("writing data to players out " + data);
		gameComm.sendObject(data);
	}

	public Square getSquare(Coordinate coor) {
		return this.board.getSquare(coor);
	}

	public void resetForNewTurnf() {

	}

	public boolean canCurrentlyClick(Coordinate coor) {
		if (!this.board.isInBoard(coor))
			return false;
		if (this.currentlyPicking == Message.UNIT_SELECT)
			return this.game.canSelectUnit(coor);
		else if (this.currentlyPicking == Message.UNIT_MOVE)
			return this.game.canMoveTo(coor);
		else if (this.currentlyPicking == Message.UNIT_ATTACK)
			return this.game.canAttack(coor);
		return false;
	}

	public boolean canCurrentlyChangeDir(Direction dir) {
		return this.game.canChangeDir(dir);
	}

	public void requestNextNaturalPick() {
		if (!this.isLocalPlayerTurn() || !this.doNaturalNextPick)
			return;
		// TODO add conditions for if not target ability, can't move piece etc.
		if (this.game.canSelectUnit() && !this.game.hasSelectedUnit()) {
			this.pickUnitButtonClicked();
		} else if (this.game.canMove()) {
			this.pickMoveButtonClicked();
		} else if (this.game.canAttack()) {
			if (this.game.getSelectedUnit().getAbility() instanceof ActiveTargetAbility) {
				this.pickAttackButtonClicked();
			}
		} else if (this.game.canChangeDir()) {
			this.pickDirectionButtonClicked();
		} else {
			// this seems kind of weird tbh lol
			// endTurnButtonClicked();
		}

	}

	public void pickUnitButtonClicked() {
		if (!this.isLocalPlayerTurn())
			return;
		if (this.game.hasEditedTurn())
			throw new RuntimeException("Already selected unit");
		this.transmitDataToGame(Message.UNIT_SELECT);
	}

	public void pickMoveButtonClicked() {
		if (!this.isLocalPlayerTurn())
			return;
		if (this.game.hasMoved())
			throw new RuntimeException("Already moved");
		if (!this.game.hasSelectedUnit())
			throw new RuntimeException("Hasn't selected unit");
		this.transmitDataToGame(Message.UNIT_MOVE);
	}

	public void pickAttackButtonClicked() {
		if (!this.isLocalPlayerTurn())
			return;
		if (this.game.hasAttacked())
			throw new RuntimeException("Already attacked");
		if (!this.game.hasSelectedUnit())
			throw new RuntimeException("Hasn't selected unit");

		if (this.game.getSelectedUnit().getAbility() instanceof AbilityAOE) {
			for (Square sqr : this.board) {
				if (this.game.canAttack(sqr.getCoor())) {
					this.aoeHighlightData.put(sqr.getCoor(),
							((AbilityAOE) this.game.getSelectedUnit().getAbility()).getAOESqaures(sqr));
				}
			}
		}

		this.transmitDataToGame(Message.UNIT_ATTACK);
	}

	public void pickDirectionButtonClicked() {
		if (!this.isLocalPlayerTurn())
			return;
		if (this.game.hasChangedDir())
			throw new RuntimeException("Already changed direction");
		if (!this.game.hasSelectedUnit())
			throw new RuntimeException("Hasn't selected unit");
		this.transmitDataToGame(Message.UNIT_DIR);
	}

	public void endTurnButtonClicked() {
		if (!this.isLocalPlayerTurn())
			return;
		this.transmitDataToGame(Message.END_TURN);
	}

	public void coordinateClicked(Coordinate coor) {
		if (!this.isLocalPlayerTurn())
			return;
		if (!this.canCurrentlyClick(coor))
			return;

		this.transmitDataToGame(coor);
		this.gui.gamePanel.repaint();
	}

	public void directionClicked(Direction dir) {
		if (!this.isLocalPlayerTurn())
			return;
		if (!this.canCurrentlyChangeDir(dir))
			return;
		this.transmitDataToGame(dir);
	}

	private final Object mouseInLock = new Object();
	private Coordinate mouseInCoordinate;

	public void setMouseInCoordinate(Coordinate mouseInCoordinate) {
		synchronized (this.mouseInLock) {
			if (this.mouseInCoordinate != mouseInCoordinate) {
				this.mouseInCoordinate = mouseInCoordinate;
				this.gui.gameDataPanel.updateUnitInfoLabels();
				this.gui.gamePanel.repaint();
			}
		}
	}

	public Square getMouseInSquare() {
		synchronized (this.mouseInLock) {
			if (this.mouseInCoordinate == null || !this.board.isInBoard(this.mouseInCoordinate))
				return null;
			else
				return this.board.getSquare(this.mouseInCoordinate);
		}
	}

	public void mouseEntered(Coordinate coor) {
		if (this.board.isInBoard(coor)) {
			if (this.isLocalPlayerTurn()) {
				synchronized (this.dataTransferLock) {
					this.transmitDataToGame(Message.HOVER);
					this.transmitDataToGame(coor);
				}
			}
			this.setMouseInCoordinate(coor);
		}
	}

	public void mouseExited(Coordinate coor) {
		if (this.board.isInBoard(coor) && this.isLocalPlayerTurn()) {
			synchronized (this.dataTransferLock) {
				this.transmitDataToGame(Message.HOVER);
				this.transmitDataToGame(null);
			}
		}
		this.setMouseInCoordinate(null);
	}

	public void mouseEnteredButton() {
		Sounds.playSound(Sounds.beepSound);
	}
}

class GameWindowGUI extends JFrame {
	private static final long serialVersionUID = 2570119871946595519L;

	private final GameWindow gameWindow;

	private final GridBagLayout gbLayout;
	private final GridBagConstraints gbConstrains;

	public GamePanel gamePanel;
	public GameDataPanel gameDataPanel;

	public GameWindowGUI(GameWindow gameWindow) {
		super("Testing Frame for Strategic Anomalies");

		this.gameWindow = gameWindow;

		this.gbLayout = new GridBagLayout();
		this.gbConstrains = new GridBagConstraints();

		this.gamePanel = new GamePanel();
		this.gameDataPanel = new GameDataPanel();

		this.organizeComponents();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// setSize(500, 500);
		this.pack();
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

		this.getContentPane().setLayout(this.gbLayout);

		this.gbConstrains.gridx = 0;
		this.gbConstrains.gridy = 0;
		this.gbConstrains.weightx = 1;
		this.gbConstrains.weighty = 1;
		this.gbConstrains.fill = GridBagConstraints.BOTH;
		this.gbConstrains.anchor = GridBagConstraints.CENTER;

		this.getContentPane().add(this.gamePanel, this.gbConstrains);

		this.gbConstrains.gridx = 1;
		this.gbConstrains.gridy = 0;
		this.gbConstrains.weightx = 0;
		this.gbConstrains.weighty = 1;
		this.gbConstrains.fill = GridBagConstraints.BOTH;
		this.gbConstrains.anchor = GridBagConstraints.CENTER;

		this.getContentPane().add(this.gameDataPanel, this.gbConstrains);

	}

	public void updateInformation() {
		this.gamePanel.updateInformation();
		this.gameDataPanel.updateInformation();
	}

	public static double scale(double num, double ori1, double ori2, double new1, double new2) {
		double scale = (new2 - new1) / (ori2 - ori1);
		return (num - ori1) * scale + new1;
	}

	public static final Color slightBlue = new Color(192, 192, 255), slightRed = new Color(255, 192, 192),
			slightGreen = new Color(192, 255, 192);
	public static final Color friendlyUnitColor = new Color(192, 220, 192), enemyUnitColor = new Color(220, 192, 192);
	public static final Color canSelectColor = GUIUtil.lighterColor(Color.blue, 150),
			canMoveColor = GUIUtil.lighterColor(Color.blue, 150),
			canAttackColor = GUIUtil.lighterColor(Color.blue, 150);
	public static final Color aoeColor = GUIUtil.lighterColor(Color.blue, 200);

	public static final Color gameDataPanelBackgroundColor = GUIUtil.lighterColor(Color.lightGray, -30);

	private final Vector<Square> blockedSquares = new Vector<>();

	public static final int BLOCK_ANIMATION_TIME = 500;

	void setupAnimationTriggers() {
		for (Unit unit : this.gameWindow.game.getAllUnits()) {
			unit.getHealthProp().getArmorProp().getBlockReporter().add(new IncidentListener() {
				@Override
				public void incidentReported(Object... specifications) {
					GameWindowGUI.this.triggerBlockAnimation(
							GameWindowGUI.this.gameWindow.board.getSquare(unit.getPosProp().getValue()));
				}
			});
		}
	}

	void setupSoundTriggers() {
		for (Unit unit : this.gameWindow.game.getAllUnits()) {
			unit.getHealthProp().getArmorProp().getBlockReporter().add(new IncidentListener() {
				@Override
				public void incidentReported(Object... specifications) {
					Sounds.playSound(Sounds.blockSound);
				}
			});
			if (unit instanceof Warrior || unit instanceof Guardian) {
				((ActiveAbility) unit.getAbility()).getOnUseReporter().add(new IncidentListener() {
					@Override
					public void incidentReported(Object... specifications) {
						Sounds.playSound(Sounds.hitSound);
					}
				});
			}
			if (unit instanceof Scout || unit instanceof Archer || unit instanceof Hunter) {
				((ActiveAbility) unit.getAbility()).getOnUseReporter().add(new IncidentListener() {
					@Override
					public void incidentReported(Object... specifications) {
						Sounds.playSound(Sounds.arrowSound);
					}
				});
			}
		}
	}

	private void triggerBlockAnimation(Square sqr) {
		this.blockedSquares.add(sqr);
		this.gamePanel.repaint();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				GameWindowGUI.this.blockedSquares.remove(sqr);
				GameWindowGUI.this.gamePanel.repaint();
			}
		}, GameWindowGUI.BLOCK_ANIMATION_TIME);
		Sounds.playSound(Sounds.blockSound);
	}

	class GamePanel extends JPanel {

		private static final long serialVersionUID = 7783998123812310360L;

		private final SquareLabel[][] labels;
		private final GridLayout gridLayout;

		public GamePanel() {
			super();
			this.labels = new SquareLabel[GameWindowGUI.this.gameWindow.board
					.getWidth()][GameWindowGUI.this.gameWindow.board.getHeight()];
			this.gridLayout = new GridLayout(GameWindowGUI.this.gameWindow.board.getHeight(),
					GameWindowGUI.this.gameWindow.board.getWidth());

			for (int x = 0; x < GameWindowGUI.this.gameWindow.board.getWidth(); x++) {
				for (int y = 0; y < GameWindowGUI.this.gameWindow.board.getHeight(); y++) {
					Coordinate coor = new Coordinate(x, y);
					this.labels[x][y] = new SquareLabel(coor);
				}
			}
			GamePanel.this.organizeComponents();
		}

		public void organizeComponents() {
			this.setLayout(this.gridLayout);
			for (int y = GameWindowGUI.this.gameWindow.board.getHeight() - 1; y >= 0; y--) {
				for (int x = 0; x < GameWindowGUI.this.gameWindow.board.getWidth(); x++) {
					this.add(this.labels[x][y]);
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(900, 900);
		}

		public void updateInformation() {
			for (int x = 0; x < GameWindowGUI.this.gameWindow.board.getWidth(); x++) {
				for (int y = 0; y < GameWindowGUI.this.gameWindow.board.getHeight(); y++) {
					this.labels[x][y].updateInformation();
				}
			}
			this.updateColorsDisplayed();
		}

		public void updateColorsDisplayed() {
			for (Square sqr : GameWindowGUI.this.gameWindow.board) {
				Coordinate coor = sqr.getCoor();

				GameWindowGUI.this.gamePanel.getSquareLabel(coor).setColorToDisplay(null);

				// if (gameWindow.currentlyPicking == Message.UNIT_SELECT) {
				// if (gameWindow.game.canSelectUnit(coor)) {
				// gamePanel.getSquareLabel(coor).setColorToDisplay(canSelectColor);
				// }
				// } else if (gameWindow.currentlyPicking == Message.UNIT_MOVE) {
				// if (gameWindow.game.canMoveTo(coor)) {
				// gamePanel.getSquareLabel(coor).setColorToDisplay(canMoveColor);
				// }
				// } else if (gameWindow.currentlyPicking == Message.UNIT_ATTACK) {
				// if (gameWindow.game.canAttack(coor)) {
				// gamePanel.getSquareLabel(coor).setColorToDisplay(canAttackColor);
				// }
				// } else if (gameWindow.currentlyPicking == Message.UNIT_DIR) {
				//
				// }
			}
		}

		private SquareLabel getSquareLabel(Coordinate coor) {
			return this.labels[coor.x()][coor.y()];
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
				this.isInBoard = GameWindowGUI.this.gameWindow.board.isInBoard(coor);
				if (this.isInBoard) {
					this.setToolTipText(coor.toString());
				}
				this.addMouseListener(this);
			}

			public void updateInformation() {
				this.unitOnTop = this.isInBoard ? GameWindowGUI.this.gameWindow.getSquare(this.coor).getUnitOnTop()
						: null;

				this.unitImg = null;
				this.waitingImg = null;
				this.dizzyImg = null;
				if (this.unitOnTop != null) {
					if (this.unitOnTop.getStunnedProp().getValue()) {
						this.dizzyImg = Images.stunnedImage;
					}
					if (this.unitOnTop.getWaitProp().isWaiting()) {
						this.waitingImg = Images.waitingImage;
					}

					Class<? extends Unit> clazz = this.unitOnTop.getClass();
					this.unitImg = Images.getImage(clazz);
				}
				this.canCurrentlyClick = GameWindowGUI.this.gameWindow.canCurrentlyClick(this.coor);
			}

			public void setColorToDisplay(Color col) {
				this.colorToDisplay = col;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (this.isInBoard && this.canCurrentlyClick) {
					GameWindowGUI.this.gameWindow.coordinateClicked(this.coor);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (this.canCurrentlyClick) {
					this.mousePressing = true;
					this.repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (this.mousePressing) {
					this.mousePressing = false;
					this.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				this.mouseIn = true;
				GameWindowGUI.this.gameWindow.mouseEntered(this.coor);

				if (this.canCurrentlyClick) {
					GameWindowGUI.this.gameWindow.mouseEnteredButton();
				}

				this.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				this.mouseIn = false;
				GameWindowGUI.this.gameWindow.mouseExited(this.coor);

				this.repaint();
			}

			@Override
			public void paintComponent(Graphics g) {
				try {
					this.paintComponentActual(g);
				} catch (Exception e) {
					this.paintComponent(g);
				}
			}

			private Color getUnitOwnershipColor() {
				if (this.unitOnTop != null) {
					Player owner = this.unitOnTop.getOwnerProp().getValue();
					if (owner.equals(GameWindowGUI.this.gameWindow.localPlayer))
						return GameWindowGUI.friendlyUnitColor;
					else
						return GameWindowGUI.enemyUnitColor;
				}
				return null;
			}

			private Color determineBackgroundColor() {
				if (!this.isInBoard)
					return Color.black;
				Color col = null;
				if (this.colorToDisplay != null) {
					col = this.colorToDisplay;
				} else {
					col = this.getUnitOwnershipColor();

					if (col == null) {
						col = Color.lightGray;
					}
					if (this.canCurrentlyClick) {
						// col = mixColors(col, lighterColor(Color.blue, 200));
					}
					if (GameWindowGUI.this.gameWindow.currentlyPicking == Message.UNIT_ATTACK
							&& GameWindowGUI.this.gameWindow.aoeHighlightData != null
							&& GameWindowGUI.this.gameWindow.getMouseInSquare() != null) {
						List<Square> aoe = GameWindowGUI.this.gameWindow.aoeHighlightData
								.get(GameWindowGUI.this.gameWindow.getMouseInSquare().getCoor());
						if (aoe != null && aoe.contains(GameWindowGUI.this.gameWindow.board.getSquare(this.coor))) {
							col = GameWindowGUI.aoeColor;
						}
					}
				}

				if (this.mousePressing) {
					col = GUIUtil.lighterColor(col, -50);
				} else if (this.mouseIn) {
					col = GUIUtil.lighterColor(col, -25);
				} else if (this.coor.equals(GameWindowGUI.this.gameWindow.getOpponentHover())) {
					col = GUIUtil.lighterColor(col, -25);
				}
				// col = GameWindow.combineColors(col,
				// gameDataPanel.colorsDisplayed[sqr.getCoor().x()][sqr.getCoor().y()]);
				return col;
			}

			public void paintComponentActual(Graphics g) {
				Color background = this.determineBackgroundColor();
				// clear
				g.setColor(background);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());

				if (this.canCurrentlyClick) {
					Color circleCol = GUIUtil.lighterColor(background, -50);
					g.setColor(circleCol);
					double percentCircle = .9;
					int width = (int) (this.getWidth() * percentCircle),
							height = (int) (this.getHeight() * percentCircle);
					g.fillOval((this.getWidth() - width) / 2, (this.getHeight() - height) / 2, width, height);
				}

				// if outside of gameWindow.board
				if (!this.isInBoard)
					return;
				// border
				g.setColor(Color.black);
				g.drawRect(0, 0, this.getWidth(), this.getHeight());

				// if empty square, end it
				if (this.unitOnTop == null)
					return;

				// draw image in the center
				double aspectratio = (double) this.getHeight() / this.getWidth();
				double imgaspectratio = (double) this.unitImg.getHeight(null) / this.unitImg.getWidth(null);
				double ratio = 0;
				if (imgaspectratio > aspectratio) {
					ratio = (double) this.getHeight() / this.unitImg.getHeight(null);
				} else {
					ratio = (double) this.getWidth() / this.unitImg.getWidth(null);
				}

				int imgWidth = (int) (ratio * this.unitImg.getWidth(null)),
						imgHeight = (int) (ratio * this.unitImg.getHeight(null));
				g.drawImage(this.unitImg, (this.getWidth() - imgWidth) / 2, (this.getHeight() - imgHeight) / 2,
						imgWidth, imgHeight, null);

				// draw waiting image (if stunned)
				double smallpiclen = .3;
				g.drawImage(this.waitingImg, (int) (this.getWidth() * (1 - smallpiclen)),
						(int) (this.getHeight() * (1 - smallpiclen)), (int) (this.getWidth() * smallpiclen),
						(int) (this.getHeight() * smallpiclen), null);
				// draw stunned image
				g.drawImage(this.dizzyImg, 0, (int) (this.getHeight() * (1 - smallpiclen)),
						(int) (this.getWidth() * smallpiclen), (int) (this.getHeight() * smallpiclen), null);

				// draw health bar
				double healthPercentage = this.unitOnTop.getHealthProp().currentPercentageHealth();
				int healthBarHeight = this.getHeight() / 15;
				g.setColor(Color.green);
				g.fillRect(1, 1, (int) (healthPercentage * this.getWidth()) - 1, healthBarHeight);

				// draw direction facing arrow
				Direction dir = this.unitOnTop.getPosProp().getDirFacingProp().getValue();
				int arrowWidth = (int) (.5 * this.getWidth()), arrowHeight = (int) (.2 * this.getHeight());
				g.setColor(Color.red);
				if (dir == Direction.UP) {
					g.drawImage(Images.upArrowImage, (this.getWidth() - arrowWidth) / 2, 0, arrowWidth, arrowHeight,
							null);
				} else if (dir == Direction.DOWN) {
					g.drawImage(Images.downArrowImage, (this.getWidth() - arrowWidth) / 2,
							this.getHeight() - arrowHeight, arrowWidth, arrowHeight, null);
				}
				arrowHeight = (int) (.2 * this.getWidth());
				arrowWidth = (int) (.5 * this.getHeight());
				if (dir == Direction.LEFT) {
					g.drawImage(Images.leftArrowImage, 0, (this.getHeight() - arrowWidth) / 2, arrowHeight, arrowWidth,
							null);

				} else if (dir == Direction.RIGHT) {
					g.drawImage(Images.rightArrowImage, this.getWidth() - arrowHeight,
							(this.getHeight() - arrowWidth) / 2, arrowHeight, arrowWidth, null);
				}

				// golden border if unit is picked
				if (GameWindowGUI.this.gameWindow.game.getSelectedUnit() == this.unitOnTop) {
					g.drawImage(Images.goldenFrameImage, 1, 1, this.getWidth() - 2, this.getHeight() - 2, null);
				}
				// border
				g.setColor(Color.black);
				g.drawRect(0, 0, this.getWidth(), this.getHeight());

				if (GameWindowGUI.this.blockedSquares
						.contains(GameWindowGUI.this.gameWindow.board.getSquare(this.coor))) {
					int imgw = Images.blockedImage.getWidth(null), imgh = Images.blockedImage.getHeight(null);
					double scale = (double) this.getWidth() / imgw;
					int scaledimgh = (int) (imgh * scale);
					g.drawImage(Images.blockedImage, 0, this.getHeight() / 2 - scaledimgh / 2, this.getWidth(),
							scaledimgh, null);
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

			this.gdpgbLayout = new GridBagLayout();
			this.gdpgbConstrains = new GridBagConstraints();

			this.pickUnitTButton = new JToggleButton("  Pick   ");

			this.pickMoveTButton = new JToggleButton("  Move   ");
			this.pickAttackTButton = new JToggleButton(" Attack  ");
			this.pickDirectionTButton = new JToggleButton("Direction");

			this.endTurnButton = new JButton("End Turn");

			this.pickUnitTButton.setFocusable(false);
			this.pickMoveTButton.setFocusable(false);
			this.pickAttackTButton.setFocusable(false);
			this.pickDirectionTButton.setFocusable(false);
			this.endTurnButton.setFocusable(false);

			this.endTurnButton.setPreferredSize(new Dimension(100, 100));

			int arrowLength = 40;
			this.upDirButton = new JButton(
					new ImageIcon(Images.getScaledImage(Images.upArrowImage, arrowLength, arrowLength)));
			this.leftDirButton = new JButton(
					new ImageIcon(Images.getScaledImage(Images.leftArrowImage, arrowLength, arrowLength)));
			this.rightDirButton = new JButton(
					new ImageIcon(Images.getScaledImage(Images.rightArrowImage, arrowLength, arrowLength)));
			this.downDirButton = new JButton(
					new ImageIcon(Images.getScaledImage(Images.downArrowImage, arrowLength, arrowLength)));

			this.upDirButton.setFocusable(false);
			this.leftDirButton.setFocusable(false);
			this.rightDirButton.setFocusable(false);
			this.downDirButton.setFocusable(false);

			this.upDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
			this.leftDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
			this.rightDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));
			this.downDirButton.setPreferredSize(new Dimension(arrowLength + 20, arrowLength + 20));

			this.commandInfoSeperator = new JSeparator();
			this.commandInfoSeperator.setPreferredSize(new Dimension(1, 10));

			this.turnInfoLabel = new JLabel("[player's name]");
			this.unitInfoLabel1 = new JLabel();
			this.unitInfoLabel1.setVerticalAlignment(SwingConstants.TOP);
			this.unitInfoLabel2 = new JLabel();
			this.unitInfoLabel2.setVerticalAlignment(SwingConstants.TOP);

			this.normalBorders = new HashMap<>();

			MouseAdapter mouseEnterListener = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					AbstractButton button = (AbstractButton) e.getSource();
					if (button.isEnabled()) {
						GameWindowGUI.this.gameWindow.mouseEnteredButton();
					}
				}
			};
			this.pickUnitTButton.addMouseListener(mouseEnterListener);
			this.pickMoveTButton.addMouseListener(mouseEnterListener);
			this.pickAttackTButton.addMouseListener(mouseEnterListener);
			this.pickDirectionTButton.addMouseListener(mouseEnterListener);
			this.endTurnButton.addMouseListener(mouseEnterListener);
			this.upDirButton.addMouseListener(mouseEnterListener);
			this.leftDirButton.addMouseListener(mouseEnterListener);
			this.rightDirButton.addMouseListener(mouseEnterListener);
			this.downDirButton.addMouseListener(mouseEnterListener);

			this.setupButtonLogic();

			GameDataPanel.this.organizeComponents();
		}

		public void organizeComponents() {

			this.setLayout(this.gdpgbLayout);

			int gridy = 0;
			int gridx = 0;

			this.gdpgbConstrains.gridx = gridx;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 6;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.turnInfoLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
			this.turnInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(this.turnInfoLabel, this.gdpgbConstrains);

			Font normalFont = new Font("Times New Roman", Font.PLAIN, 20);
			int gap = 10;
			this.gdpgbConstrains.gridx = gridx;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.NORTH;
			this.gdpgbConstrains.insets = new Insets(gap, 0, gap, 0);
			this.pickUnitTButton.setFont(normalFont);
			this.add(this.pickUnitTButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = ++gridx;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.NORTH;
			this.gdpgbConstrains.insets = new Insets(gap, 5, gap, 5);
			JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
			this.add(sep, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = ++gridx;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.NORTH;
			this.gdpgbConstrains.insets = new Insets(gap, 0, gap, 0);
			this.pickMoveTButton.setFont(normalFont);
			this.add(this.pickMoveTButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = ++gridx;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.NORTH;
			this.pickAttackTButton.setFont(normalFont);
			this.add(this.pickAttackTButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = ++gridx;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.NORTH;
			this.pickDirectionTButton.setFont(normalFont);
			this.add(this.pickDirectionTButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = ++gridx;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.NORTH;
			this.endTurnButton.setFont(normalFont);
			this.add(this.endTurnButton, this.gdpgbConstrains);

			gridx = 3;
			this.gdpgbConstrains.gridx = gridx;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.insets = new Insets(0, 0, 0, 0);
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.add(this.upDirButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = gridx - 1;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.add(this.leftDirButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = gridx + 1;
			this.gdpgbConstrains.gridy = gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.add(this.rightDirButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = gridx;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 1;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.add(this.downDirButton, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = 0;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 6;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.add(this.commandInfoSeperator, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = 0;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 0;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 6;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.unitInfoLabel1.setFont(normalFont);
			this.add(this.unitInfoLabel1, this.gdpgbConstrains);

			this.gdpgbConstrains.gridx = 0;
			this.gdpgbConstrains.gridy = ++gridy;
			this.gdpgbConstrains.weightx = 1;
			this.gdpgbConstrains.weighty = 1;
			this.gdpgbConstrains.gridheight = 1;
			this.gdpgbConstrains.gridwidth = 6;
			this.gdpgbConstrains.fill = GridBagConstraints.BOTH;
			this.gdpgbConstrains.anchor = GridBagConstraints.CENTER;
			this.unitInfoLabel2.setFont(normalFont);
			this.add(this.unitInfoLabel2, this.gdpgbConstrains);

			this.setBackground(GameWindowGUI.gameDataPanelBackgroundColor);

			this.normalBorders.put(this.pickUnitTButton, this.pickUnitTButton.getBorder());
			this.normalBorders.put(this.pickMoveTButton, this.pickMoveTButton.getBorder());
			this.normalBorders.put(this.pickAttackTButton, this.pickAttackTButton.getBorder());
			this.normalBorders.put(this.pickDirectionTButton, this.pickDirectionTButton.getBorder());

		}

		public void setupButtonLogic() {
			this.pickUnitTButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.pickUnitButtonClicked();
				}
			});
			this.pickMoveTButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.pickMoveButtonClicked();
				}
			});
			this.pickAttackTButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.pickAttackButtonClicked();
				}
			});
			this.pickDirectionTButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.pickDirectionButtonClicked();
				}
			});
			this.endTurnButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.endTurnButtonClicked();
				}
			});
			this.upDirButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.directionClicked(Direction.UP);
				}
			});
			this.leftDirButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.directionClicked(Direction.LEFT);
				}
			});
			this.rightDirButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.directionClicked(Direction.RIGHT);
				}
			});
			this.downDirButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameWindowGUI.this.gameWindow.directionClicked(Direction.DOWN);
				}
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
			Player currentPlayer = GameWindowGUI.this.gameWindow.game.getCurrentTurn().getPlayerTurn();
			this.turnInfoLabel.setText(currentPlayer.getName() + "'s turn ");
			this.turnInfoLabel
					.setIcon(GameWindowGUI.this.gameWindow.isLocalPlayerTurn() ? this.greenDotIcon : this.redDotIcon);

			this.updateEnableButtons();
			this.updateSelectButtons();

			this.updateUnitInfoLabels();

		}

		public void updateUnitInfoLabels() {
			if (GameWindowGUI.this.gameWindow.game.hasSelectedUnit()) {
				this.selectedUnitLabel = this.unitInfoLabel1;
				this.hoverUnitLabel = this.unitInfoLabel2;
			} else {
				this.hoverUnitLabel = this.unitInfoLabel1;
				this.selectedUnitLabel = this.unitInfoLabel2;
			}
			Square mouseinsqr = GameWindowGUI.this.gameWindow.getMouseInSquare();
			this.hoverUnitLabel.setText(this.getHTMLabelInfoString(
					mouseinsqr == null ? null : mouseinsqr.getUnitOnTop(), "Hovering Over Unit"));
			this.selectedUnitLabel.setText(
					this.getHTMLabelInfoString(GameWindowGUI.this.gameWindow.game.getSelectedUnit(), "Selected Unit"));
		}

		void selectTurnPartButton(Message turnPart, boolean selected) {
			this.getButton(turnPart).setSelected(selected);
		}

		void selectAllTurnPartButtons(boolean selected) {
			this.pickUnitTButton.setSelected(selected);
			this.pickMoveTButton.setSelected(selected);
			this.pickAttackTButton.setSelected(selected);
			this.pickDirectionTButton.setSelected(selected);
			this.endTurnButton.setSelected(selected);
		}

		void enableTurnPartButton(Message turnPart, boolean enable) {
			this.getButton(turnPart).setEnabled(enable);
		}

		void enableAllTurnPartButtons(boolean enable) {
			this.pickUnitTButton.setEnabled(enable);
			this.pickMoveTButton.setEnabled(enable);
			this.pickAttackTButton.setEnabled(enable);
			this.pickDirectionTButton.setEnabled(enable);
			this.endTurnButton.setEnabled(enable);
		}

		void enableAllDirButtons(boolean enable) {
			this.upDirButton.setEnabled(enable);
			this.leftDirButton.setEnabled(enable);
			this.rightDirButton.setEnabled(enable);
			this.downDirButton.setEnabled(enable);
		}

		/**
		 * Call update information on game data panel first
		 */
		public void setCurrentlyPicking(Message turnPart) {
			this.allNaturalBorders();

			if (turnPart == Message.UNIT_SELECT || turnPart == Message.UNIT_MOVE || turnPart == Message.UNIT_ATTACK
					|| turnPart == Message.UNIT_DIR) {
				JToggleButton button = (JToggleButton) this.getButton(turnPart);
				this.setSelectingBorder(button);

				this.selectTurnPartButton(turnPart, true);
			}

		}

		public void updateSelectButtons() {
			this.selectTurnPartButton(Message.UNIT_SELECT, GameWindowGUI.this.gameWindow.game.hasSelectedUnit());
			this.selectTurnPartButton(Message.UNIT_MOVE, GameWindowGUI.this.gameWindow.game.hasMoved());
			this.selectTurnPartButton(Message.UNIT_ATTACK, GameWindowGUI.this.gameWindow.game.hasAttacked());
			this.selectTurnPartButton(Message.UNIT_DIR, GameWindowGUI.this.gameWindow.game.hasChangedDir());
		}

		public void updateEnableButtons() {
			this.enableAllTurnPartButtons(false);
			this.enableAllDirButtons(false);

			if (GameWindowGUI.this.gameWindow.isLocalPlayerTurn()) {
				this.enableTurnPartButton(Message.UNIT_SELECT, GameWindowGUI.this.gameWindow.game.canSelectUnit());
				this.enableTurnPartButton(Message.UNIT_MOVE, GameWindowGUI.this.gameWindow.game.canMove());
				this.enableTurnPartButton(Message.UNIT_ATTACK, GameWindowGUI.this.gameWindow.game.canAttack());
				this.enableTurnPartButton(Message.UNIT_DIR, GameWindowGUI.this.gameWindow.game.canChangeDir());

				// if (gameWindow.currentlyPicking == Message.UNIT_DIR &&
				// !gameWindow.game.hasChangedDir()
				// && !gameWindow.game.hasDiedOnTurn()) {
				// enableAllDirButtons(true);
				// }
				if (GameWindowGUI.this.gameWindow.currentlyPicking == Message.UNIT_DIR) {
					this.enableAllDirButtons(true);
				}

				this.enableTurnPartButton(Message.END_TURN, true);
			}

		}

		private AbstractButton getButton(Message turnPart) {
			if (Message.UNIT_SELECT.equals(turnPart))
				return this.pickUnitTButton;
			else if (turnPart == Message.UNIT_MOVE)
				return this.pickMoveTButton;
			else if (turnPart == Message.UNIT_ATTACK)
				return this.pickAttackTButton;
			else if (turnPart == Message.UNIT_DIR)
				return this.pickDirectionTButton;
			else if (turnPart == Message.END_TURN)
				return this.endTurnButton;
			return null;
		}

		private void allNaturalBorders() {
			this.setNaturalBorder(this.pickUnitTButton);
			this.setNaturalBorder(this.pickMoveTButton);
			this.setNaturalBorder(this.pickAttackTButton);
			this.setNaturalBorder(this.pickDirectionTButton);
		}

		private void setNaturalBorder(JToggleButton button) {
			button.setBorder(this.normalBorders.get(button));
		}

		private final Border redBottomBorder = BorderFactory.createMatteBorder(0, 0, 5, 0, Color.red);

		private void setSelectingBorder(JToggleButton button) {
			CompoundBorder border = new CompoundBorder(this.redBottomBorder, this.normalBorders.get(button));
			button.setBorder(border);
		}

		private String getHTMLabelInfoString(Unit unit, String title) {
			if (unit == null)
				return "";
			else {
				String str = "<html>";

				str += "<strong><u>" + title + "</u></strong>";
				str += "<br>";

				Player owner = unit.getOwnerProp().getValue();
				str += owner.getName() + "'s " + unit.getClass().getSimpleName();
				str += "<br>";

				str += "Move Range: " + this.colorize(unit.getMovingProp().getValue() + "",
						unit.getMovingProp().getValue(), unit.getMovingProp().getDefaultValue(), true);
				str += "<br>";

				int health = unit.getHealthProp().getValue();
				double percentHealth = unit.getHealthProp().currentPercentageHealth();
				str += "Health: " + this.colorize(health + "(" + (int) (percentHealth * 100) + "%)",
						unit.getHealthProp().getValue(), unit.getHealthProp().getDefaultValue(), true);
				str += "<br>";

				int armor = unit.getHealthProp().getArmorProp().getValue();
				int defaultArmor = unit.getHealthProp().getArmorProp().getDefaultValue();
				str += "Armor: " + this.colorize(armor + "", armor, defaultArmor, true);
				str += "<br>";

				Ability ability = unit.getAbility();

				if (ability instanceof AbilityPower) {
					AbilityPower power = (AbilityPower) ability;
					str += "Ability Power: " + this.colorize(power.getAbilityPowerProperty().getValue() + "",
							power.getAbilityPowerProperty().getValue(),
							power.getAbilityPowerProperty().getDefaultValue(), true);
					str += "<br>";
				}
				if (ability instanceof AbilityRange) {
					AbilityRange range = (AbilityRange) ability;
					str += "Ability Range: " + this.colorize(range.getAbilityRangeProperty().getValue() + "",
							range.getAbilityRangeProperty().getValue(),
							range.getAbilityRangeProperty().getDefaultValue(), true);
					str += "<br>";
				}

				str += "Ability AOE: " + (ability instanceof AbilityAOE);
				str += "<br>";

				if (unit.getStunnedProp().getValue()) {
					str += this.colorize("Stunned*", 1, 2, true);
					str += "<br>";
				}
				if (unit.getWaitProp().isWaiting()) {
					str += this.colorize("Is waiting for " + unit.getWaitProp().getValue() + " turns*", 1, 2, true);
					str += "<br>";
				}

				str += "Front Block: " + unit.getHealthProp().getArmorProp().getFrontBlockProperty().getValue();
				str += "<br>";

				str += "Side Block: " + unit.getHealthProp().getArmorProp().getSideBlockProperty().getValue();
				str += "<br>";

				str += "</html>";
				return str;
			}
		}

		private String colorize(String str, double value, double defaultValue, boolean greaterIsGreen) {

			if (value == defaultValue)
				return str;
			else if (!(value > defaultValue ^ greaterIsGreen))
				// green
				return "<font color=\"green\">" + str + "</font>";
			else
				// red
				return "<font color=\"red\">" + str + "</font>";
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}

}
