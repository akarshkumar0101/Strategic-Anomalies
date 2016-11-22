package game;

public class Team {

	private final Player[] players;

	// TODO replace int[] args with Account[] and send each account data to
	// player creation
	public Team(int... args) {
		players = new Player[args.length];
		for (int i = 0; i < args.length; i++) {
			players[i] = new Player(this);
		}
	}

	public Player[] getPlayers() {
		return players;
	}

	public int numPlayers() {
		return players.length;
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
