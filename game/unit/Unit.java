package game.unit;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Path;
import game.interaction.effect.Affectable;
import game.unit.properties.CoordinateProperty;
import game.unit.properties.DirectionProperty;
import game.unit.properties.HealthProperty;
import game.unit.properties.OwnerProperty;
import game.unit.properties.UnitDefaults;
import game.util.Direction;
import game.util.PathFinder;

public abstract class Unit extends Affectable implements UnitDefaults {

	protected final Game game;

	protected final OwnerProperty ownerProp;

	protected final CoordinateProperty coorProp;
	protected final DirectionProperty dirFacingProp;

	protected final HealthProperty healthProp;

	public Unit(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
		this.game = game;

		this.ownerProp = new OwnerProperty(this, playerOwner);
		this.coorProp = new CoordinateProperty(this, coor);
		this.dirFacingProp = new DirectionProperty(this, directionFacing);
		this.healthProp = new HealthProperty(this, getDefaultHealth());
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

	public HealthProperty getHealthProp() {
		return healthProp;
	}


	public boolean isInRangeOfWalking(Coordinate moveToCoor) {
		int walkingdistance = Math.abs(moveToCoor.x() - coorProp.getProp().x())
				+ Math.abs(moveToCoor.y() - coorProp.getProp().y());
		return walkingdistance <= getMoveRange();
	}

	// TODO make sure all units should consider overriding these methods
	public Path getPathTo(Coordinate moveToCoor) {
		if (!(canMove() && isInRangeOfWalking(moveToCoor)))
			return null;
		return PathFinder.getPath( this, moveToCoor);
	}


	public static boolean areAllies(Unit unit1, Unit unit2) {
		return unit1.ownerProp.getTeam().equals(unit2.ownerProp.getTeam());
	}

}
