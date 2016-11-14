package game.unit;

import game.Player;
import game.Team;
import game.board.Board;
import game.board.Coordinate;
import game.board.Path;
import game.board.Square;
import game.interaction.Damage;
import game.unit.properties.ArmorProperty;
import game.unit.properties.CoordinateProperty;
import game.unit.properties.DirectionProperty;
import game.unit.properties.HealthProperty;
import game.util.Direction;
import game.util.PathFinder;

public abstract class Unit {

	protected final Player playerOwner;
	protected final Team teamOwner;

	protected final Board board;

	protected final CoordinateProperty coorProp;
	protected final DirectionProperty dirFacingProp;
	protected final HealthProperty healthProp;
	protected final ArmorProperty armorProp;

	public Unit(Player playerOwner, Team teamOwner, Board board, Direction directionFacing, Coordinate coor) {
		this.playerOwner = playerOwner;
		this.teamOwner = teamOwner;
		this.board = board;

		this.coorProp = new CoordinateProperty(this, coor);
		this.dirFacingProp = new DirectionProperty(this, directionFacing);
		this.healthProp = new HealthProperty(this, getDefaultHealth());
		this.armorProp = new ArmorProperty(this);
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

	public CoordinateProperty getCoorProp() {
		return coorProp;
	}

	public DirectionProperty getDirFacingProp() {
		return dirFacingProp;
	}

	public abstract int getDefaultHealth();

	public HealthProperty getHealthProp() {
		return healthProp;
	}

	public abstract int getDefaultArmor();

	public ArmorProperty getArmorProp() {
		return armorProp;
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
		int walkingdistance = Math.abs(moveToCoor.x() - coorProp.getCoor().x())
				+ Math.abs(moveToCoor.y() - coorProp.getCoor().y());
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

	public void takeDamage(Damage damage) {
		// determine if blocks
		healthProp.takeDamage(damage);
	}

	public static boolean areAllies(Unit unit1, Unit unit2) {
		return unit1.teamOwner.equals(unit2.teamOwner);
	}

}
