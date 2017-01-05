package testing;

import java.util.HashMap;

import game.Communication;
import game.Player;
import game.Turn;
import game.board.Board;
import game.board.NormalBoard;

public class TestingGame {

    private final Board board;

    private final TestingFrame testingFrame;

    private final TestingPlayer player1;
    private final TestingPlayer player2;

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

	testingFrame = new TestingFrame(this, player1, player2);

    }

    public void start() {

    }

    public Communication getCommForPlayer(TestingPlayer player) {
	if (!playerComms.containsKey(player)) {
	    playerComms.put(player, new Communication());
	}
	return playerComms.get(player);
    }

    private void nextTurn() {
	Player player = currentTurn.getPlayerTurn() == player1 ? player2 : player1;
	currentTurn = new Turn(currentTurn.getTurnNumber(), player);
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
