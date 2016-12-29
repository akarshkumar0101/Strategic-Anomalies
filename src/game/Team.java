package game;

import main.Account;

public class Team {

    /**
     * Players on the team
     */
    private final Player[] players;

    public Team(Account[] accounts) {
	players = new Player[accounts.length];
	for (int i = 0; i < accounts.length; i++) {
	    // TODO manage stuff
	    players[i] = new Player(this);
	}
    }

    /**
     * @return the Player[] of players on the team
     */
    public Player[] getPlayers() {
	return players;
    }

    /**
     * @return the number of players on the team
     */
    public int numPlayers() {
	return players.length;
    }

    /**
     * @return true if team is made of more than one player
     */
    public boolean isMultiplayer() {
	return players.length > 1;
    }

    /**
     * @param player
     * @return true if team contains that player
     */
    public boolean contains(Player player) {
	for (Player p : players) {
	    if (p.equals(player)) {
		return true;
	    }
	}
	return false;
    }
}
