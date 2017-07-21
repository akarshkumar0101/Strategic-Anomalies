package testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import game.Communication;
import game.Game;
import game.Player;
import game.Turn;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.NormalBoard;
import game.board.Path;
import game.unit.Unit;
import game.unit.property.ability.ActiveTargetAbilityProperty;

/*
 * Communications:
 * Each player has communications to push moves and receive moves from other players.
 * The game assigns a communication for each player in the game using a HashMaps.
 * (The game/player connects the communication from the HashMap to the player somehow, whether they are over Internet or locally)
 * The game receives moves from a player by the communication from the HashMap of a player.
 * Each player individually pushes moves by themselves (TestingFrame does it for them, bc it is them).
 * The game pushes one players move to other players by using the communications in the HashMap, that each player will receive on their communication.
 */
public class TestingGame extends Game {

    private final Board board;

    public final TestingFrame testingFrame;

    private final TestingPlayer player1;
    private final TestingPlayer player2;
    private final List<TestingPlayer> allPlayers;
    private final List<TestingPlayer> localPlayers;

    private final HashMap<TestingPlayer, Communication> playerComms;

    private Turn currentTurn;

    public final Random random;

    /**
     * run this for local game
     * 
     */
    public TestingGame() {
	super(null, null);
	random = new Random();

	board = new NormalBoard();

	playerComms = new HashMap<>(2);

	Communication comm1 = new Communication(), comm2 = new Communication();

	player1 = new TestingPlayer("Dr. Monson", comm1.connectLocally());
	player2 = new TestingPlayer("Albert Einstein", comm2.connectLocally());
	playerComms.put(player1, comm1);
	playerComms.put(player2, comm2);

	allPlayers = new ArrayList<>(2);
	allPlayers.add(player1);
	allPlayers.add(player2);

	localPlayers = new ArrayList<>(2);
	localPlayers.add(player1);
	localPlayers.add(player2);

	currentTurn = new Turn(0, player1);

	testingFrame = new TestingFrame(this, player1, player2);
	testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * run this for online game
     */
    public TestingGame(Communication serverComm, long randomSeed, boolean first) {
	super(null, null);

	random = new Random(randomSeed);

	board = new NormalBoard();

	playerComms = new HashMap<>(2);

	Communication comm1 = new Communication();

	localPlayers = new ArrayList<>(2);
	if (first) {
	    player1 = new TestingPlayer("Dr. Monson", comm1.connectLocally());
	    player2 = new TestingPlayer("Albert Einstein", null);

	    playerComms.put(player1, comm1);
	    playerComms.put(player2, serverComm);

	    currentTurn = new Turn(0, player1);

	    localPlayers.add(player1);
	} else {
	    player1 = new TestingPlayer("Dr. Monson", null);
	    player2 = new TestingPlayer("Albert Einstein", comm1.connectLocally());

	    playerComms.put(player1, serverComm);
	    playerComms.put(player2, comm1);

	    currentTurn = new Turn(0, player1);

	    localPlayers.add(player2);
	}

	allPlayers = new ArrayList<>(2);
	allPlayers.add(player1);
	allPlayers.add(player2);

	testingFrame = new TestingFrame(this, first ? player1 : player2);
	testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void startGame() {
	testingFrame.updateInformation();
	testingFrame.setVisible(true);

	// TODO add stop statement
	// game loop for different turns
	while (true) {
	    handleTurn();
	    nextTurn();

	    testingFrame.gameDataPanel.resetForNewTurn();
	}
    }

    private TestingPlayer onTurnPlayer = null;
    private boolean isLocalPlayer = false;

    private Unit onTurnUnitPicked = null;
    private boolean onTurnHasSelectedUnit = false;
    private boolean onTurnHasMoved = false;
    private boolean onTurnHasAttacked = false;
    private boolean onTurnHasChangedDir = false;

    public void handleTurn() {
	TestingPlayer currentPlayer = (TestingPlayer) currentTurn.getPlayerTurn();
	Communication currentComm = getCommForPlayer(currentPlayer);

	Object received = null;

	onTurnPlayer = currentPlayer;
	isLocalPlayer = localPlayers.contains(currentPlayer);

	onTurnHasSelectedUnit = false;
	onTurnUnitPicked = null;
	onTurnHasMoved = false;
	onTurnHasAttacked = false;
	onTurnHasChangedDir = false;
	boolean shouldRun = true;

	while (shouldRun) {
	    testingFrame.updateInformation();
	    testingFrame.repaint();
	    shouldRun = handleCommand(currentComm);
	}
	if (onTurnHasAttacked) {
	    onTurnUnitPicked.getWaitProp().triggerWaitAfterAttack();
	}

	testingFrame.updateInformation();
	testingFrame.repaint();
    }

    public boolean handleCommand(Communication currentComm) {
	Object command = null, specs = null;

	boolean run = false;
	do {
	    run = false;

	    Object obj = currentComm.recieveObject();
	    if (Message.HOVER.equals(obj)) {
		hover((Coordinate) currentComm.recieveObject());
		run = true;
	    } else if (Message.isCommand(obj)) {
		command = obj;
		announceToAllPlayers(command);
		run = true;

		if (Message.END_TURN.equals(command)) {
		    return false;
		}
	    } else {
		specs = obj;
		announceToAllPlayers(specs);
		run = false;
	    }

	} while (run);

	handleFullNonHoverCommand(command, specs);
	return true;
    }

    public boolean canSelectUnit(Coordinate coor) {
	Unit unit = board.getUnitAt(coor);
	// TODO make sure unit is selectable

	if (onTurnHasSelectedUnit) {
	    return false;
	} else if (unit == null) {
	    return false;
	} else if (!unit.getOwnerProp().getCurrentPropertyValue().equals(onTurnPlayer)) {
	    return false;
	}
	return true;
    }

    public boolean canMoveTo(Coordinate coor) {
	Path path = onTurnUnitPicked.getGamePathTo(coor);
	if (onTurnHasMoved) {
	    return false;
	} else if (onTurnUnitPicked.getPosProp().getCurrentPropertyValue().equals(coor)) {
	    return false;
	} else if (!board.getSquare(coor).isEmpty()) {
	    return false;
	} else if (path == null) {
	    return false;
	}
	return true;
    }

    public boolean canAttack(Coordinate coor) {
	if (onTurnHasAttacked) {
	    return false;
	} else if (!onTurnUnitPicked.getAbilityProp().isActiveAbility()) {
	    return false;
	} else if (!onTurnUnitPicked.getAbilityProp().canCurrentlyUseAbility()) {
	    return false;
	} else if (onTurnUnitPicked.getAbilityProp() instanceof ActiveTargetAbilityProperty) {
	    if (!((ActiveTargetAbilityProperty) onTurnUnitPicked.getAbilityProp())
		    .canUseAbilityOn(board.getSquare(coor))) {
		return false;
	    }
	}
	return true;
    }

    public boolean canChangeDir(Direction dir) {
	if (onTurnHasChangedDir) {
	    return false;
	}
	return true;
    }

    public void handleFullNonHoverCommand(Object command, Object specs) {
	if (!Message.isCommand(command)) {
	    throw new RuntimeException("Not a command");
	}
	if (Message.UNIT_SELECT.equals(command)) {
	    if (canSelectUnit((Coordinate) specs)) {
		unitSelect((Coordinate) specs);
	    } else {
		throw new RuntimeException("Cannot select unit at " + specs);
	    }
	} else if (Message.UNIT_MOVE.equals(command)) {
	    if (canMoveTo((Coordinate) specs)) {
		Path path = unitMove((Coordinate) specs);
		announceToAllLocalPlayers(path);
	    } else {
		throw new RuntimeException("Cannot move to " + specs);
	    }
	} else if (Message.UNIT_ATTACK.equals(command)) {
	    if (canAttack((Coordinate) specs)) {
		unitAttack((Coordinate) specs);
	    } else {
		throw new RuntimeException("Cannot attack at " + specs);
	    }
	} else if (Message.UNIT_DIR.equals(command)) {
	    if (canChangeDir((Direction) specs)) {
		unitChangeDir((Direction) specs);
	    } else {
		throw new RuntimeException("Cannot change dir to " + specs);
	    }
	}
    }

    public void hover(Coordinate coor) {
	if (isLocalPlayer) {
	    announceToAllNonLocalPlayers(Message.HOVER);
	    announceToAllNonLocalPlayers(coor);
	} else {
	    announceToAllLocalPlayers(Message.HOVER);
	    announceToAllLocalPlayers(coor);
	}
    }

    public void unitSelect(Coordinate coor) {
	onTurnUnitPicked = board.getUnitAt(coor);
	onTurnHasSelectedUnit = true;
    }

    public Path unitMove(Coordinate coor) {
	Path path = onTurnUnitPicked.getGamePathTo(coor);

	onTurnUnitPicked.moveTakePath(path, onTurnPlayer);
	onTurnHasMoved = true;

	return path;
    }

    public void unitAttack(Coordinate coor) {
	onTurnUnitPicked.useAbility(board.getSquare(coor));
	onTurnHasAttacked = true;
    }

    public void unitChangeDir(Direction dir) {
	onTurnUnitPicked.getPosProp().getDirFacingProp().setPropertyValue(dir, onTurnPlayer);
	onTurnHasChangedDir = true;
    }

    public void announceToAllPlayers(Object obj) {
	// if it is being notified of a move from another game
	if (!isLocalPlayer) {
	    announceToAllLocalPlayers(obj);
	} else {
	    for (TestingPlayer player : allPlayers) {
		getCommForPlayer(player).sendObject(obj);
	    }
	}
    }

    public void announceToAllLocalPlayers(Object obj) {
	for (TestingPlayer player : localPlayers) {
	    getCommForPlayer(player).sendObject(obj);
	}
    }

    public void announceToAllNonLocalPlayers(Object obj) {
	for (TestingPlayer player : allPlayers) {
	    if (!localPlayers.contains(player)) {
		getCommForPlayer(player).sendObject(obj);
	    }
	}
    }

    private void nextTurn() {
	Player player = currentTurn.getPlayerTurn() == player1 ? player2 : player1;
	currentTurn = new Turn(currentTurn.getTurnNumber(), player);
    }

    public Communication getCommForPlayer(TestingPlayer player) {
	if (!playerComms.containsKey(player)) {
	    playerComms.put(player, new Communication());
	}
	return playerComms.get(player);
    }

    @Override
    public Board getBoard() {
	return board;
    }

    public Player getPlayer1() {
	return player1;
    }

    public Player getPlayer2() {
	return player2;
    }

    @Override
    public Turn getCurrentTurn() {
	return currentTurn;
    }

}

class Message {

    public static final String HOVER = "\\hover:";

    public static final String UNIT_SELECT = "\\unitselect:";
    public static final String UNIT_MOVE = "\\unitmove:";
    public static final String UNIT_ATTACK = "\\unitattack:";
    public static final String UNIT_DIR = "\\unitdir:";

    public static final String END_TURN = "\\endturn";

    public static boolean isCommand(Object message) {
	return message.equals(Message.HOVER) || message.equals(Message.UNIT_SELECT) || message.equals(Message.UNIT_MOVE)
		|| message.equals(Message.UNIT_ATTACK) || message.equals(Message.UNIT_DIR)
		|| message.equals(Message.END_TURN);
    }
}
