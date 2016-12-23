package game.unit;

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Path;
import game.interaction.effect.Affectable;
import game.unit.properties.AbilityProperty;
import game.unit.properties.HealthProperty;
import game.unit.properties.MovingProperty;
import game.unit.properties.OwnerProperty;
import game.unit.properties.PositionProperty;
import game.unit.properties.StunnedProperty;
import game.unit.properties.UnitDefaults;
import game.util.Direction;
import game.util.PathFinder;

public abstract class Unit extends Affectable implements UnitDefaults {

    protected final Game game;

    protected final OwnerProperty ownerProp;

    protected final PositionProperty posProp;

    protected final HealthProperty healthProp;

    protected final MovingProperty movingProp;

    protected final StunnedProperty stunnedProp;

    protected final AbilityProperty abilityProp;

    public Unit(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	this.game = game;

	ownerProp = new OwnerProperty(this, playerOwner);
	posProp = new PositionProperty(this, coor, directionFacing);
	healthProp = new HealthProperty(this, getDefaultHealth(), getDefaultArmor());
	stunnedProp = new StunnedProperty(this, false);
	movingProp = new MovingProperty(this, getDefaultMoveRange());
	abilityProp = getDefaultAbilityProperty();
    }

    public Game getGame() {
	return game;
    }

    public OwnerProperty getOwnerProp() {
	return ownerProp;
    }

    public PositionProperty getPosProp() {
	return posProp;
    }

    public HealthProperty getHealthProp() {
	return healthProp;
    }

    public StunnedProperty getStunnedProp() {
	return stunnedProp;
    }

    public MovingProperty getMovingProp() {
	return movingProp;
    }

    public AbilityProperty getAbilityProp() {
	return abilityProp;
    }

    public void runOnStart() {
    }

    public void runOnDeath() {
    }

    public boolean isInRangeOfWalking(Coordinate moveToCoor) {
	return Board.walkDist(moveToCoor, posProp.getCurrentPropertyValue()) <= movingProp.getCurrentPropertyValue();
    }

    // TODO make sure all units should consider overriding these methods
    public Path getPathTo(Coordinate moveToCoor) {
	if (!(movingProp.canCurrentlyMove() && isInRangeOfWalking(moveToCoor))) {
	    return null;
	}
	return PathFinder.getPath(this, moveToCoor);
    }

    public static boolean areAllies(Unit unit1, Unit unit2) {
	return unit1.ownerProp.getTeam().equals(unit2.ownerProp.getTeam());
    }

}