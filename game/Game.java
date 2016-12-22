package game;

import game.board.Board;
import game.board.NormalBoard;

public class Game {

    private class PlayerTurnOrder {

	private final Player[] playerTurnOrder;

	private int currentIndex;

	public PlayerTurnOrder(Team team1, Team team2) {
	    playerTurnOrder = new Player[team1.numPlayers() + team2.numPlayers()];
	    currentIndex = 0;

	    int t1i = 0, t2i = 0;

	    Team currentTeam = team1.numPlayers() <= team2.numPlayers() ? team1 : team2;

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

	public Player currentPlayerTurn() {
	    return playerTurnOrder[currentIndex];
	}

	public Player nextPlayer() {
	    currentIndex++;
	    if (currentIndex == playerTurnOrder.length) {
		currentIndex = 0;
	    }
	    return playerTurnOrder[currentIndex];
	}

    }

    private final Board board;

    private Turn currentTurn;

    private final Team team1, team2;

    private final PlayerTurnOrder playerTurnOrder;

    public Game(Team team1, Team team2) {
	board = new NormalBoard();
	currentTurn = new Turn(null);

	this.team1 = team1;
	this.team2 = team2;
	playerTurnOrder = new PlayerTurnOrder(team1, team2);

    }

    public void determinePlayerTurnOrder() {

    }

    public Board getBoard() {
	return board;
    }

    public Turn getCurrentTurn() {
	return currentTurn;
    }
}
