package game.unit;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Path;
import game.board.PathFinder;
import game.board.Square;
import game.interaction.effect.Affectable;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Archer;
import game.unit.listofunits.Cleric;
import game.unit.listofunits.DarkMagicWitch;
import game.unit.listofunits.Guardian;
import game.unit.listofunits.Hunter;
import game.unit.listofunits.LightMagicWitch;
import game.unit.listofunits.Lightningmancer;
import game.unit.listofunits.Pyromancer;
import game.unit.listofunits.Scout;
import game.unit.listofunits.Warrior;
import game.unit.property.HealthProperty;
import game.unit.property.MovingProperty;
import game.unit.property.OwnerProperty;
import game.unit.property.PositionProperty;
import game.unit.property.StunnedProperty;
import game.unit.property.WaitProperty;
import game.unit.property.ability.AbilityProperty;
import game.unit.property.ability.ActiveAbilityProperty;

public abstract class Unit extends Affectable {

    // TODO go through and document EVERYTHING
    // TODO go through and determine visibility of ALL members in every class.
    @SuppressWarnings("unchecked")
    public static final Class<? extends Unit>[] UNITCLASSES = (Class<? extends Unit>[]) new Class<?>[] {
	    Aquamancer.class, Archer.class, Cleric.class, DarkMagicWitch.class, Guardian.class, Hunter.class,
	    LightMagicWitch.class, Lightningmancer.class, Pyromancer.class, Scout.class, Warrior.class };

    private final Game game;

    private final OwnerProperty ownerProp;

    private final PositionProperty posProp;

    private final HealthProperty healthProp;

    private final MovingProperty movingProp;

    private final AbilityProperty abilityProp;

    private final StunnedProperty stunnedProp;

    private final IncidentReporter deathReporter;

    private final UnitStat defaultStat;

    public Unit(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	this.game = game;

	defaultStat = UnitDefaults.getStat(this.getClass());

	ownerProp = new OwnerProperty(this, playerOwner);
	posProp = new PositionProperty(this, coor, directionFacing);
	healthProp = new HealthProperty(this, defaultStat.defaultHealth, defaultStat.defaultArmor);
	movingProp = new MovingProperty(this, defaultStat.defaultMoveRange, defaultStat.canDefaultTeleport);
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

    public abstract AbilityProperty getDefaultAbilityProperty();

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

    public UnitStat getDefaultStat() {
	return defaultStat;
    }

    public void runOnStart() {
    }

    public void triggerDeath() {
	deathReporter.reportIncident(this);
    }

    public void moveTakePath(Path path, Object sourceOfMovement) {
	Direction newdirFacing = null;
	try {
	    newdirFacing = Coordinate.inGeneralDirection(path.getPreviousPath().getEndCoor(), path.getEndCoor());
	} catch (Exception e) {
	    newdirFacing = Coordinate.inGeneralDirection(getPosProp().getCurrentPropertyValue(), path.getEndCoor());
	}
	getPosProp().setPropertyValue(path.getEndCoor(), sourceOfMovement);
	getPosProp().getDirFacingProp().setPropertyValue(newdirFacing, path);
    }

    public void useAbility(Square sqr) {
	((ActiveAbilityProperty) getAbilityProp()).performAbility(sqr);
	Direction newdirFacing = Coordinate.inGeneralDirection(getPosProp().getCurrentPropertyValue(), sqr.getCoor());
	getPosProp().getDirFacingProp().setPropertyValue(newdirFacing, getAbilityProp());
    }

    // TODO make sure all units should consider overriding these methods
    public Path getGamePathTo(Coordinate moveToCoor) {
	if (!movingProp.canCurrentlyMove() || !movingProp.isInRangeOfWalking(moveToCoor)) {
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