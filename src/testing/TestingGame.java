package testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class TestingGame extends Game {

    private final Board board;

    private final TestingFrame testingFrame;

    private final TestingPlayer player1;
    private final TestingPlayer player2;
    private final List<TestingPlayer> allPlayers;

    private final HashMap<Player, Communication> playerComms;

    private Turn currentTurn;

    /**
     * run this for local game
     * 
     */
    public TestingGame() {
	super(null, null);
	board = new NormalBoard();

	playerComms = new HashMap<>(2);

	player1 = new TestingPlayer("Dr. Monson", this);
	player2 = new TestingPlayer("Albert Einstein", this);
	allPlayers = new ArrayList<>(2);
	allPlayers.add(player1);
	allPlayers.add(player2);

	currentTurn = new Turn(0, player1);

	testingFrame = new TestingFrame(this, player1, player2);
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
    }

    public void unitSelect(Coordinate coor) {
	onTurnUnitPicked = board.getUnitAt(coor);
	// TODO make sure unit is selectable
	if (onTurnHasSelectedUnit || onTurnUnitPicked == null
		|| !onTurnUnitPicked.getOwnerProp().getCurrentPropertyValue().equals(onTurnPlayer)) {
	    throw new RuntimeException("Already selected unit or no unit picked or invalid unit picked");
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

	announceToAllPlayers(path);
    }

    public void unitAttack(Coordinate coor) {
	if (onTurnHasAttacked || !onTurnUnitPicked.getAbilityProp().canCurrentlyUseAbility()) {
	    throw new RuntimeException("Has already attacked or can't use ability");
	}
	onTurnUnitPicked.attack(board.getSquare(coor));
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
	for (TestingPlayer player : allPlayers) {
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
    public static final String UNIT_ATTACK = "\\unitattack";
    public static final String UNIT_DIR = "\\unitdir";

    public static final String END_TURN = "\\endturn";

    public static boolean isCommand(Object message) {
	return message.equals(HOVER) || message.equals(UNIT_SELECT) || message.equals(UNIT_MOVE)
		|| message.equals(UNIT_ATTACK) || message.equals(UNIT_DIR) || message.equals(END_TURN);
    }
}
