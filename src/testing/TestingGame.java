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

    private final TestingFrame testingFrame;

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
	}
    }

    private TestingPlayer onTurnPlayer = null;
    private Unit onTurnUnitPicked = null;
    private boolean onTurnHasSelectedUnit = false;
    private boolean onTurnHasMoved = false;
    private boolean onTurnHasAttacked = false;
    private boolean onTurnHasChangedDir = false;

    public void handleTurn() {
	TestingPlayer currentPlayer = (TestingPlayer) currentTurn.getPlayerTurn();
	System.out.println(currentPlayer.getName() + " TURN");
	System.out.println(testingFrame.playerIsUsingThisFrame(currentPlayer));
	Communication currentComm = getCommForPlayer(currentPlayer);

	Object received = null;

	onTurnPlayer = currentPlayer;
	onTurnHasSelectedUnit = false;
	onTurnUnitPicked = null;
	onTurnHasMoved = false;
	onTurnHasAttacked = false;
	onTurnHasChangedDir = false;
	boolean shouldRun = true;
	while (shouldRun) {
	    shouldRun = handleCommand(currentComm);
	}
	if (onTurnHasAttacked) {
	    onTurnUnitPicked.getWaitProp().triggerWaitAfterAttack();
	    testingFrame.updateInformation();
	    testingFrame.repaint();
	}
    }

    public boolean handleCommand(Communication currentComm) {
	return handleCommand(currentComm.recieveObject(), currentComm);
    }

    public boolean handleCommand(Object command, Communication currentComm) {

	if (!Message.isCommand(command)) {
	    throw new RuntimeException("Not a command");
	}

	announceToAllPlayers(command);

	if (Message.END_TURN.equals(command)) {
	    return false;
	}

	Object specifications = currentComm.recieveObject();

	if (Message.isCommand(specifications)) {
	    return handleCommand(specifications, currentComm);
	}

	try {
	    if (command.equals(Message.HOVER)) {
		hover((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_SELECT)) {
		unitSelect((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_MOVE)) {
		unitMove((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_ATTACK)) {
		unitAttack((Coordinate) specifications);
	    } else if (command.equals(Message.UNIT_DIR)) {
		unitChangeDir((Direction) specifications);
	    }

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	announceToAllPlayers(specifications);

	return true;

    }

    public void hover(Coordinate coor) {
	// if it is being notified of a move from another game
	if (!localPlayers.contains(onTurnPlayer)) {
	    announceToAllLocalPlayers(coor);
	}
    }

    public void unitSelect(Coordinate coor) {
	onTurnUnitPicked = board.getUnitAt(coor);
	// TODO make sure unit is selectable

	if (onTurnHasSelectedUnit) {
	    throw new RuntimeException("Already selected unit");
	} else if (onTurnUnitPicked == null) {
	    throw new RuntimeException("no unit picked");
	} else if (!onTurnUnitPicked.getOwnerProp().getCurrentPropertyValue().equals(onTurnPlayer)) {
	    throw new RuntimeException("invalid unit picked");
	}
	onTurnHasSelectedUnit = true;
    }

    public void unitMove(Coordinate coor) {
	Path path = onTurnUnitPicked.getGamePathTo(coor);
	if (onTurnHasMoved || onTurnUnitPicked.getPosProp().getCurrentPropertyValue().equals(coor) || path == null) {
	    throw new RuntimeException("Already moved or no movement or can't move unit to: " + coor);
	}
	onTurnUnitPicked.moveTakePath(path, onTurnPlayer);
	onTurnHasMoved = true;

	announceToAllLocalPlayers(path);

    }

    public void unitAttack(Coordinate coor) {
	if (onTurnHasAttacked || !onTurnUnitPicked.getAbilityProp().canCurrentlyUseAbility()) {
	    throw new RuntimeException("Has already attacked or can't use ability");
	}
	onTurnUnitPicked.useAbility(board.getSquare(coor));
	onTurnHasAttacked = true;
    }

    public void unitChangeDir(Direction dir) {
	if (onTurnHasChangedDir) {
	    throw new RuntimeException("Has already changed dir or ");
	}
	onTurnUnitPicked.getPosProp().getDirFacingProp().setPropertyValue(dir, onTurnPlayer);
	onTurnHasChangedDir = true;
    }

    public void announceToAllPlayers(Object obj) {
	// if it is being notified of a move from another game
	if (!localPlayers.contains(onTurnPlayer)) {
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
