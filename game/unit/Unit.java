package game.unit;

import game.Player;
import game.Team;
import game.board.Board;
import game.board.Coordinate;
import game.board.Path;
import game.board.Square;
import game.util.Direction;
import game.util.PathFinder;

public abstract class Unit {

	protected final Player playerOwner;
	protected final Team teamOwner;

	protected final Board board;

	protected Coordinate coor;
	protected Direction directionFacing;

	protected int currentHealth, currentArmor;

	public Unit(Player playerOwner, Team teamOwner, Board board, Direction directionFacing, Coordinate coor) {
		this.playerOwner = playerOwner;
		this.teamOwner = teamOwner;
		this.board = board;
		this.coor = coor;
		this.directionFacing = directionFacing;
	}

	public Player getPlayerOwner() {
		return playerOwner;
	}

	public Team getTeamOwner() {
		return teamOwner;
	}

	public Board getBoard() {
		return board;
	}

	public Coordinate getCoor() {
		return coor;
	}

	public void setCoor(Coordinate coor) {
		this.coor = coor;
	}

	public abstract int getDefaultHealth();

	public int getCurrentHealth() {
		return currentHealth;
	}

	public abstract int getDefaultArmor();

	public int getCurrentArmor() {
		return currentArmor;
	}

	// TODO make sure all units should consider overriding these methods
	public boolean canMove() {
		return true;
	}

	// side stepping is when it can move out of way to let friendly piece pass
	public boolean canSideStep() {
		return true;
	}

	public abstract int getMoveRange();

	public boolean isInRangeOfWalking(Coordinate moveToCoor) {
		int walkingdistance = Math.abs(moveToCoor.x() - coor.x()) + Math.abs(moveToCoor.y() - coor.y());
		return walkingdistance <= getMoveRange();
	}

	// TODO make sure all units should consider overriding these methods
	public Path getPathTo(Coordinate moveToCoor) {
		if (!(canMove() && isInRangeOfWalking(moveToCoor)))
			return null;
		return PathFinder.getPath(this, moveToCoor);
	}

	public abstract boolean canUseAbilityOn(Object... args);

	/**
	 * Simply tells the unit to perform its ability given some arguments
	 * 
	 * @param args
	 */
	public abstract void performAbility(Object... args);

	/**
	 * Subclasses can choose to implement and call this method from
	 * performAbility(Object..) to make casting abilities easier.
	 * 
	 * @param sqr
	 */
	public abstract void abilityInteract(Square sqr);

	public void takeDamage(int dmg) {
		// TODO make better health reduction algorithm obviously
		currentHealth -= dmg;
	}

	public static boolean areAllies(Unit unit1, Unit unit2) {
		if (unit1.teamOwner == null) {
			return unit1.playerOwner.equals(unit2.playerOwner);
		} else {
			return unit1.teamOwner.equals(unit2.teamOwner);
		}
	}

}
