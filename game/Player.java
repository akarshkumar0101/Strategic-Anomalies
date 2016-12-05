package game;

/**
 * Player that is in a team and a game, will be tied to a data stream that gives
 * inputs, either by live program input, or input over Internet (multiplayer
 * online)
 * 
 * @author Akarsh
 *
 */
public class Player {

	/**
	 * The team the player belongs to
	 */
	private final Team team;

	/**
	 * @param team
	 *            the player will belong to
	 */
	public Player(Team team) {
		this.team = team;
	}

	/**
	 * @return team the player belongs to
	 */
	public Team getTeam() {
		return team;
	}

}
