package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.NormalBoard;
import game.board.Path;
import game.board.Square;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.ability.ActiveAbility;
import game.unit.property.ability.ActiveTargetAbility;
import io.Communication;
import setup.SetupTemplate;
import testing.Message;
import testing.ui.GameWindow;

public class Game {

    // TODO in the future make it to where it accepts game input streams and a
    // turn order from the server.

    public static final String START_GAME = "game init99";

    private final boolean first;

    private final Board board;

    public GameWindow gameWindow;

    private final Player player1;
    private final Player player2;
    private final List<Player> allPlayers;

    private final List<Player> localPlayers;

    private final Map<Player, Communication> playerComms;

    private Turn currentTurn;

    public final Random random;

    private final List<Unit> allUnits;

    public final IncidentReporter gameStartReporter;

    public final IncidentReporter turnStartReporter;
    public final IncidentReporter turnEndReporter;

    // The purpose of synchronizing some of the methods in TestingGame.java
    // GameWindow accesses some methods in TestingGame to get details about the
    // turn and game
    // The game's data can change while the GameWindow accesses it from another
    // thread causing a disaster
    // Synchronizing all methods that give access to the data and all changes in
    // data will prevent this
    // The things that need to be synchronized:
    // get values (accessed by other threads) and set values (this game thread)

