package game.unit;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Path;
import game.board.Square;
import game.interaction.Damage;
import game.interaction.effect.Affectable;
import game.unit.properties.ArmorProperty;
import game.unit.properties.CoordinateProperty;
import game.unit.properties.DirectionProperty;
import game.unit.properties.HealthProperty;
import game.unit.properties.OwnerProperty;
import game.util.Direction;
import game.util.PathFinder;

public abstract class Unit extends Affectable {

	protected final Game game;

	protected final OwnerProperty ownerProp;

	protected final CoordinateProperty coorProp;
	protected final DirectionProperty dirFacingProp;

	protected final HealthProperty healthProp;
	protected final ArmorProperty armorProp;

	public Unit(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
		this.game = game;

		this.ownerProp = new OwnerProperty(this, playerOwner);
		this.coorProp = new CoordinateProperty(this, coor);
		this.dirFacingProp = new DirectionProperty(this, directionFacing);
		this.healthProp = new HealthProperty(this, getDefaultHealth());
		this.armorProp = new ArmorProperty(this);
	}

	public Game getGame() {
		return game;
	}

	public OwnerProperty getOwnerProp() {
		return ownerProp;
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

	public abstract double getDefaultSideBlock();

	public abstract double getDefaultFrontBlock();

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
		int walkingdistance = Math.abs(moveToCoor.x() - coorProp.getProp().x())
				+ Math.abs(moveToCoor.y() - coorProp.getProp().y());
		return walkingdistance <= getMoveRange();
	}

	// TODO make sure all units should consider overriding these methods
	public Path getPathTo(Coordinate moveToCoor) {
		if (!(canMove() && isInRangeOfWalking(moveToCoor)))
			return null;
		return PathFinder.getPath(game.getBoard(), this, moveToCoor);
	}

	// TODO probably delete these out dated ability methods.

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
		armorProp.filterDamage(damage);
		if (!damage.wasBlocked())
			healthProp.takeRawDamage(damage);
	}

	public static boolean areAllies(Unit unit1, Unit unit2) {
		return unit1.ownerProp.getTeam().equals(unit2.ownerProp.getTeam());
	}

}
