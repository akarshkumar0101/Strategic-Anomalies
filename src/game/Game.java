package game;

import game.board.Board;
import game.board.NormalBoard;
import main.Account;

public class Game {

    private final Board board;

    private final Team team1, team2;

    private final TurnOrder turnOrder;

    // TODO in the future make it to where it accepts game input streams and a
    // turn order from the server.
    public Game(Account[] accounts1, Account[] accounts2) {
	board = new NormalBoard();

	team1 = new Team(accounts1);
	team2 = new Team(accounts2);

	turnOrder = new TurnOrder(team1, team2);

    }

    public Board getBoard() {
	return board;
    }

    public Team getTeam1() {
	return team1;
    }

    public Team getTeam2() {
	return team2;
    }

    public Turn getCurrentTurn() {
	return turnOrder.currentTurn();
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

    private Turn nextTurn() {
	currentIndex = (currentIndex + 1) % playerTurnOrder.length;
	turnNumber++;
	currentTurn = null;
	return currentTurn();
    }

}
