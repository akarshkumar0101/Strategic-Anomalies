package game;

public class Turn {

    private final int turnNumber;

    private final Player playerTurn;

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
