package game;

public class Turn {

	public final int turnNumber;
	public final Player playerTurn;

	public Turn(Player playerTurn) {
		this(1, playerTurn);
	}

	public Turn(int turnNumber, Player playerTurn) {
		this.turnNumber = turnNumber;
		this.playerTurn = playerTurn;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public Player getPlayerTurn() {
		return playerTurn;
	}
}
