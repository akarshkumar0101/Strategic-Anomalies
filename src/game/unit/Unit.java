package game.unit;

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Path;
import game.board.PathFinder;
import game.interaction.effect.Affectable;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.property.HealthProperty;
import game.unit.property.MovingProperty;
import game.unit.property.OwnerProperty;
import game.unit.property.PositionProperty;
import game.unit.property.StunnedProperty;
import game.unit.property.UnitDefaults;
import game.unit.property.WaitProperty;
import game.unit.property.ability.AbilityProperty;
import game.unit.property.ability.ActiveAbilityProperty;

public abstract class Unit extends Affectable implements UnitDefaults {

    // TODO go through and document EVERYTHING
    // TODO go through and determine visibility of ALL members in every class.

    private final Game game;

    private final OwnerProperty ownerProp;

    private final PositionProperty posProp;

    private final HealthProperty healthProp;

    private final MovingProperty movingProp;

    private final AbilityProperty abilityProp;

    private final StunnedProperty stunnedProp;

    private final IncidentReporter deathReporter;

    public Unit(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	this.game = game;

	ownerProp = new OwnerProperty(this, playerOwner);
	posProp = new PositionProperty(this, coor, directionFacing);
	healthProp = new HealthProperty(this, getDefaultHealth(), getDefaultArmor());
	movingProp = new MovingProperty(this, getDefaultMoveRange(), canDefaultTeleport());
	abilityProp = getDefaultAbilityProperty();
	stunnedProp = new StunnedProperty(this, false);

	deathReporter = new IncidentReporter() {
	    @Override
	    public void add(IncidentListener listener, boolean onlyOnce) {
		super.add(listener, true);
	    }
	};
	// TODO add the inital wait time if going first etc.
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

    public MovingProperty getMovingProp() {
	return movingProp;
    }

    public AbilityProperty getAbilityProp() {
	return abilityProp;
    }

    public StunnedProperty getStunnedProp() {
	return stunnedProp;
    }

    public WaitProperty getWaitProp() {
	return abilityProp.isActiveAbility() ? ((ActiveAbilityProperty) abilityProp).getWaitProp() : null;
    }

    public IncidentReporter getDeathReporter() {
	return deathReporter;
    }

    public void runOnStart() {
    }

    public void triggerDeath() {
	deathReporter.reportIncident(this);
    }

    public boolean isInRangeOfWalking(Coordinate moveToCoor) {
	return Board.walkDist(moveToCoor, posProp.getCurrentPropertyValue()) <= movingProp.getCurrentPropertyValue();
    }

    // TODO make sure all units should consider overriding these methods
    public Path getPathTo(Coordinate moveToCoor) {
	if (!(movingProp.canCurrentlyMove() && isInRangeOfWalking(moveToCoor))) {
	    return null;
	} else if (movingProp.getTeleportingProp().getCurrentPropertyValue()) {
	    return PathFinder.getTeleportedPath(this, moveToCoor);
	} else {
	    return PathFinder.getPath(this, moveToCoor);
	}
    }

    public static boolean areAllies(Unit unit1, Unit unit2) {
	if (unit1 == null || unit2 == null) {
	    return false;
	}
	return unit1.ownerProp.getTeam().equals(unit2.ownerProp.getTeam());
    }

}