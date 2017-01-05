package testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import game.Communication;
import game.Player;
import game.Turn;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.NormalBoard;
import game.unit.Unit;

public class TestingGame {

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
	board = new NormalBoard();

	playerComms = new HashMap<>(2);

	player1 = new TestingPlayer(this);
	player2 = new TestingPlayer(this);
	allPlayers = new ArrayList<>(2);
	allPlayers.add(player1);
	allPlayers.add(player2);

	testingFrame = new TestingFrame(this, player1, player2);
	testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	currentTurn = new Turn(0, player1);
    }

    public void startGame() {
	testingFrame.updateInformation();
	testingFrame.setVisible(true);

	// TODO add stop statement
	TestingPlayer currentPlayer = null;
	Communication currentComm = null;
	while (true) {
	    currentPlayer = (TestingPlayer) currentTurn.getPlayerTurn();
	    currentComm = getCommForPlayer(currentPlayer);

	    Object received = null;
	    do {
		received = currentComm.recieveObject();

		announceToAllPlayers(received);

		Unit unitPicked = null;

		if (!received.equals(Message.END_TURN)) {
		    Object detail = currentComm.recieveObject();

		    if (received.equals(Message.HOVER)) {
			Coordinate coor = (Coordinate) detail;
		    } else if (received.equals(Message.UNIT_SELECT)) {
			Coordinate coor = (Coordinate) detail;
			unitPicked = board.getUnitAt(coor);
		    } else if (received.equals(Message.UNIT_MOVE)) {
			Coordinate coor = (Coordinate) detail;

		    } else if (received.equals(Message.UNIT_ATTACK)) {
			Coordinate coor = (Coordinate) detail;

		    } else if (received.equals(Message.UNIT_DIR)) {
			Direction dir = (Direction) detail;

		    }
		    announceToAllPlayers(detail);

		} else {

		}

	    } while (!received.equals(Message.END_TURN));

	    nextTurn();
	}
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

}
