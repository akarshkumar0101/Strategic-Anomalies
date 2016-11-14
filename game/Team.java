package game;

public class Team {

	private final Player[] players;

	public Team(Player... playersarr) {
		if (playersarr == null || playersarr.length == 0)
			throw new IllegalArgumentException("Attempted to create a team with null players or 0 players.");
		players = playersarr;
	}

	public Player[] getPlayers() {
		return players;
	}

	public boolean isMultiplayer() {
		return players.length > 1;
	}

	public boolean contains(Player player) {
		for (Player p : players) {
			if (p.equals(player))
				return true;
		}
		return false;
	}
}
