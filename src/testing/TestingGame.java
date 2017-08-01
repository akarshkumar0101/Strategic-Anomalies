package testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import game.Communication;
import game.Game;
import game.Player;
import game.Turn;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.NormalBoard;
import game.board.Path;
import game.board.Square;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.ability.ActiveTargetAbilityProperty;
import setup.SetupTemplate;

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

    public static final String START_GAME = "game init99";

    private final boolean first;

    private final Board board;

    public TestingFrame testingFrame;

    private final TestingPlayer player1;
    private final TestingPlayer player2;
    private final List<TestingPlayer> allPlayers;

    private final List<TestingPlayer> localPlayers;

    private final HashMap<TestingPlayer, Communication> playerComms;

    private Turn currentTurn;

    public final Random random;

    private final List<Unit> allUnits;

    public final IncidentReporter gameStartReporter;

    // The purpose of synchronizing some of the methods in TestingGame.java
    // TestingFrame accesses some methods in TestingGame to get details about the
    // turn and game
    // The game's data can change while the TestingFrame accesses it from another
    // thread causing a disaster
    // Synchronizing all methods that give access to the data and all changes in
    // data will prevent this
    // The things that need to be synchronized:
    // get values (accessed by other threads) and set values (this game thread)

    /**
     * run this for online game
     */
    public TestingGame(Communication serverComm, long randomSeed, boolean first) {
	super(null, null);
	this.first = first;

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

	    localPlayers.add(player1);
	} else {
	    player2 = new TestingPlayer("Dr. Monson", null);
	    player1 = new TestingPlayer("Albert Einstein", comm1.connectLocally());

	    playerComms.put(player2, serverComm);
	    playerComms.put(player1, comm1);

	    localPlayers.add(player1);
	}

	allPlayers = new ArrayList<>(2);
	allPlayers.add(player1);
	allPlayers.add(player2);

	testingFrame = new TestingFrame(this, player1);

	allUnits = new ArrayList<>();

	gameStartReporter = new IncidentReporter();
    }

    public void setupBoardWithTemplates(SetupTemplate homeSel, SetupTemplate awaySel) {
	board.setupBoard(this, player1, player2, homeSel, awaySel);

	for (Square sqr : board) {
	    if (!sqr.isEmpty()) {
		allUnits.add(sqr.getUnitOnTop());
	    }
	}
    }

    public List<Unit> getAllUnits() {
	return allUnits;
    }

    private TestingPlayer onTurnPlayer = null;
    private boolean isLocalPlayer = false;

    private Unit onTurnUnitPicked = null;
    private boolean onTurnHasSelectedUnit = false;
    private boolean onTurnHasMoved = false;
    private boolean onTurnHasAttacked = false;
    private boolean onTurnHasChangedDir = false;

    public void startGame() {
	testingFrame.startFrame();

	// TODO add stop statement
	// game loop for different turns
	establishGame();

	gameStartReporter.reportIncident();

	announceToAllLocalPlayers(TestingGame.START_GAME);
	while (true) {
	    handleTurn();
	    nextTurn();
	}
    }

    private void establishGame() {
	synchronized (this) {
	    if (first) {
		// offset bc next turn will say next turn
		currentTurn = new Turn(0, player1);
	    } else {
		// offset bc next turn will say next turn
		currentTurn = new Turn(0, player2);
	    }
	}
    }

    private void handleTurn() {
	TestingPlayer currentPlayer = (TestingPlayer) currentTurn.getPlayerTurn();
	Communication currentComm = getCommForPlayer(currentPlayer);

	synchronized (this) {
	    onTurnPlayer = currentPlayer;
	    isLocalPlayer = localPlayers.contains(currentPlayer);
	}

	boolean shouldRun = true;

	while (shouldRun) {
	    shouldRun = handleCommand(currentComm);
	}

	if (onTurnHasAttacked) {
	    onTurnUnitPicked.getWaitProp().triggerWaitAfterAttack();
	}
    }

    private boolean handleCommand(Communication currentComm) {
	Object command = null, specs = null;

	boolean run = false;
	do {
	    run = false;

	    Object obj = currentComm.recieveObject();
	    if (Message.HOVER.equals(obj)) {
		hover((Coordinate) currentComm.recieveObject());
		run = true;
	    } else if (obj instanceof Message) {
		command = obj;
		// announce immediately bc command doesn't modify game
		announceToAllPlayers(command);
		run = true;

		if (Message.END_TURN.equals(command)) {
		    return false;
		}
	    } else {
		specs = obj;
		run = false;
	    }

	} while (run);

	handleFullCoreCommand((Message) command, specs);
	// annouce after game handles everything
	announceToAllPlayers(specs);
	return true;
    }

    public boolean hasSelectedUnit() {
	synchronized (this) {
	    return onTurnHasSelectedUnit;
	}
    }

    public boolean canSelectUnit(Coordinate coor) {
	synchronized (this) {
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
    }

    public Unit getSelectedUnit() {
	synchronized (this) {
	    if (hasSelectedUnit()) {
		return onTurnUnitPicked;
	    } else {
		return null;
	    }
	}
    }

    public boolean hasMoved() {
	synchronized (this) {
	    return onTurnHasMoved;
	}
    }

    public boolean canMoveTo(Coordinate coor) {
	synchronized (this) {
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
    }

    public boolean hasAttacked() {
	synchronized (this) {
	    return onTurnHasAttacked;
	}
    }

    public boolean canAttack(Coordinate coor) {
	synchronized (this) {
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
    }

    public boolean hasChangedDir() {
	synchronized (this) {
	    return onTurnHasChangedDir;
	}
    }

    public boolean canChangeDir(Direction dir) {
	synchronized (this) {
	    if (onTurnHasChangedDir) {
		return false;
	    }
	    return true;
	}
    }

    private void handleFullCoreCommand(Message command, Object specs) {

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

    private void hover(Coordinate coor) {
	if (isLocalPlayer) {
	    announceToAllNonLocalPlayers(Message.HOVER);
	    announceToAllNonLocalPlayers(coor);
	} else {
	    announceToAllLocalPlayers(Message.HOVER);
	    announceToAllLocalPlayers(coor);
	}
    }

    private void unitSelect(Coordinate coor) {
	synchronized (this) {
	    onTurnUnitPicked = board.getUnitAt(coor);
	    onTurnHasSelectedUnit = true;
	}
    }

    private Path unitMove(Coordinate coor) {
	synchronized (this) {
	    Path path = onTurnUnitPicked.getGamePathTo(coor);

	    onTurnUnitPicked.moveTakePath(path, onTurnPlayer);
	    onTurnHasMoved = true;

	    return path;
	}
    }

    private void unitAttack(Coordinate coor) {
	synchronized (this) {
	    onTurnHasAttacked = true;
	    onTurnUnitPicked.useAbility(board.getSquare(coor));
	}
    }

    private void unitChangeDir(Direction dir) {
	synchronized (this) {
	    onTurnUnitPicked.getPosProp().getDirFacingProp().setPropertyValue(dir, onTurnPlayer);
	    onTurnHasChangedDir = true;
	}
    }

    private void announceToAllPlayers(Object obj) {
	// if it is being notified of a move from another game
	if (!isLocalPlayer) {
	    announceToAllLocalPlayers(obj);
	} else {
	    for (TestingPlayer player : allPlayers) {
		getCommForPlayer(player).sendObject(obj);
	    }
	}
    }

    private void announceToAllLocalPlayers(Object obj) {
	for (TestingPlayer player : localPlayers) {
	    getCommForPlayer(player).sendObject(obj);
	}
    }

    private void announceToAllNonLocalPlayers(Object obj) {
	for (TestingPlayer player : allPlayers) {
	    if (!localPlayers.contains(player)) {
		getCommForPlayer(player).sendObject(obj);
	    }
	}
    }

    private void nextTurn() {
	synchronized (this) {
	    Player player = currentTurn.getPlayerTurn() == player1 ? player2 : player1;
	    currentTurn = new Turn(currentTurn.getTurnNumber() + 1, player);

	    onTurnHasSelectedUnit = false;
	    onTurnUnitPicked = null;
	    onTurnHasMoved = false;
	    onTurnHasAttacked = false;
	    onTurnHasChangedDir = false;
	}
    }

    private Communication getCommForPlayer(TestingPlayer player) {
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
	synchronized (this) {
	    return currentTurn;
	}
    }

}
