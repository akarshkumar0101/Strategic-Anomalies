package game;

import game.board.Board;
import game.board.NormalBoard;

public class Game {

	private final Board board;

	private Turn currentTurn;

	public Game() {
		board = new NormalBoard();
		currentTurn = new Turn(null);
	}

	public Board getBoard() {
		return board;
	}

	public Turn getCurrentTurn() {
		return currentTurn;
	}
}