    /**
     * run this for online game
     */
    public Game(Communication serverComm, long randomSeed, boolean first, String myname, String oppname) {
	this.first = first;

	random = new Random(randomSeed);

	board = new NormalBoard();

	playerComms = new HashMap<>(2);

	Communication comm1 = new Communication();

	player1 = new Player(myname, comm1.connectLocally());
	player2 = new Player(oppname, null);

	playerComms.put(player1, comm1);
	playerComms.put(player2, serverComm);

	allPlayers = new ArrayList<>(2);
	allPlayers.add(player1);
	allPlayers.add(player2);

	localPlayers = new ArrayList<>(2);
	localPlayers.add(player1);

	gameWindow = new GameWindow(this, player1);

	allUnits = new ArrayList<>();

	gameStartReporter = new IncidentReporter();
	turnStartReporter = new IncidentReporter();
	turnEndReporter = new IncidentReporter();
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

    private Player onTurnPlayer = null;
    private boolean isLocalPlayer = false;

    // attack means ability*
    private Unit onTurnUnitPicked = null;
    private boolean onTurnHasSelectedUnit = false;
    private boolean onTurnHasMoved = false;
    private boolean onTurnHasAttacked = false;
    private boolean onTurnHasChangedDir = false;
    private boolean onTurnHasDied = false;

    private final IncidentListener deathMidTurnListener = specifications -> {
	onTurnHasDied = true;
    };

    public void startGame() {
	if (first) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	gameWindow.startFrame();

	// TODO add stop statement
	// game loop for different turns
	establishGame();

	turnEndReporter.add(specifications -> {
	    if (onTurnHasMoved) {
		onTurnUnitPicked.getWaitProp().triggerWaitForMove();
	    }
	    if (onTurnHasAttacked) {
		onTurnUnitPicked.getWaitProp().triggerWaitForAttack();
	    }
	});

	gameStartReporter.reportIncident();

	announceToAllLocalPlayers(Game.START_GAME);
	while (true) {

	    turnStartReporter.reportIncident(currentTurn);

	    handleTurn();

	    turnEndReporter.reportIncident(currentTurn);

	    nextTurn();
	}
    }

    private void establishGame() {
	synchronized (this) {
	    if (first) {
		currentTurn = new Turn(0, player1);
	    } else {
		currentTurn = new Turn(0, player2);
	    }
	}
    }

    private void handleTurn() {
	Player currentPlayer = currentTurn.getPlayerTurn();
	Communication currentComm = getCommForPlayer(currentPlayer);

	synchronized (this) {
	    onTurnPlayer = currentPlayer;
	    isLocalPlayer = localPlayers.contains(currentPlayer);
	}

	boolean shouldRun = true;
	while (shouldRun) {
	    shouldRun = handleCommand(currentComm);
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

    public boolean hasDiedOnTurn() {
	synchronized (this) {
	    return onTurnHasDied;
	}
    }

    public boolean hasSelectedUnit() {
	synchronized (this) {
	    return onTurnHasSelectedUnit;
	}
    }

    public boolean hasEditedTurn() {
	synchronized (this) {
	    return onTurnHasMoved || onTurnHasAttacked || onTurnHasChangedDir;
	}
    }

    public boolean hasMoved() {
	synchronized (this) {
	    return onTurnHasMoved;
	}
    }

    public boolean hasAttacked() {
	synchronized (this) {
	    return onTurnHasAttacked;
	}
    }

    public boolean hasChangedDir() {
	synchronized (this) {
	    return onTurnHasChangedDir;
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

    public boolean canSelectUnit() {
	return !onTurnHasDied && !hasEditedTurn();
    }

    public boolean canMove() {
	return !onTurnHasDied && onTurnHasSelectedUnit && !onTurnHasMoved;
    }

    public boolean canAttack() {
	return !onTurnHasDied && onTurnHasSelectedUnit && !onTurnHasAttacked;
    }

    public boolean canChangeDir() {
	return !onTurnHasDied && onTurnHasSelectedUnit && !onTurnHasChangedDir;
    }

    public boolean canSelectUnit(Coordinate coor) {
	synchronized (this) {
	    Unit unit = board.getUnitAt(coor);
	    // TODO make sure unit is selectable
	    if (onTurnHasDied) {
		return false;
	    } else if (hasEditedTurn()) {
		return false;
	    } else if (unit == null) {
		return false;
	    } else if (!unit.getOwnerProp().getValue().equals(onTurnPlayer)) {
		return false;
	    }
	    return true;
	}
    }

    public boolean canMoveTo(Coordinate coor) {
	synchronized (this) {
	    if (!onTurnHasSelectedUnit) {
		return false;
	    }
	    Path path = onTurnUnitPicked.getGamePathTo(coor);
	    if (onTurnHasDied) {
		return false;
	    } else if (onTurnHasMoved) {
		return false;
	    } else if (onTurnUnitPicked.getPosProp().getValue().equals(coor)) {
		return false;
	    } else if (!board.getSquare(coor).isEmpty()) {
		return false;
	    } else if (path == null) {
		return false;
	    }
	    return true;
	}
    }

    public boolean canAttack(Coordinate coor) {
	synchronized (this) {
	    if (onTurnHasDied) {
		return false;
	    } else if (onTurnHasAttacked) {
		return false;
	    } else if (!onTurnHasSelectedUnit) {
		return false;
	    } else if (!(onTurnUnitPicked.getAbility() instanceof ActiveAbility)) {
		return false;
	    } else if (!((ActiveAbility) onTurnUnitPicked.getAbility()).canUseAbility()) {
		return false;
	    } else if (onTurnUnitPicked.getAbility() instanceof ActiveTargetAbility) {
		if (!((ActiveTargetAbility) onTurnUnitPicked.getAbility()).canUseAbilityOn(board.getSquare(coor))) {
		    return false;
		}
	    }
	    return true;
	}
    }

    public boolean canChangeDir(Direction dir) {
	synchronized (this) {
	    if (onTurnHasDied) {
		return false;
	    } else if (!onTurnHasSelectedUnit) {
		return false;
	    } else if (onTurnHasChangedDir) {
		return false;
	    }
	    return true;
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
	    if (onTurnUnitPicked != null) {
		onTurnUnitPicked.getDeathReporter().remove(deathMidTurnListener);
	    }
	    onTurnUnitPicked = board.getUnitAt(coor);
	    onTurnHasSelectedUnit = true;

	    onTurnUnitPicked.getDeathReporter().add(deathMidTurnListener);
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
	    onTurnUnitPicked.getPosProp().getDirFacingProp().setValue(dir, onTurnPlayer);
	    onTurnHasChangedDir = true;
	}
    }

    private void announceToAllPlayers(Object obj) {
	// if it is being notified of a move from another game
	if (!isLocalPlayer) {
	    announceToAllLocalPlayers(obj);
	} else {
	    for (Player player : allPlayers) {
		getCommForPlayer(player).sendObject(obj);
	    }
	}
    }

    private void announceToAllLocalPlayers(Object obj) {
	for (Player player : localPlayers) {
	    getCommForPlayer(player).sendObject(obj);
	}
    }

    private void announceToAllNonLocalPlayers(Object obj) {
	for (Player player : allPlayers) {
	    if (!localPlayers.contains(player)) {
		getCommForPlayer(player).sendObject(obj);
	    }
	}
    }

    private void nextTurn() {
	synchronized (this) {
	    Player player = currentTurn.getPlayerTurn() == player1 ? player2 : player1;
	    currentTurn = new Turn(currentTurn.getTurnNumber() + 1, player);

	    if (onTurnUnitPicked != null) {
		onTurnUnitPicked.getDeathReporter().remove(deathMidTurnListener);
	    }

	    onTurnHasSelectedUnit = false;
	    onTurnUnitPicked = null;
	    onTurnHasMoved = false;
	    onTurnHasAttacked = false;
	    onTurnHasChangedDir = false;
	    onTurnHasDied = false;
	}
    }

    private Communication getCommForPlayer(Player player) {
	if (!playerComms.containsKey(player)) {
	    playerComms.put(player, new Communication());
	}
	return playerComms.get(player);
    }

    public Board getBoard() {
	return board;
    }

    public Player getPlayer1() {
	return player1;
    }

    public Player getPlayer2() {
	return player2;
    }

    public Turn getCurrentTurn() {
	synchronized (this) {
	    return currentTurn;
	}
    }

}

class TurnOrder {

    private final Player[] playerTurnOrder;

    private int currentIndex;

    private int turnNumber;

    private Turn currentTurn;

    public TurnOrder(Team team1, Team team2) {
	playerTurnOrder = new Player[team1.numPlayers() + team2.numPlayers()];

	determinePlayerTurnOrder(team1, team2);

	currentIndex = 0;
	turnNumber = 0;
    }

    public void determinePlayerTurnOrder(Team team1, Team team2) {

	int t1i = 0, t2i = 0;

	Team currentTeam = null;

	if (team1.numPlayers() == team1.numPlayers()) {
	    currentTeam = Math.random() > .5 ? team2 : team1;
	} else if (team1.numPlayers() < team2.numPlayers()) {
	    currentTeam = team1;
	} else {
	    currentTeam = team2;
	}

	for (int i = 0; i < playerTurnOrder.length; i++) {

	    if (currentTeam == team1) {
		playerTurnOrder[i++] = team1.getPlayers()[t1i++];
	    } else if (currentTeam == team2) {
		playerTurnOrder[i++] = team2.getPlayers()[t2i++];
	    }

	    currentTeam = currentTeam == team1 ? team2 : team1;
	    if (t1i == team1.numPlayers()) {
		currentTeam = team2;
	    }
	    if (t2i == team2.numPlayers()) {
		currentTeam = team1;
	    }
	}
    }

    public Turn currentTurn() {
	if (currentTurn == null) {
	    return currentTurn = new Turn(turnNumber, playerTurnOrder[currentIndex]);
	} else {
	    return currentTurn;
	}
    }

    public Turn nextTurn() {
	currentIndex = (currentIndex + 1) % playerTurnOrder.length;
	turnNumber++;
	currentTurn = null;
	return currentTurn();
    }

}
